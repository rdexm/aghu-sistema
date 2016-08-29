package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.action.RelatorioAnaEvoInternacaoController;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.certificacaodigital.action.VisualizarDocumentoController;
import br.gov.mec.aghu.dominio.DominioNodoPOL;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.exames.action.ConsultarResultadosNotaAdicionalController;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.action.ConsultarInternacoesPOLController.EnumTargetConsultaInternacoesPOL;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltasAmbulatoriasPolVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.CirurgiasInternacaoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItensResultadoImpressaoVo;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ProcedimentosImagemPOLVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

@SuppressWarnings({"PMD.AghuTooManyMethods"})
public class CirurgiasInternacaoPOLController extends ActionController implements ActionPaginator {
	private static final Log LOG = LogFactory.getLog(CirurgiasInternacaoPOLController.class);
	private static final long serialVersionUID = -8764119058026431909L;
	private static final String AMBULATORIO_RELATORIO_AMAMNESE_EVOLUCAO_INTERNACAO = "ambulatorio-relatorioAmamneseEvolucaoInternacao";
	private static final String RELATORIO_DESCRICAO_CIRURGIA_PDF = "relatorioDescricaoCirurgiaPdf";
	private static final String DOCUMENTO_ASSINADO = "certificacaodigital-visualizarDocumentoAssinadoPOL";
	private static final String EXIBE_RELATORIO_PDTR_DESCRICAO = "exibeRelatorioPdtrDescricao";
	private static final String RELATORIO_PLANEJAMENTO_CIRURGICO_PDF = "relatorioPlanejamentoCirurgicoPdf";
	private static final String RELATORIO_ATOS_ANESTESICOS = "pol-relatorioAtosAnestesicosPdf";
	private static final String RELATORIO_PDT_DESC_PROC_CIRURGIA_PDF = "relatorioPdtDescProcCirurgiaPdf";
	private static final String CONSULTAR_RESULTADO_NOTA_ADICIONAL = "exames-consultarResultadoNotaAdicional";
	private static final String CIRURGIA_INTERNACAO_LIST_POL = "pol-cirurgia";
	private static final String POL_CIRURGIA = "pol-cirurgia";
	private static final String CINZA = "cinza";

	@EJB
	private IPacienteFacade pacienteFacade;
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	@EJB
	private IInternacaoFacade internacaoFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;	
	@Inject
	private RelatorioAtosAnestesicosController relatorioAtosAnestesicosController;
	@Inject
	private VisualizarDocumentoController visualizarDocumentoController;
	@Inject
	private ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController;
	@Inject
	private RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	@Inject
	private RelatorioPdtDescProcCirurgiaController relatorioPdtDescProcCirurgiaController;
	@Inject @Paginator
	private DynamicDataModel<CirurgiasInternacaoPOLVO> dataModel;
	private Integer prontuario;
	private Boolean habilitarBotaoVoltar;
	private Boolean botaoDescricao;
	private Boolean botaoDocAssinado;
	private Boolean botaoAtoAnestesico;
	private Boolean botaoExameAnatomopatologico;
	private Boolean botaoPlanejamentoCirurgico;
	private Date dataInicial;
	private Date dataFinal;
	private Boolean botaoControles;
	private String origem;
	private DominioSituacaoCirurgia dominioSituacaoCirurgia;
	private List<CirurgiasInternacaoPOLVO> cirurgias;
	private List<ItensResultadoImpressaoVo> itensResultadoImpressaoVO;
	private CirurgiasInternacaoPOLVO registroSelecionado;
	private MbcExtratoCirurgia extratoCirurgia;
	private Integer seqVersaoDoc;
	private Boolean renderizaBotaoMotivoCancelComModal;
	private Integer seqEnvioRelatorio;// Atributo para gerar relatorio quando não assinado
	private Short seq2EnvioRelatorio;// Atributo para gerar relatorio quando não assinado
	private String nomeRelatorio;
	private Boolean comModal;
	private Boolean exibeRelatorioPdtrDescricao;
	private Boolean exibeRelatorioMbcrDescrCirurgica;
	private List<PdtDescricao> listaPdtrDescricao;
	private List<MbcDescricaoCirurgica> listaMbcrDescricao;
	private Boolean permiteImpressao;
	private PdtDescricao pdtDescricaoSelecionada;
	private MbcDescricaoCirurgica mbcDescricaoCirurgicaSelecionada;
	private Long seqMbcFichaAnestesia;
	private String vSessao;
	private Long tamLista;
	private AipPacientes paciente;
	private Boolean acessoAdminPOL;
	private Boolean habilitarBtnSeAcessoAdminPOL;
	@Inject
	private SecurityController securityController;
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;		
	private MbcAgendas agendaCirurgia;
	private InternacaoVO internacao = new InternacaoVO();
	private boolean pesquisarRegistrosControlePaciente = false;
	private Boolean certificacaoHuAtiva;
	private Date dataMotivoCancelamento;
	private String descricaoMotivoCancelamento;
	private String descricaoMotivoCancelamentoTruncada;
	private String motivoCancelamento;
	@Inject
	private ProcedimentosPOLController procedimentosPOLController;
	@Inject
	private RelatorioAnaEvoInternacaoController relatorioAnaEvoInternacaoController;	
	
