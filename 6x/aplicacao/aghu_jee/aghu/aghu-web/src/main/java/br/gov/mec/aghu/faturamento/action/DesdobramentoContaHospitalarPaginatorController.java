package br.gov.mec.aghu.faturamento.action;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobr;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobrId;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatMotivoDesdobramento;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class DesdobramentoContaHospitalarPaginatorController extends ActionController implements ActionPaginator {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final long serialVersionUID = -1338296886773509412L;
	
	private static final Log LOG = LogFactory.getLog(DesdobramentoContaHospitalarPaginatorController.class);
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private VisualizarSugestoesDesdobramentoContaHospitalarPaginatorController visualizarSugestoesDesdobramentoContaHospitalarPaginatorController;

	@Inject
	private PesquisarContasHospitalaresParaDesdobramentoPaginatorController pesquisarContasHospitalaresParaDesdobramentoPaginatorController;
	
	private Integer cthSeq;

	private String from;

	private VFatContaHospitalarPac contaHospitalar;

	private VFatAssociacaoProcedimento procedimentoHospitalarSolicitado;

	private VFatAssociacaoProcedimento procedimentoHospitalarRealizado;

	private FatMotivoDesdobramento motivoDesdobramento;

	private Date dataHoraDesdobramento;

	private Boolean exibirModal;
	
	private Integer seqContaCriadaPeloDesdobramento;
	
	// Parametros passados da tela anterior, onde é criado 
	// a sugestão de desdobramento da conta (FatContaSugestaoDesdobr) 
	private Byte mdsSeq;
	private String strDthrSugestao;
	
	private final static String DATE_PATTERN_DDMMYYYY_HHMMSS = "dd/MM/yyyy HH:mm:ss";
	
	@Inject @Paginator
	private DynamicDataModel<FatItemContaHospitalar> dataModel;
	
	private AghParametros grupoSUS;
	
	private final static String REDIRECIONA_VISUALIZAR_DESDOBRAMENTO = "visualizarSugestoesDesdobramentoContaHospitalar";
	
	private final static String REDIRECIONA_PESQUISAR_CONTA_HOSP_DESDOBRAMENTO = "pesquisarContasHospitalaresParaDesdobramento";
	
	private final static String REDIRECIONA_DESDOBRAR_CONTA_HOSP_DESDOBRAMENTO = "desdobramentoContaHospitalar";
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public String inicio() {
	 

	
		this.dataModel.setPesquisaAtiva(true);
		this.exibirModal = false;
		
		Enum[] leftJoin = new Enum[] {VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO, 
				VFatContaHospitalarPac.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO_REALIZADO,
				VFatContaHospitalarPac.Fields.CONTA_HOSPITALAR,
				VFatContaHospitalarPac.Fields.PACIENTE,
				VFatContaHospitalarPac.Fields.MOTIVO_DESDOBRAMENTO,
				VFatContaHospitalarPac.Fields.CONTAS_HOSPITALARES,
				VFatContaHospitalarPac.Fields.TIPO_AIH};
		try {
			this.contaHospitalar = this.faturamentoFacade.obterContaHospitalarPaciente(this.cthSeq, leftJoin);

			grupoSUS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		} catch (Exception e) {
			LOG.error("Exceção ignorada.", e);
		}

		if (this.contaHospitalar == null) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_CARREGAR_CONTA_HOSPITALAR", this.cthSeq);

			return this.from;
		}

		if (this.contaHospitalar.getProcedimentoHospitalarInterno() != null) {
			try {
				List<VFatAssociacaoProcedimento> procedimento = this.faturamentoFacade
						.listarAssociacaoProcedimentoSUSSolicitado(this.contaHospitalar.getProcedimentoHospitalarInterno().getSeq());
				if (!procedimento.isEmpty()) {
					this.procedimentoHospitalarSolicitado = procedimento.get(0);
				}
			} catch (final BaseException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				this.procedimentoHospitalarSolicitado = new VFatAssociacaoProcedimento();
			}
		}

		if (this.contaHospitalar.getProcedimentoHospitalarInternoRealizado() != null) {
			try {
				List<VFatAssociacaoProcedimento> procedimento = this.faturamentoFacade.listarAssociacaoProcedimentoSUSRealizado(
						this.contaHospitalar.getProcedimentoHospitalarInternoRealizado().getSeq(), this.cthSeq);
				if (!procedimento.isEmpty()) {
					this.procedimentoHospitalarRealizado = procedimento.get(0);
				}
			} catch (final BaseException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				this.procedimentoHospitalarRealizado = new VFatAssociacaoProcedimento();
			}
		}

		if (this.contaHospitalar.getContaHospitalar().getContasHospitalares() != null
				&& !this.contaHospitalar.getContaHospitalar().getContasHospitalares().isEmpty()) {
			
			final FatContasHospitalares contaCriadaPeloEstorno = this.contaHospitalar.getContaHospitalar().getContasHospitalares().iterator().next();
			this.seqContaCriadaPeloDesdobramento = contaCriadaPeloEstorno.getSeq();
			this.dataHoraDesdobramento = contaCriadaPeloEstorno.getDataInternacaoAdministrativa();
		} else {
			this.seqContaCriadaPeloDesdobramento = null;
		}
		
		this.motivoDesdobramento = this.contaHospitalar.getContaHospitalar().getMotivoDesdobramento();
		
		Date dthrSugestao = null;
		try {
			if (!StringUtils.isEmpty(strDthrSugestao)) {
				dthrSugestao = converterStringParaData(strDthrSugestao);	
			}
		} catch (ParseException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		
		carregarCamposSugestaoDesdobramento(dthrSugestao);
		
		dataModel.reiniciarPaginator();
		
		return null;
	
	}
	
	private void carregarCamposSugestaoDesdobramento(Date dthrSugestao) {
		if (mdsSeq != null && cthSeq != null && dthrSugestao != null) {
			FatContaSugestaoDesdobrId contaSugestaoDesdobrId = new FatContaSugestaoDesdobrId(
					mdsSeq, cthSeq, dthrSugestao);
			FatContaSugestaoDesdobr contaSugestaoDesdobr = this.faturamentoFacade
					.obterFatContaSugestaoDesdobrPorChavePrimaria(contaSugestaoDesdobrId);

			if (contaSugestaoDesdobr != null) {
				this.dataHoraDesdobramento = contaSugestaoDesdobr.getId()
						.getDthrSugestao();
				this.motivoDesdobramento = contaSugestaoDesdobr
						.getMotivoDesdobramento();
			}			
		}
	}

	@Override
	public Long recuperarCount() {
		try {
			return faturamentoFacade.listarItensContaHospitalarCount(this.cthSeq, null, null, null, null, null, false, grupoSUS);
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	public String gerarSugestoesDesdobramento() {
		if (this.cthSeq != null) {
			try {
				if(this.faturamentoFacade.geraSugestaoDesdobramentoContaHospitalar(this.cthSeq)){
					apresentarMsgNegocio("MENSAGEM_DESDOBRAMENTO_CTH_SUCESSO");
					
					visualizarSugestoesDesdobramentoContaHospitalarPaginatorController.setOrigem(REDIRECIONA_DESDOBRAR_CONTA_HOSP_DESDOBRAMENTO);
					visualizarSugestoesDesdobramentoContaHospitalarPaginatorController.pesquisar();
					
					return REDIRECIONA_VISUALIZAR_DESDOBRAMENTO;
					
				} else {
					apresentarMsgNegocio("MENSAGEM_DESDOBRAMENTO_CTH_ERRO");
				}
			} catch (BaseException e) {
				LOG.error(EXCECAO_CAPTURADA, e);
				apresentarExcecaoNegocio(e);
			}
		}
		
		return null;
	}
	
	
	@Override
	public List<FatItemContaHospitalar> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		try {
			return faturamentoFacade.listarItensContaHospitalar(firstResult, maxResult, orderProperty, asc, this.cthSeq, null, null, null, null, null, false, grupoSUS);
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatItemContaHospitalar>();
		}
	}

	public Long pesquisarCountMotivosDesdobramentos(String filtro) {
		return this.faturamentoFacade.listarMotivosDesdobramentosCount((String) filtro,
				this.contaHospitalar.getTipoAih() != null ? this.contaHospitalar.getTipoAih().getSeq() : null);
	}

	public List<FatMotivoDesdobramento> pesquisarMotivosDesdobramentos(String filtro) {
		return  this.returnSGWithCount(this.faturamentoFacade.listarMotivosDesdobramentos((String) filtro,
				this.contaHospitalar.getTipoAih() != null ? this.contaHospitalar.getTipoAih().getSeq() : null),pesquisarCountMotivosDesdobramentos(filtro));
	}

	public void desdobrarContaConsiderarReapresentada() {
		this.desdobrarConta(true);
	}

	public void desdobrarContaNaoConsiderarReapresentada() {
		this.desdobrarConta(false);
	}

	public void desdobrarConta(Boolean contaConsideradaReapresentada) {
		this.exibirModal = false;

		if (motivoDesdobramento != null) {
			LOG.debug("motivoDesdobramento: " + this.motivoDesdobramento.getDescricao());
		}
		LOG.debug("dataHoraDesdobramento: " + this.dataHoraDesdobramento);
		LOG.debug("contaConsideradaReapresentada: " + contaConsideradaReapresentada);

		try {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.debug("Exceção caputada:", e);
			}
			Date dt = new Date();
			this.faturamentoFacade.desdobrarContaHospitalar(this.cthSeq, this.motivoDesdobramento, this.dataHoraDesdobramento,
															contaConsideradaReapresentada, nomeMicrocomputador, dt);
//			this.faturamentoFacade.clear();
//			this.faturamentoFacade.flush();
			this.motivoDesdobramento = null;
			this.dataHoraDesdobramento = null;
			
			apresentarMsgNegocio("DESDOBRAMENTO_CONCLUIDO_SUCESSO");
			
			// Atualiza as informações na tela
			dataModel.reiniciarPaginator();
			
			pesquisarContasHospitalaresParaDesdobramentoPaginatorController.pesquisar();
			
			this.inicio();
		} catch (BaseException e) {
			LOG.error("Exceção capturada.", e);
			apresentarExcecaoNegocio(e);
		}
	}

	public void estornarDesdobramento() {
		if (motivoDesdobramento != null) {
			LOG.debug("motivoDesdobramento: " + this.motivoDesdobramento.getDescricao());
		}
		LOG.debug("dataHoraDesdobramento: " + this.dataHoraDesdobramento);
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.debug("Exceção caputada:", e);
		}
		
		try {
			final Date dt = new Date();
			this.faturamentoFacade.estornarDesdobramento(this.cthSeq, nomeMicrocomputador, dt);
			
			this.motivoDesdobramento = null;
			this.dataHoraDesdobramento = null;
			
			apresentarMsgNegocio("ESTORNO_DESDOBRAMENTO_CONCLUIDO_SUCESSO");
			
			// Atualiza as informações na tela
			this.dataModel.reiniciarPaginator();
			
			pesquisarContasHospitalaresParaDesdobramentoPaginatorController.pesquisar();
			
			this.inicio();
		} catch (BaseException e) {
			LOG.error("Exceção capturada.", e);
			apresentarExcecaoNegocio(e);
		}		
	}
	
	public void validarDesdobramento() {
		if (this.contaHospitalar.getContaHospitalar().getContaHospitalarReapresentada() == null) {
			this.desdobrarConta(null);
		} else {
			this.exibirModal = true;
		}
	}
	
	private Date converterStringParaData(String str) throws ParseException {
		DateFormat df = new SimpleDateFormat(DATE_PATTERN_DDMMYYYY_HHMMSS);
		return df.parse(str);
	}
	
	public String voltar() {
		return from != null ? from : REDIRECIONA_PESQUISAR_CONTA_HOSP_DESDOBRAMENTO;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public VFatContaHospitalarPac getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(VFatContaHospitalarPac contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public VFatAssociacaoProcedimento getProcedimentoHospitalarSolicitado() {
		return procedimentoHospitalarSolicitado;
	}

	public void setProcedimentoHospitalarSolicitado(VFatAssociacaoProcedimento procedimentoHospitalarSolicitado) {
		this.procedimentoHospitalarSolicitado = procedimentoHospitalarSolicitado;
	}

	public VFatAssociacaoProcedimento getProcedimentoHospitalarRealizado() {
		return procedimentoHospitalarRealizado;
	}

	public void setProcedimentoHospitalarRealizado(VFatAssociacaoProcedimento procedimentoHospitalarRealizado) {
		this.procedimentoHospitalarRealizado = procedimentoHospitalarRealizado;
	}

	public FatMotivoDesdobramento getMotivoDesdobramento() {
		return motivoDesdobramento;
	}

	public void setMotivoDesdobramento(FatMotivoDesdobramento motivoDesdobramento) {
		this.motivoDesdobramento = motivoDesdobramento;
	}

	public Date getDataHoraDesdobramento() {
		return dataHoraDesdobramento;
	}

	public void setDataHoraDesdobramento(Date dataHoraDesdobramento) {
		this.dataHoraDesdobramento = dataHoraDesdobramento;
	}

	public Boolean getExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(Boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public Integer getSeqContaCriadaPeloDesdobramento() {
		return seqContaCriadaPeloDesdobramento;
	}

	public void setSeqContaCriadaPeloDesdobramento(Integer seqContaCriadaPeloDesdobramento) {
		this.seqContaCriadaPeloDesdobramento = seqContaCriadaPeloDesdobramento;
	}
	
	public Byte getMdsSeq() {
		return mdsSeq;
	}

	public void setMdsSeq(Byte mdsSeq) {
		this.mdsSeq = mdsSeq;
	}

	public String getStrDthrSugestao() {
		return strDthrSugestao;
	}

	public void setStrDthrSugestao(String strDthrSugestao) {
		this.strDthrSugestao = strDthrSugestao;
	}

	public DynamicDataModel<FatItemContaHospitalar> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatItemContaHospitalar> dataModel) {
		this.dataModel = dataModel;
	}

}
