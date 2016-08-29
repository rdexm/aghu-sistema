package br.gov.mec.aghu.faturamento.action;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioMensagemEstornoAIH;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAih;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ListarContasHospPacientesPorPacCodigoVO;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatContasHospPacientes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * @author aghu
 *
 */

public class InformarSolicitadoContaHospitalarPaginatorController extends ActionController implements ActionPaginator {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final long serialVersionUID = 1895991331461304587L;
	
	private final String PAGE_FROM_PESQUISA_CH = "pesquisarContasHospitalaresParaInformarSolicitado";
	
	private static final Log LOG = LogFactory.getLog(InformarSolicitadoContaHospitalarPaginatorController.class);

	@Inject @Paginator
	private DynamicDataModel<ListarContasHospPacientesPorPacCodigoVO> dataModel;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;	
		
	@Inject
	private EncerramentoPreviaContaController encerramentoPreviaContaController;

	private Integer cthSeq;
	
	private Integer pacCodigo;
	
	private BigDecimal intSeq;
	
	private String from;

	private Short tipoGrupoContaSUS;

	private VFatContasHospPacientes contaHospitalar;

	private VFatAssociacaoProcedimento procedimentoSolicitado;

	private Long numeroAIH;
	
	private Date dataHoraEmissao;

	private RapServidores auditor;

	private Boolean exibirModal;
	
	private String mensagemEstornoAIH;

	private Boolean exibirModalConfirmacao;
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	@PostConstruct
	public void init() {
		begin(conversation);
		
		try {
			tipoGrupoContaSUS = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico()
					.shortValue();
		} catch (ApplicationBusinessException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_CARREGAR_TIPO_GRUPO_CONTA_SUS");
		}
	}

	public String inicio() {		
		
		this.exibirModal = false;
		this.exibirModalConfirmacao = false;

		if (this.cthSeq != null) {
			try {
				this.contaHospitalar = this.faturamentoFacade.obterVFatContasHospPacientes(this.cthSeq, this.intSeq);
				this.dataModel.reiniciarPaginator();
				this.eventPostQuery();
			} catch (Exception e) {
				LOG.debug("Exceção ignorada. " +  e.getMessage());
			}
		}

		if (this.contaHospitalar == null) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_CARREGAR_CONTA_HOSPITALAR",
					String.valueOf(this.cthSeq));