	private Integer prmverImgImpax;
	private String prmEndWebImagens;
	
	private List<ProcedimentosImagemPOLVO> listaImagem;
	private String impaxUrl = "";
	
	private Boolean botaoImagem;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		acessoAdminPOL = securityController.usuarioTemPermissao("acessoAdminPOL", "acessar");
		pesquisarRegistrosControlePaciente  = securityController.usuarioTemPermissao("pesquisarRegistrosControlePaciente", "pesquisar");
		try {
			prmverImgImpax = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_VER_IMAGENS_IMPAX);
			prmEndWebImagens = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_ENDERECO_WEB_IMAGENS);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	// se usuario tiver a permissao acessoAdminPOL, btn desabilitado
	public Boolean gethabilitarBtnSeAcessoAdminPOL() {
		if (acessoAdminPOL) {
			habilitarBtnSeAcessoAdminPOL = true;
		} else {
			habilitarBtnSeAcessoAdminPOL = false;
		}
		return habilitarBtnSeAcessoAdminPOL;
	}

	public Boolean getUsuarioAdministrativo() {
		if (acessoAdminPOL) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public void setInicio(Integer prontuario, String origem) throws ApplicationBusinessException{
		this.prontuario = prontuario;
		this.origem = origem;
		inicio();
	}
	
	public void inicio() {
	 
		if(registroSelecionado != null){
			return;
		}

		if (itemPOL!=null){
			prontuario=itemPOL.getProntuario();
			if (itemPOL.getTipo().equals(DominioNodoPOL.CIRURGIA.getTipo())){
				origem=null;
				habilitarBotaoVoltar = false;
				
			}else if (itemPOL.getTipo().equals(DominioNodoPOL.INTERNACAO.getTipo()) || 
						itemPOL.getTipo().equals(DominioNodoPOL.DETALHE_INTERNACAO.getTipo())){
				habilitarBotaoVoltar=true;
			}	
		}

		registroSelecionado = new CirurgiasInternacaoPOLVO();
		paciente = this.pacienteFacade.pesquisarPacientePorProntuario(prontuario);
		
		if (pdtDescricaoSelecionada == null) {
			pdtDescricaoSelecionada = new PdtDescricao();
		}
		
		if (mbcDescricaoCirurgicaSelecionada == null) {
			mbcDescricaoCirurgicaSelecionada = new MbcDescricaoCirurgica();
			mbcDescricaoCirurgicaSelecionada.setId(new MbcDescricaoCirurgicaId());
		}

		limparSituacaoBotoes();
		limpaRegistrosSelecionados();
		listaPdtrDescricao = null;
		
		if(certificacaoHuAtiva == null){
			try{
				certificacaoHuAtiva = "S".equals(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL).getVlrTexto());
			}catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		this.dataModel.reiniciarPaginator();
		Date today = DateUtil.truncaData(new Date());
		this.setDataFinal(today);
		this.setDataInicial(DateUtil.adicionaDias(today, -14));
	}

	private void limparSituacaoBotoes() {
		botaoAtoAnestesico = Boolean.FALSE;
		botaoExameAnatomopatologico = Boolean.FALSE;
		botaoDescricao = Boolean.FALSE;
		botaoPlanejamentoCirurgico = Boolean.FALSE;
		botaoImagem = Boolean.FALSE;
		exibeRelatorioMbcrDescrCirurgica = Boolean.FALSE;
		exibeRelatorioPdtrDescricao = Boolean.FALSE;
		botaoControles = Boolean.FALSE;
		comModal = null;
		cirurgias = null;
		tamLista = null;
		procedimentosPOLController.processarListaPrescricaoMedicas(null, certificacaoHuAtiva, null);
	}

	public void limpaRegistrosSelecionados() {
		pdtDescricaoSelecionada = new PdtDescricao();
		mbcDescricaoCirurgicaSelecionada = new MbcDescricaoCirurgica();
		mbcDescricaoCirurgicaSelecionada.setId(new MbcDescricaoCirurgicaId());
	}

	public void selecionaRegistroPdtDescricao() {
		if(this.pdtDescricaoSelecionada != null){
		this.seqEnvioRelatorio = pdtDescricaoSelecionada.getSeq();
		}
		
	}

	public void selecionaRegistroMbcDescricao() {
		if(this.mbcDescricaoCirurgicaSelecionada != null){
			seqEnvioRelatorio = mbcDescricaoCirurgicaSelecionada.getId().getCrgSeq();
			seq2EnvioRelatorio = mbcDescricaoCirurgicaSelecionada.getId().getSeqp();
		}
	}

	public void selecionaRegistro() {
		try {
		this.internacao = new InternacaoVO();
			botaoDescricao = botaoControles = false;
			botaoAtoAnestesico = prontuarioOnlineFacade.habilitarBotaoAtoAnestesicoCirurgiasPol(registroSelecionado.getSeq());
			// botaoDocAssinado = prontuarioOnlineFacade.habilitarBotaoDocAssinadoCirurgiasPol(registroSelecionado.getSeq());
			botaoExameAnatomopatologico = prontuarioOnlineFacade.habilitarBotaoExameAnatopatologico(registroSelecionado.getSeq());
			botaoDescricao = prontuarioOnlineFacade.habilitarBotaoDescricaoCirurgiasPol(registroSelecionado.getSeq());
			processarBotaoPlanejamentoCirurgico(registroSelecionado);
			AghAtendimentos atendimento = aghuFacade.obterAghAtendimentosComInternacaoEAtendimentoUrgencia(registroSelecionado.getAtdSeq());
			if(atendimento!=null) {
				botaoControles = this.internacaoFacade.habilitarDadosControle(this.paciente.getCodigo(), registroSelecionado.getAtdSeq())
					&& pesquisarRegistrosControlePaciente;
			}
			
			if (botaoControles){
				this.internacao.setAtdSeq(registroSelecionado.getAtdSeq());
				
				this.internacao.setCodigoPaciente(atendimento.getPaciente().getCodigo());
				
				if (atendimento.getOrigem() == DominioOrigemAtendimento.I){
					if (atendimento.getInternacao()!=null && atendimento.getInternacao().getAtendimentoUrgencia()!=null){
						this.internacao.setDthrInicio(atendimento.getInternacao().getAtendimentoUrgencia().getDtAtendimento());
						this.internacao.setDthrFim(atendimento.getInternacao().getAtendimentoUrgencia().getDtAltaAtendimento());
					}
					
					if (atendimento.getInternacao()!=null && atendimento.getInternacao().getAtendimentoUrgencia()==null){
						this.internacao.setDthrInicio(atendimento.getInternacao().getDthrInternacao());
						this.internacao.setDthrFim(atendimento.getInternacao().getDthrAltaMedica());
					}
				}
				if (atendimento.getOrigem() == DominioOrigemAtendimento.C){
					
					Date auxDateIni = DateUtils.truncate(atendimento.getDthrInicio(), Calendar.DAY_OF_MONTH); 
					auxDateIni = DateUtils.addDays(auxDateIni, -1); 
					
					this.internacao.setDthrInicio(auxDateIni);  
					
					if(atendimento.getDthrFim()!=null){
						this.internacao.setDthrFim(atendimento.getDthrFim());
						
					} else{
						this.internacao.setDthrFim(new Date());
						
						if (CoreUtil.diferencaEntreDatasEmDias(this.internacao.getDthrFim(), internacao.getDthrInicio())> 15){
							Date auxDateFim = DateUtils.truncate(this.internacao.getDthrInicio(), Calendar.DAY_OF_MONTH); 
							auxDateFim = DateUtils.addDays(auxDateFim, 15); 
							this.internacao.setDthrFim(auxDateFim);
					    }	
				   }
				}
			}
			
			if (botaoDescricao) {
				Object[] resultadoImpressaoDescricao = prontuarioOnlineFacade
						.imprimirDescricao(registroSelecionado.getSeq());
				seqEnvioRelatorio = (Integer) resultadoImpressaoDescricao[0];
				seq2EnvioRelatorio = (Short) resultadoImpressaoDescricao[1];
				nomeRelatorio = (String) resultadoImpressaoDescricao[2];
				comModal = (Boolean) resultadoImpressaoDescricao[3];
				exibeRelatorioPdtrDescricao = (Boolean) resultadoImpressaoDescricao[4];
				exibeRelatorioMbcrDescrCirurgica = (Boolean) resultadoImpressaoDescricao[5];
				listaPdtrDescricao = (List<PdtDescricao>) resultadoImpressaoDescricao[6];
				listaMbcrDescricao = (List<MbcDescricaoCirurgica>) resultadoImpressaoDescricao[7];
				permiteImpressao = (Boolean) resultadoImpressaoDescricao[8];
			} else {
				comModal = null;
			}

			if(atendimento != null /*&& DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial().contains(atendimento.getOrigem())*/){
				procedimentosPOLController.
						processarListaPrescricaoMedicas(
								atendimento.getSeq(), 
								certificacaoHuAtiva, "pol-cirurgia");///paciente/prontuarioonline/cirurgiasInternacaoListPOL.xhtml
			}
			
					
			if(prmverImgImpax == 1){
				botaoImagem = registroSelecionado.getPacOruAccNummer() != null;
				String accessiom = registroSelecionado.getPacOruAccNummer();
				String url = prmEndWebImagens + this.paciente.getCodigo() + "%26accession%3D" + accessiom;
				setImpaxUrl(url);
			}else{
				setBotaoImagem(Boolean.FALSE);
			}
				
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Verifica se o botão Ananmese deverá estar habilitado.
	 * 
	 * @return
	 */
	public Boolean habilitarBotaoAnanmese() {
		Boolean habilitar = Boolean.FALSE;
		if (registroSelecionado != null) {
			habilitar = ambulatorioFacade
					.existeDadosImprimirRelatorioAnamneses(registroSelecionado.getAtdSeq());
		}
		return habilitar && (getUsuarioAdministrativo() == false);
	}

	/**
	 * Verifica se o botão Evolução deverá estar habilitado.
	 * 
	 * @return
	 */
	public Boolean habilitarBotaoEvolucao() {
		Boolean habilitar = Boolean.FALSE;
		if (registroSelecionado != null) {
			habilitar = ambulatorioFacade
					.existeDadosImprimirRelatorioEvolucao(registroSelecionado.getAtdSeq());
		}
		return habilitar && (getUsuarioAdministrativo() == false);
	}
	
	private void processarBotaoPlanejamentoCirurgico(
			CirurgiasInternacaoPOLVO registroSelecionado) {
		try {
			agendaCirurgia = prontuarioOnlineFacade.obtemAgendaPlanejamentoCirurgico(registroSelecionado.getSeq());
			botaoPlanejamentoCirurgico = agendaCirurgia != null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String exibeRelatorioPdtrDescricaoModal() {
		try {
			Boolean docAssinado = prontuarioOnlineFacade.verificarSeDocumentoDescricaoCirugiaAssinado(pdtDescricaoSelecionada.getMbcCirurgias().getSeq());
			if (docAssinado) {
				seqVersaoDoc = prontuarioOnlineFacade.chamarDocCertifCirurg(pdtDescricaoSelecionada.getSeq());
				if (seqVersaoDoc != null) {
					visualizarDocumentoController.setOrigem(CIRURGIA_INTERNACAO_LIST_POL);
					visualizarDocumentoController.setSeqAghVersaoDocumento(seqVersaoDoc);
					return DOCUMENTO_ASSINADO;
				}
			} else {
				relatorioPdtDescProcCirurgiaController.setVoltarPara(CIRURGIA_INTERNACAO_LIST_POL);
				relatorioPdtDescProcCirurgiaController.setSeqPdtDescricao(pdtDescricaoSelecionada.getSeq());
				return RELATORIO_PDT_DESC_PROC_CIRURGIA_PDF;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String exibeRelatorioPdtrDescricao() {
		try {
			Boolean docAssinado = prontuarioOnlineFacade.verificarSeDocumentoDescricaoCirugiaAssinado(registroSelecionado.getSeq());
			if (docAssinado) {
				seqVersaoDoc = prontuarioOnlineFacade.chamarDocCertifCirurg(registroSelecionado.getSeq());
				if (seqVersaoDoc != null) {
					visualizarDocumentoController.setOrigem(CIRURGIA_INTERNACAO_LIST_POL);
					visualizarDocumentoController.setSeqAghVersaoDocumento(seqVersaoDoc);
					return DOCUMENTO_ASSINADO;
				}
			} else {
				return EXIBE_RELATORIO_PDTR_DESCRICAO;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String exibeRelatorioMbcrDescricao() {
		try {
			Boolean docAssinado = prontuarioOnlineFacade.verificarSeDocumentoDescricaoCirugiaAssinado(registroSelecionado.getSeq());
			if (docAssinado) {
				seqVersaoDoc = prontuarioOnlineFacade.chamarDocCertifCirurg(registroSelecionado.getSeq());
				if (seqVersaoDoc != null) {
					visualizarDocumentoController.setOrigem(CIRURGIA_INTERNACAO_LIST_POL);
					visualizarDocumentoController.setSeqAghVersaoDocumento(seqVersaoDoc);
					return DOCUMENTO_ASSINADO;
				}
			} else {
				return RELATORIO_DESCRICAO_CIRURGIA_PDF;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String exibeRelatorioMbcrDescricaoModal() {
		try {
			Boolean docAssinado = prontuarioOnlineFacade.verificarSeDocumentoDescricaoCirugiaAssinado(mbcDescricaoCirurgicaSelecionada.getId().getCrgSeq());
			if (docAssinado) {
				seqVersaoDoc = prontuarioOnlineFacade.chamarDocCertifCirurg(mbcDescricaoCirurgicaSelecionada.getId().getCrgSeq());
				if (seqVersaoDoc != null) {
					visualizarDocumentoController.setOrigem(CIRURGIA_INTERNACAO_LIST_POL);
					visualizarDocumentoController.setSeqAghVersaoDocumento(seqVersaoDoc);
					return DOCUMENTO_ASSINADO;
				}
			} else {
				relatorioDescricaoCirurgiaController.setCrgSeq(mbcDescricaoCirurgicaSelecionada.getId().getCrgSeq());
				relatorioDescricaoCirurgiaController.setSeqpMbcDescCrg(mbcDescricaoCirurgicaSelecionada.getId().getSeqp());
				relatorioDescricaoCirurgiaController.setPrint(this.getPermiteImpressao());
				relatorioDescricaoCirurgiaController.setVoltarPara("cirurgiasInternacaoListPOL");
				
				return RELATORIO_DESCRICAO_CIRURGIA_PDF;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String exibeRelatorioPlanejamentoCirurgico() {
		try {
			Boolean docAssinado = prontuarioOnlineFacade.verificarSeDocumentoPlanejamentoCirugicoAssinado(registroSelecionado.getSeq());
			if (docAssinado) {
				seqVersaoDoc = prontuarioOnlineFacade.chamarDocCertifPlanejamentoCirurg(registroSelecionado.getSeq());
				if (seqVersaoDoc != null) {
					return "documentoAssinado_planejamentoCirurgico"; //TODO vai para onde????? Não encontrado local correspondente no 5
				}
			} else {
				return RELATORIO_PLANEJAMENTO_CIRURGICO_PDF;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void exibirMsgFuncionalidadeNaoImplementada() {
		apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_FUNCIONALIDADE_NAO_IMPLEMENTADA");
	}

	public String pesquisarEvolucaoPeriodo() {
		String retorno = null;
		boolean dataNula = false;
		if (getDataInicial() == null){
			this.apresentarMsgNegocio("dataInicio",Severity.ERROR, "RELATORIO_EVOLUCAO_PERIODO_DATA_OBRIGATORIA", "Data Inicial");
			dataNula = true;		
		}
		if (getDataFinal() == null){
			this.apresentarMsgNegocio("dataFim",Severity.ERROR, "RELATORIO_EVOLUCAO_PERIODO_DATA_OBRIGATORIA", "Data Final");
			dataNula = true;			
		}

		if(getDataInicial() != null && getDataFinal() != null){
			if (getDataInicial().after(getDataFinal())) {
				apresentarMsgNegocio(Severity.ERROR, "RELATORIO_EVOLUCAO_PERIODO_DATA_FINAL_MENOR_DATA_INICIAL");
				return null;
			}
		}

		if (dataNula){
			return null;
		}
		
		if (DateUtil.calcularDiasEntreDatas(getDataInicial(), getDataFinal()) > 15){
			this.apresentarMsgNegocio(Severity.ERROR ,"RELATORIO_EVOLUCAO_PERIODO_MAIOR_15_DIAS");
			return null;			
		}

		relatorioAnaEvoInternacaoController.setDataInicial(getDataInicial());
		relatorioAnaEvoInternacaoController.setDataFinal(getDataFinal());
		relatorioAnaEvoInternacaoController.setTipoRelatorio(EnumTargetConsultaInternacoesPOL.RELATORIO_EVOLUCAO_PERIODO.toString());
		gerarRelatorioAnaEvoInternacao();

		if (relatorioAnaEvoInternacaoController.getDadosRelatorioIsNotEmpty()) {
			retorno = AMBULATORIO_RELATORIO_AMAMNESE_EVOLUCAO_INTERNACAO;
		}
		 return retorno;
	}
	
	public String pesquisarAnamnese() {
		relatorioAnaEvoInternacaoController.setTipoRelatorio(EnumTargetConsultaInternacoesPOL.RELATORIO_ANAMNESES.toString());
		gerarRelatorioAnaEvoInternacao(); 
		
		if (relatorioAnaEvoInternacaoController.getDadosRelatorioIsNotEmpty()) {
			return "ambulatorio-relatorioAmamneseEvolucaoInternacao";
		}
		return null;
	}
	
	public void pesquisarEvolucaoTodos() {
		RapServidores servidorLogado = null;
		try {
			// Seta sessão para 12 horas
			pacienteFacade.commit(60 * 60 * 12);
			
			servidorLogado = registroColaboradorFacade
					.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
							new Date());

			relatorioAnaEvoInternacaoController.imprimirRelatorioEvolucaoTodos(
					registroSelecionado.getAtdSeq(), servidorLogado,
					this.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Evoluções");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage());
		}
	}	
	private void gerarRelatorioAnaEvoInternacao() {	
		relatorioAnaEvoInternacaoController.setOrigem(POL_CIRURGIA);
		relatorioAnaEvoInternacaoController.setAtdSeq(registroSelecionado.getAtdSeq());		
		relatorioAnaEvoInternacaoController.gerarDados();	
	}		
	
	public String verificarSeDocumentoAtoAnestesicoAssinado() {
		try {
			Boolean docAssinado = prontuarioOnlineFacade.verificarSeDocumentoAtoAnestesicoAssinado(registroSelecionado.getSeq());
			if (docAssinado) {

				seqVersaoDoc = prontuarioOnlineFacade.chamarDocCertifFicha(registroSelecionado.getSeq());
				if (seqVersaoDoc != null) {
					visualizarDocumentoController.setOrigem(CIRURGIA_INTERNACAO_LIST_POL);
					visualizarDocumentoController.setSeqAghVersaoDocumento(seqVersaoDoc);
					return DOCUMENTO_ASSINADO;
				}
			} else {
				String idSessao = ((HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();
				Object[] relAtoAnest = prontuarioOnlineFacade.processarRelatorioAtosAnestesicos(registroSelecionado.getSeq(), null, null,idSessao);				
				seqMbcFichaAnestesia = (Long) relAtoAnest[0];
				vSessao = (String) relAtoAnest[1];
				permiteImpressao = (Boolean) relAtoAnest[2];			
				
				relatorioAtosAnestesicosController.setSeqMbcFichaAnestesia(seqMbcFichaAnestesia);
				
				relatorioAtosAnestesicosController.setvSessao(vSessao);
				
				relatorioAtosAnestesicosController.setPermiteImpressao(permiteImpressao);
				
				relatorioAtosAnestesicosController.setVoltarPara("cirurgiasInternacaoListPOL");
				return RELATORIO_ATOS_ANESTESICOS;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String obterCorLinha(AltasAmbulatoriasPolVO registro) {
		if (registroSelecionado != null && registro != null
				&& registro.getSeq() != null
				&& registro.getSeq().equals(registroSelecionado.getSeq())) {
			return CINZA;
		} else {
			return "";
		}
	}

	public String obterCorLinhaPdt(PdtDescricao registro) {
		if (pdtDescricaoSelecionada != null && registro != null
				&& pdtDescricaoSelecionada.getSeq() != null
				&& pdtDescricaoSelecionada.getSeq().equals(registro.getSeq())) {
			return CINZA;
		} else {
			return "";
		}
	}

	public String obterCorLinhaMbc(MbcDescricaoCirurgica registro) {
		if (mbcDescricaoCirurgicaSelecionada != null
				&& registro != null
				&& mbcDescricaoCirurgicaSelecionada.getId().getSeqp() != null
				&& mbcDescricaoCirurgicaSelecionada.getId().getSeqp()
						.equals(registro.getId().getSeqp())) {
			return CINZA;
		} else {
			return "";
		}
	}

	public String redirecionarParaVerResultadoDeExame() {		
		try {
			consultarResultadosNotaAdicionalController.setSolicitacoes(prontuarioOnlineFacade.processarResultadosNotasAdicionaisPorCirurgia(registroSelecionado.getSeq(), 0, Boolean.FALSE));
			return CONSULTAR_RESULTADO_NOTA_ADICIONAL;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}	
		return null;
	}

	public void buscarMotivoCancelamento() {
		extratoCirurgia = prontuarioOnlineFacade.buscarMotivoCancelCirurgia(registroSelecionado.getSeq());
		
		motivoCancelamento = extratoCirurgia.getDescricaoMotivoCancelamentoEditado(); 
		dataMotivoCancelamento = registroSelecionado.getData();
		descricaoMotivoCancelamento = registroSelecionado.getDescricao();
		descricaoMotivoCancelamentoTruncada = StringUtil.trunc(descricaoMotivoCancelamento, true, 60l);
		
		openDialog("modalMotivoCancelamentoCirurgiaWG");
	}

	public void exibirMsgErroSemParametroMotDesmarcar() {
		apresentarMsgNegocio(Severity.ERROR,
				"MENSAGEM_PARAMETRO_MOT_DESMARCAR_NAO_CONFIGURADO");
	}

	@Override
	public Long recuperarCount() {
		if (cirurgias == null && tamLista == null) {
			tamLista = (long) prontuarioOnlineFacade.pesquisarCirurgiasInternacaoPOL(paciente.getCodigo()).size();
		}
		return tamLista;
	}

	@Override
	public List<CirurgiasInternacaoPOLVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		cirurgias = prontuarioOnlineFacade.pesquisarCirurgiasInternacaoPOL(paciente.getCodigo());
		
		if ((cirurgias != null) && !(cirurgias.isEmpty())) {
			// setMaxResults(5);
			paginarLista(firstResult, maxResult, orderProperty, asc);
			renderizaBotaoMotivoCancelComModal = parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR);
			return cirurgias;
		}
		return new ArrayList<CirurgiasInternacaoPOLVO>();
	}

	private void paginarLista(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		if (tamLista == null || tamLista == 0) {
			tamLista = (long) cirurgias.size();
		}
		Integer lastResult = (firstResult + maxResult) > cirurgias.size() ? cirurgias
				.size() : (firstResult + maxResult);
		cirurgias = cirurgias.subList(firstResult, lastResult);
	}

	public String voltarCirurgiasInternacao() {
		registroSelecionado = null;
		return origem;
	}
	public DominioSituacaoCirurgia[] getDominioSituacaoCirurgiaLista() {
		return this.dominioSituacaoCirurgia.values();
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}
	public Boolean getHabilitarBotaoVoltar() {
		return habilitarBotaoVoltar;
	}
	public void setHabilitarBotaoVoltar(Boolean habilitarBotaoVoltar) {
		this.habilitarBotaoVoltar = habilitarBotaoVoltar;
	}
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public List<CirurgiasInternacaoPOLVO> getCirurgias() {
		return cirurgias;
	}
	public void setCirurgias(List<CirurgiasInternacaoPOLVO> cirurgias) {
		this.cirurgias = cirurgias;
	}
	public CirurgiasInternacaoPOLVO getRegistroSelecionado() {
		return registroSelecionado;
	}
	public void setRegistroSelecionado(
			CirurgiasInternacaoPOLVO registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}
	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}
	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}
	public Integer getSeqVersaoDoc() {
		return seqVersaoDoc;
	}
	public void setSeqVersaoDoc(Integer seqVersaoDoc) {
		this.seqVersaoDoc = seqVersaoDoc;
	}
	public Boolean getRenderizaBotaoMotivoCancelComModal() {
		return renderizaBotaoMotivoCancelComModal;
	}
	public void setRenderizaBotaoMotivoCancelComModal(
			Boolean renderizaBotaoMotivoCancelComModal) {
		this.renderizaBotaoMotivoCancelComModal = renderizaBotaoMotivoCancelComModal;
	}
	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}
	public MbcExtratoCirurgia getExtratoCirurgia() {
		return extratoCirurgia;
	}
	public void setExtratoCirurgia(MbcExtratoCirurgia extratoCirurgia) {
		this.extratoCirurgia = extratoCirurgia;
	}
	public Boolean getBotaoDescricao() {
		return botaoDescricao;
	}
	public void setBotaoDescricao(Boolean botaoDescricao) {
		this.botaoDescricao = botaoDescricao;
	}
	public Boolean getBotaoDocAssinado() {
		return botaoDocAssinado;
	}
	public void setBotaoDocAssinado(Boolean botaoDocAssinado) {
		this.botaoDocAssinado = botaoDocAssinado;
	}
	public Boolean getBotaoAtoAnestesico() {
		return botaoAtoAnestesico;
	}
	public void setBotaoAtoAnestesico(Boolean botaoAtoAnestesico) {
		this.botaoAtoAnestesico = botaoAtoAnestesico;
	}
	public Boolean getBotaoExameAnatomopatologico() {
		return botaoExameAnatomopatologico;
	}
	public void setBotaoExameAnatomopatologico(
			Boolean botaoExameAnatomopatologico) {
		this.botaoExameAnatomopatologico = botaoExameAnatomopatologico;
	}
	public Integer getSeqEnvioRelatorio() {
		return seqEnvioRelatorio;
	}
	public void setSeqEnvioRelatorio(Integer seqEnvioRelatorio) {
		this.seqEnvioRelatorio = seqEnvioRelatorio;
	}
	public Short getSeq2EnvioRelatorio() {
		return seq2EnvioRelatorio;
	}
	public void setSeq2EnvioRelatorio(Short seq2EnvioRelatorio) {
		this.seq2EnvioRelatorio = seq2EnvioRelatorio;
	}
	public String getNomeRelatorio() {
		return nomeRelatorio;
	}
	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}
	public Boolean getComModal() {
		return comModal;
	}
	public void setComModal(Boolean comModal) {
		this.comModal = comModal;
	}
	public Boolean getExibeRelatorioPdtrDescricao() {
		return exibeRelatorioPdtrDescricao;
	}
	public void setExibeRelatorioPdtrDescricao(
			Boolean exibeRelatorioPdtrDescricao) {
		this.exibeRelatorioPdtrDescricao = exibeRelatorioPdtrDescricao;
	}
	public Boolean getExibeRelatorioMbcrDescrCirurgica() {
		return exibeRelatorioMbcrDescrCirurgica;
	}
	public void setExibeRelatorioMbcrDescrCirurgica(
			Boolean exibeRelatorioMbcrDescrCirurgica) {
		this.exibeRelatorioMbcrDescrCirurgica = exibeRelatorioMbcrDescrCirurgica;
	}
	public List<PdtDescricao> getListaPdtrDescricao() {
		return listaPdtrDescricao;
	}
	public void setListaPdtrDescricao(List<PdtDescricao> listaPdtrDescricao) {
		this.listaPdtrDescricao = listaPdtrDescricao;
	}
	public List<MbcDescricaoCirurgica> getListaMbcrDescricao() {
		return listaMbcrDescricao;
	}
	public void setListaMbcrDescricao(
			List<MbcDescricaoCirurgica> listaMbcrDescricao) {
		this.listaMbcrDescricao = listaMbcrDescricao;
	}
	public Boolean getPermiteImpressao() {
		return permiteImpressao;
	}
	public void setPermiteImpressao(Boolean permiteImpressao) {
		this.permiteImpressao = permiteImpressao;
	}
	public void setPdtDescricaoSelecionada(PdtDescricao pdtDescricaoSelecionada) {
		this.pdtDescricaoSelecionada = pdtDescricaoSelecionada;
	}
	public PdtDescricao getPdtDescricaoSelecionada() {
		return pdtDescricaoSelecionada;
	}
	public void setMbcDescricaoCirurgicaSelecionada(
			MbcDescricaoCirurgica mbcDescricaoCirurgicaSelecionada) {
		this.mbcDescricaoCirurgicaSelecionada = mbcDescricaoCirurgicaSelecionada;
	}
	public MbcDescricaoCirurgica getMbcDescricaoCirurgicaSelecionada() {
		return mbcDescricaoCirurgicaSelecionada;
	}
	public List<ItensResultadoImpressaoVo> getItensResultadoImpressaoVO() {
		return itensResultadoImpressaoVO;
	}
	public void setItensResultadoImpressaoVO(
			List<ItensResultadoImpressaoVo> itensResultadoImpressaoVO) {
		this.itensResultadoImpressaoVO = itensResultadoImpressaoVO;
	}
	public Long getSeqMbcFichaAnestesia() {
		return seqMbcFichaAnestesia;
	}
	public String getvSessao() {
		return vSessao;
	}
	public void setSeqMbcFichaAnestesia(Long seqMbcFichaAnestesia) {
		this.seqMbcFichaAnestesia = seqMbcFichaAnestesia;
	}
	public void setvSessao(String vSessao) {
		this.vSessao = vSessao;
	}
	public DominioSituacaoCirurgia getDominioSituacaoCirurgia() {
		return dominioSituacaoCirurgia;
	}
	public void setDominioSituacaoCirurgia(
			DominioSituacaoCirurgia dominioSituacaoCirurgia) {
		this.dominioSituacaoCirurgia = dominioSituacaoCirurgia;
	}
	public Long getTamLista() {
		return tamLista;
	}
	public void setTamLista(Long tamLista) {
		this.tamLista = tamLista;
	}
	public void setBotaoPlanejamentoCirurgico(Boolean botaoPlanejamentoCirurgico) {
		this.botaoPlanejamentoCirurgico = botaoPlanejamentoCirurgico;
	}

	public Boolean getBotaoPlanejamentoCirurgico() {
		return botaoPlanejamentoCirurgico;
	}

	public void setAgendaCirurgia(MbcAgendas agendaCirurgia) {
		this.agendaCirurgia = agendaCirurgia;
	}

	public MbcAgendas getAgendaCirurgia() {
		return agendaCirurgia;
	}

	public DynamicDataModel<CirurgiasInternacaoPOLVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<CirurgiasInternacaoPOLVO> dataModel) {
		this.dataModel = dataModel;
	}
	public Boolean getBotaoControles() {
		return botaoControles;
	}

	public void setBotaoControles(Boolean botaoControles) {
		this.botaoControles = botaoControles;
	}
	public InternacaoVO getInternacao() {
		return internacao;
	}
	public void setInternacao(InternacaoVO internacao) {
		this.internacao = internacao;
	}
	public Boolean getCertificacaoHuAtiva() {
		return certificacaoHuAtiva;
	}
	public Date getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	public Date getDataMotivoCancelamento() {
		return dataMotivoCancelamento;
	}
	public void setDataMotivoCancelamento(Date dataMotivoCancelamento) {
		this.dataMotivoCancelamento = dataMotivoCancelamento;
	}
	public String getDescricaoMotivoCancelamento() {
		return descricaoMotivoCancelamento;
	}
	public void setDescricaoMotivoCancelamento(String descricaoMotivoCancelamento) {
		this.descricaoMotivoCancelamento = descricaoMotivoCancelamento;
	}
	public String getDescricaoMotivoCancelamentoTruncada() {
		return descricaoMotivoCancelamentoTruncada;
	}
	public void setDescricaoMotivoCancelamentoTruncada(
			String descricaoMotivoCancelamentoTruncada) {
		this.descricaoMotivoCancelamentoTruncada = descricaoMotivoCancelamentoTruncada;
	}
	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}
	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	
	public List<ProcedimentosImagemPOLVO> getListaImagem() {
		return listaImagem;
	}

	
	public void setListaImagem(List<ProcedimentosImagemPOLVO> listaImagem) {
		this.listaImagem = listaImagem;
	}

	
	public String getImpaxUrl() {
		return impaxUrl;
	}

	
	public void setImpaxUrl(String impaxUrl) {
		this.impaxUrl = impaxUrl;
	}

	public Boolean getBotaoImagem() {
		return botaoImagem;
	}

	
	public void setBotaoImagem(Boolean botaoImagem) {
		this.botaoImagem = botaoImagem;
	}
}