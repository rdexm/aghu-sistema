package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.faturamento.vo.FatProcedAmbRealizadosVO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalarId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimentoId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterProcedimentoAmbulatorialPaginatorController extends ActionController implements ActionPaginator {

	private static final String BLOCOCIRURGICO_REGISTRO_CIRURGIA_REALIZADA = "blococirurgico-registroCirurgiaRealizada";

	/**
	 * 
	 */
	private static final long serialVersionUID = -2412909226174301008L;

	private static final Log LOG = LogFactory.getLog(ManterProcedimentoAmbulatorialPaginatorController.class);

	private Log getLog() {
		return LOG;
	}
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@Inject @Paginator
	private DynamicDataModel<FatProcedAmbRealizadosVO> dataModel;

	private FatProcedAmbRealizadosVO selected;

	@Inject
	private ManterProcedimentoAmbulatorialController controller;
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;

	private DominioSituacaoProcedimentoAmbulatorio situacao;

	// Consulta
	private Integer conNumero;

	// Exame
	private Integer soeSeq;

	private Short seqp;

	// Cirurgia
	private Integer grcSeq;

	private Integer eprPciSeq;

	private Short eprEspSeq;

	private Long procedimentoAmbSeq;

	private DominioOrigemProcedimentoAmbulatorialRealizado origem;

	// Suggestions
	private FatCompetencia competencia;

	private AghUnidadesFuncionais unidadeFuncional;

	private Integer pacCodigoFonetica;

	private Integer pacProntuario;

	private Integer pacCodigo;

	private AipPacientes paciente;

	private VFatAssociacaoProcedimento procedimento;

	private Short cpgCphCspCnvCodigo;

	private Short cpgGrcSeq;

	private Byte cpgCphCspSeq;

	private Long seqEdicao;

	// Convenio / Plano para FatProcedAmbRealizado
	private Short convenioId;

	private Byte planoId;

	private FatConvenioSaudePlano convenioSaudePlano;

	private String voltarParaTela;
	private Date dataCompetencia;
	
	private List<Long> idProcedimentoNotaConsumo;
	
	public enum ManteProcedimentoHospitalarInternoPaginatorControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISAR_PROCEDIMENTO_AMBULATORIAL_INFORME_UM_CAMPO, MENSAGEM_PESQUISAR_PROCEDIMENTO_AMBULATORIAL_COMPETENCIA_OBRIGATORIA, MENSAGEM_PESQUISAR_PROCEDIMENTO_AMBULATORIAL_INFORME_MAIS_UM_CAMPO
	}

	@PostConstruct
	protected void init() {
		begin(conversation, true);
		inicio();
	}

	public void inicio() {
	 

		
		if(dataCompetencia != null){
			Calendar cDataCompetencia = Calendar.getInstance();
			cDataCompetencia.setTime(dataCompetencia);
			int mes = cDataCompetencia.get(Calendar.MONTH);
			competencia  = faturamentoFacade.obterCompetenciaModuloMesAno(DominioModuloCompetencia.AMB,
					++mes, cDataCompetencia.get(Calendar.YEAR));
		}

		if (competencia == null) {
			List<FatCompetencia> competencias = this.pesquisarCompetencias(null);
			if (competencias != null && !competencias.isEmpty()) {
				final BeanComparator sorter = new BeanComparator(FatCompetencia.Fields.IND_SITUACAO.toString(), new NullComparator(false));
				Collections.sort(competencias, sorter);
				if (DominioSituacaoCompetencia.A.equals(competencias.get(0).getIndSituacao())) {
					competencia = competencias.get(0);
				}
			}
		}
		
		if (convenioSaudePlano == null){
			convenioSaudePlano = new FatConvenioSaudePlano();
		}
		
		if (procedimento == null){
			procedimento = new VFatAssociacaoProcedimento();
			procedimento.setId(new VFatAssociacaoProcedimentoId());
		}
		
		if(unidadeFuncional==null){
			unidadeFuncional = new AghUnidadesFuncionais();
		}
			
		if (situacao == null) {
			situacao = DominioSituacaoProcedimentoAmbulatorio.ABERTO;
		}

		if (cpgCphCspCnvCodigo == null) {
			createParametrosFind();
		}

		inicializaPaciente();
		
		if(voltarParaTela != null){
			this.dataModel.reiniciarPaginator();
		}
		
	
	}
	
	private void inicializaPaciente() {
		if (paciente == null || paciente.getCodigo() == null) {
			CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
			if (codPac != null && codPac.getCodigo() > 0) { 
				paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac.getCodigo());
				if (paciente != null) {
					pacProntuario = paciente.getProntuario();
					pacCodigo = paciente.getCodigo();
				}
			}
		} else if (pacCodigo != null) {
			obterPacientePorCodigo();

		} else if (pacProntuario != null) {
			obterPacientePorProntuario();
		}
	}

	public String editar(final Long seq) {
		controller.setCpgCphCspCnvCodigo(cpgCphCspCnvCodigo);
		controller.setCpgGrcSeq(cpgGrcSeq);
		controller.setCpgCphCspSeq(cpgCphCspSeq);

		controller.setPacCodigo(pacCodigo);

		controller.setSeq(seq);

		controller.inicio();
		return "manterProcedimentoAmbulatorial";
	}
	
	public String voltar(){
		
		if(voltarParaTela != null){
			if(voltarParaTela.equalsIgnoreCase("notaConsumoCirurgia")){
				return BLOCOCIRURGICO_REGISTRO_CIRURGIA_REALIZADA;
			}
		}
		
		return voltarParaTela;
	}

	public String novo() {

		controller.setCpgCphCspCnvCodigo(cpgCphCspCnvCodigo);
		controller.setCpgGrcSeq(cpgGrcSeq);
		controller.setCpgCphCspSeq(cpgCphCspSeq);

		controller.setPacCodigo(pacCodigo);
		if (procedimento != null && procedimento.getId() != null) {
			controller.setPhiSeq(procedimento.getId().getPhiSeq());
		} else {
			controller.setPhiSeq(null);
		}
		controller.setProcedimento(procedimento);

		controller.inicio();
		return "manterProcedimentoAmbulatorial";
	}

	private void createParametrosFind() {
		try {
			final AghParametros planoSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
			final AghParametros convenioSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
			final AghParametros grupoSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);

			cpgCphCspCnvCodigo = convenioSUS.getVlrNumerico().shortValue();
			cpgCphCspSeq = planoSUS.getVlrNumerico().byteValue();
			cpgGrcSeq = grupoSUS.getVlrNumerico().shortValue();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			getLog().error(e);
		}
	}

	public void pesquisar() {
		if (pacProntuario == null && pacCodigo != null) {
			obterPacientePorCodigo();

		} else if (pacProntuario != null && pacCodigo == null) {
			obterPacientePorProntuario();
		}

		String msgErroValidacao = validarPreenchimentoCampos();

		if (msgErroValidacao == null) {
			this.dataModel.reiniciarPaginator();
		} else {
			apresentarMsgNegocio(Severity.ERROR, msgErroValidacao);
		}
	}

	public void limpar() {
		conNumero = null;
		situacao = null;

		soeSeq = null;
		seqp = null;

		grcSeq = null;
		eprPciSeq = null;
		eprEspSeq = null;

		competencia = null;
		procedimento = null;

		unidadeFuncional = null;
		procedimentoAmbSeq = null;
		origem = null;
		convenioSaudePlano = null;
		convenioId = null;
		planoId = null;

		limparPaciente();

		this.dataModel.setPesquisaAtiva(false);
	}

	private void limparPaciente() {
		pacProntuario = null;
		pacCodigo = null;
		pacCodigoFonetica = null;
		paciente = null;
	}

	private String validarPreenchimentoCampos() {
		String msgKey = null;
		if (conNumero == null && soeSeq == null && seqp == null && grcSeq == null && eprPciSeq == null && eprEspSeq == null
				&& procedimento == null && unidadeFuncional == null && procedimentoAmbSeq == null && origem == null
				&& convenioSaudePlano == null && paciente == null) {
			msgKey = ManteProcedimentoHospitalarInternoPaginatorControllerExceptionCode.MENSAGEM_PESQUISAR_PROCEDIMENTO_AMBULATORIAL_INFORME_MAIS_UM_CAMPO
					.toString();
		} else if (competencia == null
				&& (unidadeFuncional != null || procedimento != null || situacao != null || origem != null || convenioSaudePlano != null)) {
			msgKey = ManteProcedimentoHospitalarInternoPaginatorControllerExceptionCode.MENSAGEM_PESQUISAR_PROCEDIMENTO_AMBULATORIAL_COMPETENCIA_OBRIGATORIA
					.toString();
		}
		return msgKey;
	}

	public void atribuirPlano() {
		if (getConvenioSaudePlano() != null) {
			this.convenioId = getConvenioSaudePlano().getConvenioSaude().getCodigo();
			this.planoId = getConvenioSaudePlano().getId().getSeq();
		} else {
			this.convenioId = null;
			this.planoId = null;
		}
	}

	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			setConvenioSaudePlano(this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId));
		}
	}

	// Suggestion Convênio
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String) filtro);
	}

	// Suggestion Convênio
	public Long pesquisarConvenioSaudePlanosCount(String filtro) {
		return faturamentoApoioFacade.pesquisarConvenioSaudePlanosCount((String) filtro);
	}

	// Suggestion Unidade Funcional
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(final String strPesquisa) {
		return this.aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(strPesquisa);
	}

	// Suggestion Unidade Funcional
	public Long pesquisarUnidadeFuncionalCount(final String strPesquisa) {
		return aghuFacade.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasCount(strPesquisa);
	}

	@Override
	public Long recuperarCount() {
		if (cpgCphCspCnvCodigo == null) {
			createParametrosFind();
		}
		Short unfSeq = obterUnfSeq(unidadeFuncional);

		return faturamentoFacade.listarFatProcedAmbRealizadoCount(competencia, paciente,
				(procedimento != null ? procedimento.getProcedimentoHospitalarInterno() : null), new AacConsultaProcedHospitalar(
						new AacConsultaProcedHospitalarId(conNumero, null)), new AelItemSolicitacaoExames(new AelItemSolicitacaoExamesId(
						soeSeq, seqp)), new MbcProcEspPorCirurgias(new MbcProcEspPorCirurgiasId(grcSeq, eprPciSeq,
						eprEspSeq, null)), situacao, cpgCphCspCnvCodigo, cpgGrcSeq, cpgCphCspSeq, unfSeq, procedimentoAmbSeq, origem,
				convenioId, planoId);
	}

	@Override
	public List<FatProcedAmbRealizadosVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if (cpgCphCspCnvCodigo == null) {
			createParametrosFind();
		}
		Short unfSeq = obterUnfSeq(unidadeFuncional);

		return faturamentoFacade.listarFatProcedAmbRealizado(competencia, paciente,
				(procedimento != null ? procedimento.getProcedimentoHospitalarInterno() : null), new AacConsultaProcedHospitalar(
						new AacConsultaProcedHospitalarId(conNumero, null)), new AelItemSolicitacaoExames(new AelItemSolicitacaoExamesId(
						soeSeq, seqp)), new MbcProcEspPorCirurgias(new MbcProcEspPorCirurgiasId(grcSeq, eprPciSeq,
						eprEspSeq, null)), situacao, cpgCphCspCnvCodigo, cpgGrcSeq, cpgCphCspSeq, unfSeq, procedimentoAmbSeq, origem,
				convenioId, planoId, firstResult, maxResult, orderProperty, asc);
	}

	// Suggestion competência
	public List<FatCompetencia> pesquisarCompetencias(Object objPesquisa) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(faturamentoFacade.getCompetenciaId(objPesquisa,
					DominioModuloCompetencia.AMB));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatCompetencia>(0);
		}
	}

	// Suggestion competência
	public Long pesquisarCompetenciasCount(Object objPesquisa) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(faturamentoFacade.getCompetenciaId(objPesquisa,
					DominioModuloCompetencia.AMB));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	private Short obterUnfSeq(AghUnidadesFuncionais unidadeFuncional) {
		if (unidadeFuncional != null) {
			return unidadeFuncional.getSeq();
		} else {
			return null;
		}
	}

	public void obterPacientePorProntuario() {
		final Integer filtro = pacProntuario;

		if (filtro != null) {
			paciente = pacienteFacade.obterPacientePorProntuario(filtro);

			if (paciente != null) {
				pacProntuario = paciente.getProntuario();
				pacCodigo = paciente.getCodigo();
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PACIENTE_PRONTUARIO_NAO_ENCONTRADO");
			}
		} else {
			limparPaciente();
		}
	}

	public void obterPacientePorCodigo() {
		final Integer filtro = pacCodigo;

		if (filtro != null) {
			paciente = pacienteFacade.obterNomePacientePorCodigo(filtro);
			if (paciente != null) {
				pacProntuario = paciente.getProntuario();
				pacCodigo = paciente.getCodigo();

			} else {
				apresentarMsgNegocio("FAT_00731");
			}
		} else {
			limparPaciente();
		}
	}

	public String redirecionarPesquisaFonetica() {
		limparPaciente();
		return "paciente-pesquisaPacienteComponente";
	}

	public List<VFatAssociacaoProcedimento> listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(final String strPesquisa) {
		try {
			return faturamentoFacade.listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricao(strPesquisa, cpgCphCspCnvCodigo, cpgGrcSeq,
					cpgCphCspSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public Long listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(final String strPesquisa) {
		try {
			return faturamentoFacade.listarAssociacaoProcedimentoPorPhiSeqCodSusOuDescricaoCount(strPesquisa, cpgCphCspCnvCodigo,
					cpgGrcSeq, cpgCphCspSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public VFatAssociacaoProcedimento getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(VFatAssociacaoProcedimento procedimento) {
		this.procedimento = procedimento;
	}

	public Short getCpgCphCspCnvCodigo() {
		return cpgCphCspCnvCodigo;
	}

	public void setCpgCphCspCnvCodigo(Short cpgCphCspCnvCodigo) {
		this.cpgCphCspCnvCodigo = cpgCphCspCnvCodigo;
	}

	public Short getCpgGrcSeq() {
		return cpgGrcSeq;
	}

	public void setCpgGrcSeq(Short cpgGrcSeq) {
		this.cpgGrcSeq = cpgGrcSeq;
	}

	public Byte getCpgCphCspSeq() {
		return cpgCphCspSeq;
	}

	public void setCpgCphCspSeq(Byte cpgCphCspSeq) {
		this.cpgCphCspSeq = cpgCphCspSeq;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getGrcSeq() {
		return grcSeq;
	}

	public void setGrcSeq(Integer grcSeq) {
		this.grcSeq = grcSeq;
	}

	public Integer getEprPciSeq() {
		return eprPciSeq;
	}

	public void setEprPciSeq(Integer eprPciSeq) {
		this.eprPciSeq = eprPciSeq;
	}

	public Short getEprEspSeq() {
		return eprEspSeq;
	}

	public void setEprEspSeq(Short eprEspSeq) {
		this.eprEspSeq = eprEspSeq;
	}

	public DominioSituacaoProcedimentoAmbulatorio getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoProcedimentoAmbulatorio situacao) {
		this.situacao = situacao;
	}

	public Long getSeqEdicao() {
		return seqEdicao;
	}

	public void setSeqEdicao(Long seqEdicao) {
		this.seqEdicao = seqEdicao;
	}

	public Long getProcedimentoAmbSeq() {
		return procedimentoAmbSeq;
	}

	public void setProcedimentoAmbSeq(Long procedimentoAmbSeq) {
		this.procedimentoAmbSeq = procedimentoAmbSeq;
	}

	public DominioOrigemProcedimentoAmbulatorialRealizado getOrigem() {
		return origem;
	}

	public void setOrigem(DominioOrigemProcedimentoAmbulatorialRealizado origem) {
		this.origem = origem;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return convenioSaudePlano;
	}

	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	public FatProcedAmbRealizadosVO getSelected() {
		return selected;
	}

	public void setSelected(FatProcedAmbRealizadosVO selected) {
		this.selected = selected;
	}

	public DynamicDataModel<FatProcedAmbRealizadosVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatProcedAmbRealizadosVO> dataModel) {
		this.dataModel = dataModel;
	}
	
	public List<Long> getIdProcedimentoNotaConsumo() {
		return idProcedimentoNotaConsumo;
	}

	public void setIdProcedimentoNotaConsumo(
			List<Long> idProcedimentoNotaConsumo) {
		this.idProcedimentoNotaConsumo = idProcedimentoNotaConsumo;
	}

	public String getVoltarParaTela() {
		return voltarParaTela;
	}

	public void setVoltarParaTela(String voltarParaTela) {
		this.voltarParaTela = voltarParaTela;
	}

	public Date getDataCompetencia() {
		return dataCompetencia;
	}

	public void setDataCompetencia(Date dataCompetencia) {
		this.dataCompetencia = dataCompetencia;
	}

}