			return this.from;
		}

		return null;
	}

	@Override
	public Long recuperarCount() {
		if (this.contaHospitalar != null) {
			try {
				return faturamentoFacade.listarContasHospPacientesPorPacCodigoCount(this.contaHospitalar.getPacCodigo());
			} catch (BaseException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				apresentarExcecaoNegocio(e);
			}
		}
		
		return 0L;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ListarContasHospPacientesPorPacCodigoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		if (this.contaHospitalar != null) {
			try {
				List<ListarContasHospPacientesPorPacCodigoVO> lista = this.faturamentoFacade.listarContasHospPacientesPorPacCodigo(
						this.contaHospitalar.getPacCodigo(), this.tipoGrupoContaSUS, firstResult, maxResult, orderProperty, asc);

				if (lista != null) {
					return lista;
				}
			} catch (BaseException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				LOG.error("Erro ao carregar VFatContasHospPacientes", e);
			}
		}

		return new ArrayList<ListarContasHospPacientesPorPacCodigoVO>(0);
	}

	private void eventPostQuery() {
		Date dtIntAdministrativa = this.contaHospitalar.getCthDtIntAdministrativa();
		Date dtAltaAdministrativa = this.contaHospitalar.getCthDtAltaAdministrativa();
		if (dtIntAdministrativa != null && dtAltaAdministrativa != null
				&& DateValidator.validarMesmoDia(dtIntAdministrativa, dtAltaAdministrativa)) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONTA_POSSUI_DATA_INTERNACAO_ALTA_IGUAIS",
					String.valueOf(this.cthSeq));
		}

		FatAih aih = this.contaHospitalar.getAih();
		if (aih != null) {
			this.numeroAIH = aih.getNroAih();
			this.dataHoraEmissao = aih.getDthrEmissao();

			if (aih.getSerMatricula() != null && aih.getSerVinCodigo() != null) {
				try {
					this.auditor = this.faturamentoFacade.obterMedicoAuditor(aih.getServidor());
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			} else {
				this.auditor = null;
			}

			// Habilitar o botão imprimir
			LOG.debug("O botão imprimir deverá estar habilitado, quando essa hisória for implementada.");
		} else {
			this.numeroAIH = null;
			this.dataHoraEmissao = null;
			this.auditor = null;

			// Desabilitar o botão imprimir
			LOG.debug("O botão imprimir deverá estar desabilitado, quando essa hisória for implementada.");
		}

		List<Date> datasTransplantes = this.faturamentoFacade.listarDatasTransplantes(this.contaHospitalar.getPacCodigo());
		if (datasTransplantes != null && !datasTransplantes.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PACIENTE_REALIZOU_TRANSPLANTE",
					DATE_FORMAT.format(datasTransplantes.get(0)));
		}

		this.procedimentoSolicitado = null;
		if (this.contaHospitalar.getCthPhiSeq() != null) {
			List<VFatAssociacaoProcedimento> listaAssociacoesProcedimentos = this.faturamentoFacade.pesquisarAssociacoesProcedimentos(
					null, this.contaHospitalar.getCthPhiSeq(), true, this.contaHospitalar.getCthCspCnvCodigo(),
					this.contaHospitalar.getCthCspSeq(), DominioSituacao.A, DominioSituacao.A, this.tipoGrupoContaSUS, null);
			if (listaAssociacoesProcedimentos != null && !listaAssociacoesProcedimentos.isEmpty()) {
				this.procedimentoSolicitado = listaAssociacoesProcedimentos.get(0);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_MOTIVO_SAIDA_PACIENTE_JA_INFORMADO");
			}
		}
	}

	// ## SUGGESTION BOX ##
	public List<VFatAssociacaoProcedimento> pesquisarAssociacoesProcedimentos(String filtro) {
		return this.returnSGWithCount(this.faturamentoFacade
				.pesquisarAssociacoesProcedimentos(filtro, null, true,
						this.contaHospitalar.getCthCspCnvCodigo(),
						this.contaHospitalar.getCthCspSeq(), DominioSituacao.A,
						DominioSituacao.A, this.tipoGrupoContaSUS, null),
				this.faturamentoFacade.pesquisarAssociacoesProcedimentosCount(
						filtro, null, true,
						this.contaHospitalar.getCthCspCnvCodigo(),
						this.contaHospitalar.getCthCspSeq(), DominioSituacao.A,
						DominioSituacao.A, this.tipoGrupoContaSUS, null));
	}

	public Long pesquisarAuditoresCount(String filtro) {
		try {
			return this.faturamentoFacade.pesquisarAuditoresCount((String) filtro);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return 0L;
	}

	public List<RapServidores> pesquisarAuditores(String filtro) {
		try {
			return this.faturamentoFacade.pesquisarAuditores((String) filtro);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}

	public void estornarAIHReaproveitarAIH() {
		this.estornarAIH(true);
	}
	
	public void estornarAIHSemReaproveitarAIH() {
		this.estornarAIH(false);
	}
	
	private void estornarAIH(boolean reaproveitarAIH){
		try {			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			final Date dataFimVinculoServidor = new Date();
			this.faturamentoFacade.estornarAIH(this.contaHospitalar.getCthSeq(), this.numeroAIH, reaproveitarAIH, 
											   nomeMicrocomputador, dataFimVinculoServidor);
			this.faturamentoFacade.clear();
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AIH_ESTORNADA", String.valueOf(this.numeroAIH));

			// Atualiza as informações na tela
			this.dataModel.reiniciarPaginator();
			//this.pesquisarContasHospitalaresParaInformarSolicitado.getDataModel().reiniciarPaginator();
			this.inicio();
		} catch (BaseException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarEstornoAIH(){
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AIH_NAO_ESTORNADA");
	}
	
	public void validarEstornoAIH() {
		if(numeroAIH != null){
			try {

				
				final DominioMensagemEstornoAIH result = faturamentoFacade.validaEstornoAIH(contaHospitalar.getCthSeq(), numeroAIH);
				
				if(DominioMensagemEstornoAIH.I.equals(result) || DominioMensagemEstornoAIH.N.equals(result)){
					mensagemEstornoAIH = "AIH '" + numeroAIH + "'" + result.getDescricao();
					
				} else {
					mensagemEstornoAIH = result.getDescricao(); 
				}
				
				openDialog("modalEstornoAIHWG");
				
			} catch (ApplicationBusinessException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				apresentarExcecaoNegocio(e);
			}
		} else {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AIH_NAO_ASSOCIA_CTH");
		}
	}
	
	public void validarInformarSolicitado() {
		this.exibirModal = false;
		
		if(numeroAIH != null && auditor == null){
			this.apresentarMsgNegocio(Severity.ERROR, "AIH_SEM_AUDITOR");
			return;
		}
		
		List<FatContasInternacao> contasInternacao = this.faturamentoFacade.listarContasInternacao(this.contaHospitalar.getCthSeq());

		Integer vIntSeq = null;
		if (contasInternacao != null && !contasInternacao.isEmpty() && contasInternacao.get(0).getInternacao() != null) {
			vIntSeq = contasInternacao.get(0).getInternacao().getSeq();
		}

		DominioSituacaoConta[] situacoes = new DominioSituacaoConta[] { DominioSituacaoConta.E, DominioSituacaoConta.F };

		Calendar cal = Calendar.getInstance();
		cal.setTime(this.contaHospitalar.getCthDtIntAdministrativa());
		cal.add(Calendar.DAY_OF_MONTH, -3);

		Long count = this.faturamentoFacade.validarInformarSolicitado(DominioGrupoConvenio.S, this.contaHospitalar.getCthSeq(),
				this.contaHospitalar.getPacProntuario(), vIntSeq, situacoes, cal.getTime(), new Date());
		if (count != null && count > 0) {
			this.exibirModal = true;
		}
		
		if (!this.exibirModal) {
			this.validarInformarSolicitado2();
		}
	}
	
	public void validarInformarSolicitado2() {
		
		try {
			this.exibirModalConfirmacao = false;
			
			if (this.numeroAIH != null) {
				if(faturamentoFacade.validaNumeroAIHInformadoManualmente(numeroAIH)){
					final FatAih fatAih = this.faturamentoFacade.obterFatAihPorChavePrimaria(this.numeroAIH);
					
					if (fatAih == null) {
						this.exibirModalConfirmacao = true;
					} else {
						if (!DominioSituacaoAih.U.equals(fatAih.getIndSituacao())) {
							this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_AIH_NAO_PODE_SER_ASSOCIADO");
							return;
						}
					}
				} else {
					this.apresentarMsgNegocio(Severity.ERROR, "AIH_INVALIDO");
					return;
				}
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		if (!this.exibirModalConfirmacao) {
			this.informarSolicitado();
		}
	}
	
	public void informarSolicitado() {
		this.exibirModal = false;
		this.exibirModalConfirmacao = false;
		
		if(LOG.isDebugEnabled()){
			LOG.debug("numeroAIH: " + numeroAIH);
			LOG.debug("dataHoraEmissao: " + dataHoraEmissao);
			LOG.debug("procedimentoSolicitado: " + (procedimentoSolicitado != null ? procedimentoSolicitado.getId().getPhiDescricao()
					: ""));
			LOG.debug("auditor: " + auditor);
		}

		try {			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			final Date dataFimVinculoServidor = new Date();			
			this.faturamentoFacade.informarSolicitado(this.contaHospitalar, this.procedimentoSolicitado, this.numeroAIH, this.dataHoraEmissao, this.auditor, nomeMicrocomputador, dataFimVinculoServidor);
			this.faturamentoFacade.clear();

			this.apresentarMsgNegocio(Severity.INFO, "INFORMAR_SOLICITADO_CONCLUIDO_SUCESSO");

			// Atualiza as informações na tela
			this.dataModel.reiniciarPaginator();
			//this.pesquisarContasHospitalaresParaInformarSolicitado.getDataModel().reiniciarPaginator();
			this.inicio();
		} catch (BaseException e) {
			LOG.error("Exceção capturada.", e);
			apresentarExcecaoNegocio(e);
		}
	}

	public String voltar() {
		return from != null ? from : PAGE_FROM_PESQUISA_CH;
	}
	
	public String consultarInconsistencias() {
		return "faturamento-consultarInconsistencias";
	}
	
	public String previaContaHospitalar() {
		this.encerramentoPreviaContaController.setCthSeqSelected(this.cthSeq);
		String retorno = this.encerramentoPreviaContaController.previaContaHospitalar();
		
		return retorno;
	}

	public void reinternarContaHospitalar() {
		try {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			final Date dataFimVinculoServidor = new Date();
			this.faturamentoFacade.reinternarContaHospitalar(cthSeq, pacCodigo, nomeMicrocomputador, dataFimVinculoServidor);

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CONTA_HOSPITALAR_REINTERNADA", String.valueOf(cthSeq));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public VFatContasHospPacientes getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(VFatContasHospPacientes contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public VFatAssociacaoProcedimento getProcedimentoSolicitado() {
		return procedimentoSolicitado;
	}

	public void setProcedimentoSolicitado(VFatAssociacaoProcedimento procedimentoSolicitado) {
		this.procedimentoSolicitado = procedimentoSolicitado;
	}

	public Long getNumeroAIH() {
		return numeroAIH;
	}

	public void setNumeroAIH(Long numeroAIH) {
		this.numeroAIH = numeroAIH;
	}

	public Date getDataHoraEmissao() {
		return dataHoraEmissao;
	}

	public void setDataHoraEmissao(Date dataHoraEmissao) {
		this.dataHoraEmissao = dataHoraEmissao;
	}

	public RapServidores getAuditor() {
		return auditor;
	}

	public void setAuditor(RapServidores auditor) {
		this.auditor = auditor;
	}

	public Boolean getExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(Boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public String getMensagemEstornoAIH() {
		return mensagemEstornoAIH;
	}

	public void setMensagemEstornoAIH(String mensagemEstornoAIH) {
		this.mensagemEstornoAIH = mensagemEstornoAIH;
	}

	public Boolean getExibirModalConfirmacao() {
		return exibirModalConfirmacao;
	}

	public void setExibirModalConfirmacao(Boolean exibirModalConfirmacao) {
		this.exibirModalConfirmacao = exibirModalConfirmacao;
	}

	public BigDecimal getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(BigDecimal intSeq) {
		this.intSeq = intSeq;
	}

	public DynamicDataModel<ListarContasHospPacientesPorPacCodigoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<ListarContasHospPacientesPorPacCodigoVO> dataModel) {
		this.dataModel = dataModel;
	}	
}