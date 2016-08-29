package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.action.procdiagterap.DescricaoProcDiagTerapController;
import br.gov.mec.aghu.blococirurgico.action.procdiagterap.DescricaoProcDiagTerapSedacaoController;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaComPacEmTransOperatorioVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.MotivoCancelamentoVO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.SuggestionListaCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.TelaListaCirurgiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.action.ListarPendenciasAssinaturaPaginatorController;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioModulo;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.exames.solicitacao.action.SolicitacaoExameController;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAvalPreSedacao;
import br.gov.mec.aghu.model.MbcAvalPreSedacaoId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcQuestao;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcValorValidoCanc;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.action.ConsultarAmbulatorioPOLController;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioListarCirurgiasPdtDescProcCirurgiaController;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AvaliacaoPreSedacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength","PMD.UselessOverridingMethod"})
public class ListarCirurgiasController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);	 
	}

	private static final Log LOG = LogFactory.getLog(ListarCirurgiasController.class);

	private static final long serialVersionUID = -2593717606733142845L;
	
	private final static ConstanteAghCaractUnidFuncionais[] CARACTERISTICAS_UNIDADE_FUNCIONAL = {ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS};
	@EJB
	private IAghuFacade aghuFacade;	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;	

	@Inject
	private RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	
	@Inject
	private RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController;
	
	@Inject
	private DescricaoCirurgicaController descricaoCirurgicaController;
	
	@Inject
	private DescricaoProcDiagTerapController descricaoProcDiagTerapController;
	
	@Inject
	private DescricaoProcDiagTerapSedacaoController descricaoProcDiagTerapSedacaoController;
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@Inject
    private SolicitacaoExameController solicitacaoExameController;
	
	@Inject
	private SecurityController securityController;
	
	//@Out(required = false, scope = ScopeType.SESSION)
	private CirurgiaVO crgSelecionada;
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private ConsultarAmbulatorioPOLController consultarAmbulatorioPOLController;
	
	private TelaListaCirurgiaVO tela;
	private List<CirurgiaVO> cirurgias;
	private Map<String, String> legendaTitles;
	private Map<String, String> legendaIcos;
	private Integer seqCrgSelecionada;
	private Integer atdSeqSelecionado;
	private Integer pacCodigoSelecionado;
	private Integer prontuarioSelecionado;
	private String currentSortProperty;
	private String loginUsuarioLogado;
	private RapServidores servidorLogado;
	private MotivoCancelamentoVO motivoCancelamentoVO;
	private MbcQuestao questao;
	private MbcValorValidoCanc valorValidoCanc;
	private String complementoCanc;
	private boolean dispararPesquisa;
	private Integer dcgCrgSeq;
	private Integer crgSeq;
	private Short dcgSeqp;
	private Short unfSeq;
	private Integer ddtSeq;
	private Integer ddtCrgSeq;
	private Boolean togglePesquisaOpened;
	private Boolean exibeModalCancelarCrgComDescricao = Boolean.FALSE;
	private Integer pacCodigo;
	private String tipo;
	private String nomeMicrocomputador;
	private Boolean isImpPreAne = Boolean.FALSE;
	private List<CirurgiaComPacEmTransOperatorioVO> cirurgiasComPacientesEmTransOperatorio;
	private Long tempoRecarregarCirurgiasEmTransOperatorio;
	private Boolean refreshComCamposPesquisaFechados;
	private Integer seqSolicitacaoInternacao;
	private Boolean playAlertaNovoPacEmTransOperatorio = false;
	private Boolean visualizarAlertaPacienteTransOperatorio = false; 
	private String urlFichaAnestesica;
	private Boolean existeIntegracaoAghWebAGHU;
	private String banco = null;
	private String urlBaseWebForms = null;
	private boolean botaoPrescreverHabilitado = false;
	private String filtroPaciente;
	private boolean liberarCamposDoFiltro = false;
	private String mensagemTempoCirurgiaMinimo = "";
	private String paginaOrigem = "";
	
	private List<CirurgiaVO> filteredCirurgias;
	
	private final String PAGE_LAUDO_AIH = "laudoAIH";
	private final String PAGE_CADASTRO_ANOTACOES = "cadastroAnotacoes";
	private final String PAGE_DESCRICAO_CIRURGICA = "blococirurgico-descricaoCirurgica";
	private final String PAGE_DESCRICAO_PDT = "blococirurgico-descricaoProcDiagTerap";
	private final String PAGE_EXAMES_SOLIC_EXAMES ="exames-solicitacaoExameCRUD";
	private final String PAGE_EXAMES_PESQ_FLUXOGRAMA = "exames-pesquisaFluxograma";
	private final String PAGE_PRESCR_ENF_ELABORAR_PRESCRICAO_ENF ="prescricaoenfermagem-elaboracaoPrescricaoEnfermagem";
    private final String PAGE_PRESCR_MED_VER_PRESCR_MED = "prescricaomedica-verificaPrescricaoMedica";
    private final String PAGE_CONTR_PACIENTE_VISUALIZAR_REG = "controlepaciente-visualizarRegistros";
    private final String PAGE_BLOCO_LISTAR_CIRURGIAS = "blococirurgico-listaCirurgias";
    private final String PAGE_ACOMPANHAMENTO_CIRURGIA = "acompanhamentoCirurgia";
    private final String PAGE_REDIRECIONAR_LISTA_PENDENCIAS_ASSINATURA = "certificacaodigital-listarPendenciasAssinatura";

	private AghAtendimentos atendimento;
	private AvaliacaoPreSedacaoVO avaliacaoPreSedacaoVO;
	
	private String urlPendenciaDigital;
	private boolean direcionaAghDocs;
	
	@Inject
	private ListarPendenciasAssinaturaPaginatorController listarPendenciasAssinaturaPaginatorController;

	
	public void pesquisarPacientesEmTransOperario(){
		if(visualizarAlertaPacienteTransOperatorio){
			List<CirurgiaComPacEmTransOperatorioVO> novaLista = blocoCirurgicoFacade.pesquisarCirurgiasComPacientesEmTransOperatorio(DominioSituacaoCirurgia.TRAN,
							DominioOrigemPacienteCirurgia.I, Boolean.FALSE, cirurgiasComPacientesEmTransOperatorio, tela.getUnidade());
			playAlertaNovoPacEmTransOperatorio = Boolean.FALSE;
			if(novaLista != null && cirurgiasComPacientesEmTransOperatorio != null){
				//Verifica se tem cirurgias NOVAS
				for(CirurgiaComPacEmTransOperatorioVO vo: novaLista){
					if(!cirurgiasComPacientesEmTransOperatorio.contains(vo)){
						playAlertaNovoPacEmTransOperatorio = Boolean.TRUE;
					}
				}
			}
			//entrada na tela encontrou registros em transoperatorios
			if(novaLista != null && cirurgiasComPacientesEmTransOperatorio == null){
				playAlertaNovoPacEmTransOperatorio = Boolean.TRUE;
			}
			cirurgiasComPacientesEmTransOperatorio = novaLista;
			if (playAlertaNovoPacEmTransOperatorio){
				RequestContext.getCurrentInstance().execute("playAlertaPacienteEmTransOperatorio()");
			}
		}
	}
	
	public String cirurgiaDaPesquisaEmTransOperatorio(CirurgiaVO cirgVoSelec){
		try{
			if (cirgVoSelec != null){
				if(visualizarAlertaPacienteTransOperatorio &&
					cirurgiasComPacientesEmTransOperatorio != null &&
					cirurgiasComPacientesEmTransOperatorio.contains(new CirurgiaComPacEmTransOperatorioVO(cirgVoSelec.getCrgSeq()))){
					//Necessário atualizar todo registro
					List<CirurgiaVO> listaCirurgiaVO = blocoCirurgicoFacade.pesquisarCirurgias(tela, null, cirgVoSelec.getCrgSeq());
					cirgVoSelec  = listaCirurgiaVO.get(0);
					if(cirurgias.indexOf(cirgVoSelec) >= 0) {
						cirurgias.set(cirurgias.indexOf(cirgVoSelec), cirgVoSelec);
					}
					return CirurgiaVO.VERMELHO;
				}else{
					return cirgVoSelec.getCorExibicao();
				}
			}
		
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
		return null;
	}
	
	public void collapseTogglePanelTransOper(CirurgiaComPacEmTransOperatorioVO cirgEmtransOper){
		cirgEmtransOper.setPanelAberto(!cirgEmtransOper.getPanelAberto());
	}
	
	public Boolean disabledBtnInternar(Integer pacCodigoSelec) {
		if(pacCodigoSelec == null){
			return true;
		}
		AipPacientes paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigoSelec);
		if (paciente.getDtObito() != null
				|| paciente.getTipoDataObito() != null
				|| paciente.getProntuario() == null) {
			return true;
		}
		return false;
	}
	
	public void iniciar() {
		
		visualizarAlertaPacienteTransOperatorio =  securityController.usuarioTemPermissao("visualizarAlertaPacienteTransOperatorio", "visualizar");
		if(legendaTitles == null){
			inicializaLegenda();
		}
		
		if(Boolean.TRUE.equals(refreshComCamposPesquisaFechados)){
			togglePesquisaOpened = Boolean.FALSE;
		}else{
			togglePesquisaOpened = Boolean.TRUE;
		}
		
		if(seqSolicitacaoInternacao != null){//User voltou da String de internação
			this.apresentarMsgNegocio(Severity.INFO, 
					"MENSAGEM_SUCESSO_PERSISTIR_SOLICITACAO_INTERNACAO", this.seqSolicitacaoInternacao);
			seqSolicitacaoInternacao = null;
		}

		
		if (tela == null) {
			carregarTela();
		} 
		
		if(this.isDispararPesquisa() || tela.getUnidade() != null ){
			pesquisar();
		}
		
		try {
			tempoRecarregarCirurgiasEmTransOperatorio = ((parametroFacade
					.obterAghParametro(AghuParametrosEnum.P_AGHU_INTERVALO_RECARREGAR_CIRURGIAS).getVlrNumerico().longValue()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		this.setExisteIntegracaoAghWebAGHU(this.verificaSeHaIntegracaoEntreAGHUeAghWEB());

		this.popularParametros();
		this.botaoPrescreverHabilitado = this.habilitaBotaoPrescrever();
		
		
		try{
			direcionaAghDocs = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_DIRECIONA_AGH_DOCS).equalsIgnoreCase("S");
			
			if (direcionaAghDocs) {
				if (urlPendenciaDigital == null || StringUtils.isBlank(urlPendenciaDigital)) {
					urlPendenciaDigital = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_ENDERECO_WEB_CERTIF_COMPL); 
				}
			}
		} catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
		
	}

	private void inicializaLegenda() {
		legendaIcos = new HashMap<String, String>();
		legendaIcos.put("FOLHA_DUPLA_EVOLUCAO", CirurgiaVO.FOLHA_DUPLA_EVOLUCAO);
		legendaIcos.put("FOLHA_VERMELHO_PREENCHIDA", CirurgiaVO.FOLHA_VERMELHO_PREENCHIDA);
		legendaIcos.put("FOLHA_PREENCHIDA", CirurgiaVO.FOLHA_PREENCHIDA);
		legendaIcos.put("FOLHA_DUPLA", CirurgiaVO.FOLHA_DUPLA);
		legendaIcos.put("FOLHA_BRANCO", CirurgiaVO.FOLHA_BRANCO);
		legendaIcos.put("PRANCHETA_VERMELHA", CirurgiaVO.PRANCHETA_VERMELHA);
		legendaIcos.put("PRANCHETA_BRANCA", CirurgiaVO.PRANCHETA_BRANCA);
		legendaIcos.put("PRANCHETA_AMARELA", CirurgiaVO.PRANCHETA_AMARELA);
		legendaIcos.put("PACIENTE_SEM_INT", CirurgiaVO.PACIENTE_SEM_INT);
		legendaIcos.put("CHIP", CirurgiaVO.CHIP);
		legendaIcos.put("REALIZADA", CirurgiaVO.REALIZADA);
		legendaIcos.put("MEDICO", CirurgiaVO.MEDICO);
		legendaIcos.put("TELEFONE", CirurgiaVO.TELEFONE);
		legendaIcos.put("AGENDA", CirurgiaVO.AGENDA);
		legendaIcos.put("PACIENTE_CAMA", CirurgiaVO.PACIENTE_CAMA);
		legendaIcos.put("CANCELADA", CirurgiaVO.CANCELADA);
		legendaIcos.put("PROJ_PESQ", CirurgiaVO.PROJ_PESQ);
		legendaIcos.put("CERIH", CirurgiaVO.CERIH);
		legendaIcos.put("GMR", CirurgiaVO.GMR);
		legendaIcos.put("SILK_BLUE", CirurgiaVO.SILK_BLUE);
		legendaTitles = new HashMap<String, String>();
		legendaTitles.put("TITLE_FOLHA_DUPLA_EVOLUCAO", CirurgiaVO.TITLE_FOLHA_DUPLA_EVOLUCAO);
		legendaTitles.put("TITLE_FOLHA_VERMELHO_PREENCHIDA", CirurgiaVO.TITLE_FOLHA_VERMELHO_PREENCHIDA);
		legendaTitles.put("TITLE_FOLHA_PREENCHIDA", CirurgiaVO.TITLE_FOLHA_PREENCHIDA);
		legendaTitles.put("TITLE_FOLHA_DUPLA", CirurgiaVO.TITLE_FOLHA_DUPLA);
		legendaTitles.put("TITLE_FOLHA_BRANCO", CirurgiaVO.TITLE_FOLHA_BRANCO);
		legendaTitles.put("TITLE_PRANCHETA_VERMELHA", CirurgiaVO.TITLE_PRANCHETA_VERMELHA);
		legendaTitles.put("TITLE_PRANCHETA_BRANCA", CirurgiaVO.TITLE_PRANCHETA_BRANCA);
		legendaTitles.put("TITLE_PRANCHETA_AMARELA", CirurgiaVO.TITLE_PRANCHETA_AMARELA);
		legendaTitles.put("TITLE_PACIENTE_SEM_INT", CirurgiaVO.TITLE_PACIENTE_SEM_INT);
		legendaTitles.put("TITLE_CHIP", CirurgiaVO.TITLE_CHIP);
		legendaTitles.put("TITLE_REALIZADA", CirurgiaVO.TITLE_REALIZADA);
		legendaTitles.put("TITLE_MEDICO", CirurgiaVO.TITLE_MEDICO);
		legendaTitles.put("TITLE_TELEFONE", CirurgiaVO.TITLE_TELEFONE);
		legendaTitles.put("TITLE_AGENDA", CirurgiaVO.TITLE_AGENDA);
		legendaTitles.put("TITLE_PACIENTE_CAMA", CirurgiaVO.TITLE_PACIENTE_CAMA);
		legendaTitles.put("TILE_CANCELADA", CirurgiaVO.TILE_CANCELADA);
		legendaTitles.put("TITLE_PROJ_PESQ", CirurgiaVO.TITLE_PROJ_PESQ);
		legendaTitles.put("TITLE_CERIH", CirurgiaVO.TITLE_CERIH);
		legendaTitles.put("TITLE_GMR", CirurgiaVO.TITLE_GMR);
		legendaTitles.put("TITLE_SILK_BLUE", CirurgiaVO.TITLE_SILK_BLUE);
	}

	private void popularParametros() {
		try {
			if (aghuFacade.isHCPA()){
				AghParametros aghParametroUrlBaseWebForms = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS);
				if (aghParametroUrlBaseWebForms != null) {
					urlBaseWebForms = aghParametroUrlBaseWebForms.getVlrTexto();
				}
				AghParametros aghParametrosBanco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
				if (aghParametrosBanco != null) {
					banco = aghParametrosBanco.getVlrTexto();
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void carregarTela() {
		try {
			crgSelecionada = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção capturada:", e);
			}
			if(loginUsuarioLogado == null){
				loginUsuarioLogado = obterLoginUsuarioLogado();
			}
			if(servidorLogado == null){
				servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(loginUsuarioLogado,new Date());
			}
			tela = blocoCirurgicoFacade.inicializarTelaListaCirurgiaVO(nomeMicrocomputador, true);
			liberarBloquearPesquisa();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		} 
	}
	
	public Boolean validarUrlBaseWebFormsBanco(){
		return StringUtils.isBlank(urlBaseWebForms) || StringUtils.isBlank(banco);
	}
	
	public void novaPesquisa() {	
		RequestContext.getCurrentInstance().execute("PF('filtroToggleWG').toggle();");
		RequestContext.getCurrentInstance().execute("PF('listaCirurgiasWG').clearFilters();");
		this.seqCrgSelecionada = null;
		this.crgSelecionada = null;
        limparFiltroListaCirurgias();
		this.pesquisar();		
		
	
	}
	
	private void limparPesquisaAjax() {
		if (!paginaOrigem.equalsIgnoreCase(PAGE_DESCRICAO_PDT)) {
			//Necessesario para limpar as chamadas ajax da tela do cliente, sem isso pode ser passado parametros errados. Causando para o usuário editar paciente
			RequestContext.getCurrentInstance().execute("PF('listaCirurgiasWG').clearFilters();");		
		} else {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DESCRICAO_PROC_DIAG_TERAP_LIBERAR_LAUDO_DEFINITVO_SUCESSO");
			if (StringUtils.isNotBlank(mensagemTempoCirurgiaMinimo)) {
				this.apresentarMsgNegocio(Severity.INFO, "MBC_01096");
			}
		}
	}
	
	public void pesquisar() {
		try {

			limparPesquisaAjax();
			this.botaoPrescreverHabilitado = this.habilitaBotaoPrescrever();
			cirurgias = blocoCirurgicoFacade.pesquisarCirurgias(tela, null, null);			
			tela.setPesquisaAtiva(Boolean.TRUE);
			liberarBloquearPesquisa();
			unfSeq = tela.getUnidade().getSeq();
			cirurgiasComPacientesEmTransOperatorio = null;
			if (!cirurgias.isEmpty()) {
				togglePesquisaOpened = Boolean.FALSE;	
			}
			pesquisarPacientesEmTransOperario();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			tela.setPesquisaAtiva(false);
		} 
		
	}

    public void limparFiltroListaCirurgias() {
        this.filtroPaciente = null;
        this.filteredCirurgias = null;
    }

	public void limpar() {
        limparFiltroListaCirurgias();
		this.botaoPrescreverHabilitado = false;
		dispararPesquisa = Boolean.FALSE;
		cirurgias = new ArrayList<CirurgiaVO>();
		currentSortProperty = null;
		seqCrgSelecionada = null;
		crgSelecionada = null;
		tela = null;
		cirurgiasComPacientesEmTransOperatorio = null;
		playAlertaNovoPacEmTransOperatorio = Boolean.FALSE;
		isImpPreAne = Boolean.FALSE;
		iniciar();
		this.setLiberarCamposDoFiltro(false);
	}
	public void liberarBloquearPesquisa() {
		tela.setPesquisaLiberada(tela.getUnidade() != null && tela.getDtProcedimento() != null);
		if (!tela.isPesquisaLiberada()) {
			cirurgias = new ArrayList<CirurgiaVO>();
		}
	}
	
	public void liberarCamposDoFiltroListaCirurgias(){
		this.setLiberarCamposDoFiltro(tela.getUnidade() != null && tela.getDtProcedimento() != null);	
	}
	
	public void limparlistaCirurgias(){
		cirurgias = new ArrayList<CirurgiaVO>();
		tela.setPesquisaAtiva(false);
		liberarCamposDoFiltroListaCirurgias();
	}

	public void posDeleteUnidadeFuncional() throws ApplicationBusinessException{
		//tela = null;
		//tela = blocoCirurgicoFacade.inicializarTelaListaCirurgiaVO(nomeMicrocomputador, false);
		this.tela.setPesquisaLiberada(false);
		this.setLiberarCamposDoFiltro(false);
		this.limparFiltroListaCirurgias();
	}
	
	public void collapseTogglePesquisa() {
		if (Boolean.TRUE.equals(togglePesquisaOpened)) {
			togglePesquisaOpened = Boolean.FALSE;
		} else {
			togglePesquisaOpened = Boolean.TRUE;
		}
	}
	private CirurgiaVO obterCirurgiaSelecionada(Integer seqSelecionada) {
		for (CirurgiaVO cirurgiaVO : cirurgias) {
			if (cirurgiaVO.getCrgSeq().equals(seqSelecionada)) {
				return cirurgiaVO;
			}
		}
		return null;
	}
	public void carregarCirurgiaSelecionada() {
		seqCrgSelecionada = crgSelecionada.getCrgSeq();
		crgSelecionada = null;
		
 		if (seqCrgSelecionada != null) {
 			crgSelecionada = obterCirurgiaSelecionada(seqCrgSelecionada);
 			pacCodigoSelecionado = crgSelecionada.getPacCodigo();
 			prontuarioSelecionado = crgSelecionada.getProntuario();
 			atdSeqSelecionado = crgSelecionada.getAtdSeq();
			popularCamposCancelamento();
			habilitarBotaoAvaliacaoPreAnestesica(crgSelecionada);
		}
 		
 //		this.processarUrlBaseAnestesia();
 		this.botaoPrescreverHabilitado = this.habilitaBotaoPrescrever();
	}
	private void popularCamposCancelamento() {
		if (this.crgSelecionada != null) {
			try {
				blocoCirurgicoFacade.popularCancelamentoCirurgia(tela, crgSelecionada);
				motivoCancelamentoVO = crgSelecionada.getMotivoCancelamento();
				questao = crgSelecionada.getQuestao();
				if(crgSelecionada.getValorValido() != null){
					valorValidoCanc = blocoCirurgicoFacade.obterMbcValorValidoCancById(crgSelecionada.getValorValido().getId());
				}else {
				valorValidoCanc = crgSelecionada.getValorValido();
				}
				complementoCanc = crgSelecionada.getComplementoCanc();
				this.popularListaValoresQuestaoEComplemento();
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);				
			}
		}
	}
	public void popularListaValoresQuestaoEComplemento() {
		if (this.crgSelecionada != null) {
			exibeModalCancelarCrgComDescricao = blocoCirurgicoFacade.verificarCancelamentoCirurgiaComDescricao(this.crgSelecionada.getCrgSeq());
			if (motivoCancelamentoVO != null) {
				blocoCirurgicoFacade.popularListaValoresQuestaoEComplemento(crgSelecionada, motivoCancelamentoVO.getSeq());
				questao = crgSelecionada.getQuestao();
				if(crgSelecionada.getValorValido() != null){
					valorValidoCanc = blocoCirurgicoFacade.obterMbcValorValidoCancById(crgSelecionada.getValorValido().getId());
				}else {
					valorValidoCanc = crgSelecionada.getValorValido();
				}
				valorValidoCanc = crgSelecionada.getValorValido();
				complementoCanc = crgSelecionada.getComplementoCanc();
			} else {
				questao = null;
				valorValidoCanc = null;
				complementoCanc = null;
			}
		}
	}
	public void fecharModalCancelarCrgComDescricao() {
		exibeModalCancelarCrgComDescricao = Boolean.FALSE;
	}	
	
	public void fecharModalCancelarCrgComDescricaoDesfazer() {
		exibeModalCancelarCrgComDescricao = Boolean.FALSE;
		popularCamposCancelamento();
	}
	
	public void prepararAlterarCirurgia(){
		try {
			blocoCirurgicoFacade.verificarCirurgiaPossuiDescricaoCirurgica(tela, crgSelecionada);
			alterarCirurgia();
		} catch (BaseException e) {
			openDialog("modalConfirmacaoCancelamentoWG");
		}
	}
	
	public void alterarCirurgia() {
		boolean limparFiltrosCirurgia = true;
		if (this.crgSelecionada != null) {
			MbcMotivoCancelamento motivoCancelamento = blocoCirurgicoFacade.obterMotivoCancelamentoPorChavePrimaria(motivoCancelamentoVO.getSeq());
			MbcCirurgias cirurgia = blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(crgSelecionada.getCrgSeq());
			cirurgia.setMotivoCancelamento(motivoCancelamento);
			cirurgia.setQuestao(questao);
			cirurgia.setValorValidoCanc(valorValidoCanc);
			cirurgia.setComplementoCanc(complementoCanc);
			
			try {
				
				blocoCirurgicoFacade.verificarPermissaoCancelamentoCirurgia(tela, crgSelecionada);
				blocoCirurgicoFacade.validarQuestaoValorValidoEComplemento(questao, valorValidoCanc, complementoCanc);
				blocoCirurgicoFacade.persistirCirurgia(cirurgia, servidorLogado);				
	//TODO
				blocoCirurgicoFacade.verificarPacienteSalaPreparo(crgSelecionada.getCrgSeq(), nomeMicrocomputador, servidorLogado.getDtFimVinculo());
				
				this.apresentarMsgNegocio(Severity.INFO, 
						"MENSAGEM_SUCESSO_ALTERAR_CIRURGIA");
				obterCirurgiaAtualizada();
				popularCamposCancelamento();
				
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);				
				limparFiltrosCirurgia = false;
			}	
		}
		if(limparFiltrosCirurgia){
			RequestContext.getCurrentInstance().execute("PF('listaCirurgiasWG').filter();");
		}
		exibeModalCancelarCrgComDescricao = Boolean.FALSE;
	}
	private void obterCirurgiaAtualizada() {
		if (this.crgSelecionada != null) {
			try {
				List<CirurgiaVO> listaCirurgiaVO = blocoCirurgicoFacade.pesquisarCirurgias(tela, null, this.crgSelecionada.getCrgSeq());
				if (!listaCirurgiaVO.isEmpty()) {
					crgSelecionada = listaCirurgiaVO.get(0);
					cirurgias.set(cirurgias.indexOf(crgSelecionada), crgSelecionada);
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);				
			}
		}
	}
	
	private boolean habilitaBotaoPrescrever(){
		if (this.crgSelecionada != null && this.crgSelecionada.getCrgSeq() != null) {
		    try {
				return this.blocoCirurgicoFacade.habilitaBotaoPrescrever(this.crgSelecionada);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} 
		}
		return false;
		
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String strPesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristica( (String)strPesquisa, CARACTERISTICAS_UNIDADE_FUNCIONAL, DominioSituacao.A, Boolean.FALSE),pesquisarUnidadeFuncionalCount(strPesquisa));
	}
	
	public Long pesquisarUnidadeFuncionalCount(String strPesquisa) {
		return aghuFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristicaCount( (String)strPesquisa, CARACTERISTICAS_UNIDADE_FUNCIONAL, DominioSituacao.A, Boolean.FALSE);
	}
	
	public List<SuggestionListaCirurgiaVO> listarEspecialidades(final String strPesquisa) {
		return this.aghuFacade.pesquisarEspecialidadesCirurgicas((String) strPesquisa, tela.getUnidade(),  tela.getDtProcedimento(), DominioSituacao.A);
	}
	
	public Long listarEspecialidadesCount(final String strPesquisa) {
		return this.aghuFacade.pesquisarEspecialidadesCirurgicasCount((String) strPesquisa, tela.getUnidade(),  tela.getDtProcedimento(), DominioSituacao.A);
	}
	
	
	public List<MbcSalaCirurgica> buscarSalasCirurgicas(final String filtro){
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade.buscarSalasCirurgicas( (String) filtro, tela.getUnidade().getSeq(), DominioSituacao.A),buscarSalasCirurgicasCount(filtro));
	}
	
	public Long buscarSalasCirurgicasCount(final String filtro){
		return blocoCirurgicoCadastroApoioFacade.buscarSalasCirurgicasCount( (String) filtro, tela.getUnidade().getSeq(), DominioSituacao.A);
	}
		
	public List<SuggestionListaCirurgiaVO> pesquisarSuggestionEquipe(final String filtro){
		return 
			this.returnSGWithCount(
				blocoCirurgicoFacade.pesquisarSuggestionEquipe(tela.getUnidade(), tela.getDtProcedimento(), (String) filtro, true),
				blocoCirurgicoFacade.pesquisarSuggestionEquipeCount(tela.getUnidade(), tela.getDtProcedimento(), (String) filtro, true)
			);
	}
		
	public List<SuggestionListaCirurgiaVO> pesquisarSuggestionProcedimento(final String filtro){
		return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarSuggestionProcedimento( tela.getUnidade(),  tela.getDtProcedimento(),  (String) filtro,  tela.getEspecialidade() != null ? tela.getEspecialidade().getSeq() : null, 
																	 DominioSituacao.A,  true  ),pesquisarSuggestionProcedimentoCount(filtro));
	}
	
	public Long pesquisarSuggestionProcedimentoCount(final String filtro){
		return blocoCirurgicoFacade.pesquisarSuggestionProcedimentoCount( tela.getUnidade(),  tela.getDtProcedimento(),  (String) filtro,  tela.getEspecialidade() != null ? tela.getEspecialidade().getSeq() : null, 
																	 DominioSituacao.A,  true  );
	}
	
	
	public boolean moduloExamesLaudoNaoEstaAtivoEEhHospitalClinicas(){
		return !cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.EXAMES_LAUDOS.getDescricao()) && existeIntegracaoAghWebAGHU;
	}
	
	public boolean moduloPrescricaoEnfermagemNaoEstaAtivoEEhHospitalClinicas(){
		return !cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.PRESCRICAO_ENFERMAGEM.getDescricao()) && existeIntegracaoAghWebAGHU;
	}
	
	public boolean moduloPrescricaoMedicaNaoEstaAtivoEEhHospitalClinicas(){
		Boolean retorno = !cascaFacade.verificarSeModuloEstaAtivo(DominioModulo.PRESCRICAO_MEDICA.getDescricao())  && existeIntegracaoAghWebAGHU;
		/*if(!retorno && crgSelecionada != null && crgSelecionada.getAtdSeq() != null){
			AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(crgSelecionada.getAtdSeq());
			if(!DominioOrigemAtendimento.C.equals(atendimento.getOrigem())){
				return Boolean.TRUE;
			}
		}*/
		return retorno;
	}
	
	public String redirecionarPrescricaoMedica() {
		String retorno = null;
		if (this.crgSelecionada != null) {
			if(crgSelecionada.getProntuario() == null){// #40540
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PACIENTE_NAO_POSSUI_PRONTUARIO_PROVIDENCIAR");
				return null;
			}
			List<MbcCirurgias> cirurgiasPmeInformatizada = blocoCirurgicoFacade.pesquisarCirurgiaPorCaractUnidFuncionais(crgSelecionada.getCrgSeq(), ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA);
			if(cirurgiasPmeInformatizada != null && !cirurgiasPmeInformatizada.isEmpty()){
				if (crgSelecionada.getAtdSeq() == null) {
					this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CIRURGIA_REDIRECIONAR_PACIENTE_PRESCRICAO");
				}/* else if (prontuarioSelecionado == null) {
					this.apresentarMsgNegocio(Severity.ERROR, 
							"MENSAGEM_CIRURGIA_REDIRECIONAR_PRONTUARIO_NULO", "Solicitação de Exame");
				}*/ else {
					retorno = PAGE_PRESCR_MED_VER_PRESCR_MED;
				}
			}else{
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_UNF_SEM_CARACT_PME_INFORMATIZADA");
			}
		}
		return retorno;	
	}
	
	public String redirecionarPrescricaoEnfermagem(){
		if (this.crgSelecionada != null) {
			atdSeqSelecionado = crgSelecionada.getAtdSeq();	
			if (atdSeqSelecionado == null) {
				this.apresentarMsgNegocio(Severity.ERROR, 
						"MENSAGEM_CIRURGIA_REDIRECIONAR_PACIENTE_PRESCRICAO");
			} else if (prontuarioSelecionado == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CIRURGIA_REDIRECIONAR_PRONTUARIO_NULO", "Solicitação de Exame");
			} else {
				return PAGE_PRESCR_ENF_ELABORAR_PRESCRICAO_ENF;
			}
		}
		return null;
	}
	
	public String redirecionarSolicitacaoExame() {
		if (this.crgSelecionada != null) {
			blocoCirurgicoFacade.executarEventoBotaoPressionadoListaCirurgias(crgSelecionada);
			atdSeqSelecionado = crgSelecionada.getAtdSeq();	
			if (atdSeqSelecionado == null) {
				this.apresentarMsgNegocio(Severity.ERROR, 
						"MENSAGEM_CIRURGIA_REDIRECIONAR_PACIENTE_NAO_ESTA_EM_ATENDIMENTO");
			} else if (prontuarioSelecionado == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CIRURGIA_REDIRECIONAR_PRONTUARIO_NULO", "Solicitação de Exame");
			} else {
				try {
					// Valida as permissões de negócio para solicitar exame
					atendimento = this.solicitacaoExameFacade.verificarPermissoesParaSolicitarExame(atdSeqSelecionado);
					solicitacaoExameController.setAtendimento(atendimento);
                    solicitacaoExameController.setAtendimentoSeq(atdSeqSelecionado);
				return PAGE_EXAMES_SOLIC_EXAMES;
				} catch (BaseException e) {
					super.apresentarExcecaoNegocio(e);
			}
		}
		}
		return null;
	}
	
	public String redirecionarPesquisaFluxograma() {
		if (prontuarioSelecionado == null) {
			this.apresentarMsgNegocio(Severity.ERROR, 
					"MENSAGEM_CIRURGIA_REDIRECIONAR_PRONTUARIO_NULO", "Pesquisa de Fluxograma");
		} else {
			return PAGE_EXAMES_PESQ_FLUXOGRAMA;
		}
		return null;
	}
	
	public String redirecionarVisualizarRegistros(){
		if(atdSeqSelecionado != null){
			return PAGE_CONTR_PACIENTE_VISUALIZAR_REG;	
		}
		return null;		
	}
	
	
	public String redirecionarCadastroAnotacoes(){
		return PAGE_CADASTRO_ANOTACOES;
	}
	
	public boolean habilitaBotaoVisualizarRegistros() throws ApplicationBusinessException{
		return this.crgSelecionada != null ? blocoCirurgicoFacade.habilitaBotaoVisualizarRegistros(
				this.atdSeqSelecionado, this.tela.isCheckSalaRecuperacao(), this.crgSelecionada.getDataInicioCirurgia()) : false;
	}
	
	public boolean habilitaBotaoEvolucao(boolean perfilEnfermeiro, boolean perfilMedico) {
		if(perfilEnfermeiro == true || perfilMedico == true){
			if (this.atdSeqSelecionado != null) {
				return existeIntegracaoAghWebAGHU;
			}	
		}
		return false;
	}
	
	private boolean verificaSeHaIntegracaoEntreAGHUeAghWEB(){
		try {
			AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_INTEGRACAO_AGH_ORACLE_WEBFORMS);
			if (aghParametros != null) {
				String str = aghParametros.getVlrTexto();
				if (str != null && str.equalsIgnoreCase("S")) {
					return true;
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return false;
	}
	
	public String getBanco(){
		return banco;
	}
	
	public String redirecionarDescricaoCirurgica(CirurgiaVO cirurgiaVO) {
		try {
			this.crgSelecionada = cirurgiaVO;
			
			Object descricao = blocoCirurgicoFacade.executarDescreverCirurgiaOuOutra(tela, cirurgiaVO);			
			if (descricao != null) {
				if (descricao instanceof MbcDescricaoCirurgica) {
					MbcDescricaoCirurgica descricaoCirurgica = (MbcDescricaoCirurgica) descricao;
					setarParametrosDescricaoCirurgica(descricaoCirurgica);
										
					descricaoCirurgicaController.setDcgCrgSeq(dcgCrgSeq);
					descricaoCirurgicaController.setDcgSeqp(dcgSeqp);
					descricaoCirurgicaController.setUnfSeq(unfSeq);
					descricaoCirurgicaController.setCrgSelecionada(cirurgiaVO);
					descricaoCirurgicaController.setAbaSelecionada(null);
					descricaoCirurgicaController.setIsEdicao(false);
					
					return PAGE_DESCRICAO_CIRURGICA;
					
				} else if (descricao instanceof PdtDescricao) {
					PdtDescricao descricaoPdt = (PdtDescricao) descricao;
					setarParametrosDescricaoPdt(descricaoPdt);
					descricaoProcDiagTerapController.setDdtCrgSeq(ddtCrgSeq);
					descricaoProcDiagTerapController.setDdtSeq(ddtSeq);
					descricaoProcDiagTerapController.setUnfSeq(unfSeq);
					descricaoProcDiagTerapController.setCrgSelecionada(cirurgiaVO);
					descricaoProcDiagTerapController.setAbaSelecionada(null);
					return PAGE_DESCRICAO_PDT;
				}
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
		return null;
	}
	
	public String redirecionarAcompanharCirurgia() {		
		return PAGE_ACOMPANHAMENTO_CIRURGIA;
	}
	
	public String redirecionarLaudoAih( ) {
		if (this.crgSelecionada != null) {
			pacCodigo = crgSelecionada.getPacCodigo();
		}
		return PAGE_LAUDO_AIH;
	}
	
	public boolean habilitaBotoesDescricaoCirurgica(boolean edicao, boolean permissaoDescricaoCirurgica, boolean permissaoDescricaoCirurgicaPDT){
		return (permissaoDescricaoCirurgica || permissaoDescricaoCirurgicaPDT) && edicao;
	}
	
	public String redirecionarEdicaoDescricaoCirurgica(CirurgiaVO cirurgiaVO) {
		try {
			this.crgSelecionada = cirurgiaVO;
			
			Object descricao = blocoCirurgicoFacade.executarEdicaoDescricaoCirurgica(tela, cirurgiaVO);			
			if (descricao != null) {
				if (descricao instanceof MbcDescricaoCirurgica) {
					MbcDescricaoCirurgica descricaoCirurgica = (MbcDescricaoCirurgica) descricao;
					setarParametrosDescricaoCirurgica(descricaoCirurgica);
					
					descricaoCirurgicaController.setDcgCrgSeq(dcgCrgSeq);
					descricaoCirurgicaController.setDcgSeqp(dcgSeqp);
					descricaoCirurgicaController.setUnfSeq(unfSeq);
					descricaoCirurgicaController.setCrgSelecionada(cirurgiaVO);
					descricaoCirurgicaController.setAbaSelecionada(null);
					descricaoCirurgicaController.setIsEdicao(true);
					
					return PAGE_DESCRICAO_CIRURGICA;
					
				} else if (descricao instanceof PdtDescricao) {
					PdtDescricao descricaoPdt = (PdtDescricao) descricao;
					setarParametrosDescricaoPdt(descricaoPdt);
					
					descricaoProcDiagTerapController.setDdtCrgSeq(ddtCrgSeq);
					descricaoProcDiagTerapController.setDdtSeq(ddtSeq);
					descricaoProcDiagTerapController.setUnfSeq(unfSeq);
					descricaoProcDiagTerapController.setCrgSelecionada(cirurgiaVO);
					descricaoProcDiagTerapController.setAbaSelecionada(null);
					return PAGE_DESCRICAO_PDT;
				}
				
			} else {
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_SERVIDOR_SEM_DESCRICAO_CIRURGICA","editar");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
		return null;
	}
	
	public String visualizarDescricaoCirurgicaOuPDT(CirurgiaVO cirurgiaVO) {
		this.crgSelecionada = cirurgiaVO;
		if(crgSelecionada != null){			
			try {
				Object descricao = blocoCirurgicoFacade.executarEdicaoDescricaoCirurgica(tela, cirurgiaVO);
			
				if (descricao != null) {
					if (descricao instanceof MbcDescricaoCirurgica) {
						MbcDescricaoCirurgica descricaoCirurgica = (MbcDescricaoCirurgica) descricao;
						setarParametrosDescricaoCirurgica(descricaoCirurgica);
											
						MbcAvalPreSedacaoId id = new MbcAvalPreSedacaoId(dcgCrgSeq, dcgSeqp);
						MbcAvalPreSedacao avalPreSedacao = blocoCirurgicoFacade.pesquisarMbcAvalPreSedacaoPorDdtSeq(id);
						ProfDescricaoCirurgicaVO executorSedacao = blocoCirurgicoFacade.obterProfissionalDescricaoAnestesistaPorDescricaoCirurgica(dcgCrgSeq, dcgSeqp);
						this.descricaoCirurgicaController.obterDadosAvalPreSedacao(avalPreSedacao, executorSedacao);		
					} else if (descricao instanceof PdtDescricao) {
						PdtDescricao descricaoPdt = (PdtDescricao) descricao;
						setarParametrosDescricaoPdt(descricaoPdt);
						descricaoProcDiagTerapSedacaoController.setDdtSeq(ddtSeq);
						descricaoProcDiagTerapSedacaoController.setUnfSeq(unfSeq);
						descricaoProcDiagTerapSedacaoController.populaSedacao();
					}
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);			
			}
			
			relatorioDescricaoCirurgiaController.setAvaliacaoPreSedacaoVO(this.descricaoCirurgicaController.getAvaliacaoPreSedacaoVO());
			relatorioDescricaoCirurgiaController.setCrgSeq(crgSelecionada.getCrgSeq());
			relatorioDescricaoCirurgiaController.setVoltarPara(PAGE_BLOCO_LISTAR_CIRURGIAS);
			relatorioListarCirurgiasPdtDescProcCirurgiaController.setAvaliacaoPreSedacaoVO(descricaoProcDiagTerapSedacaoController.getAvaliacaoPreSedacaoVO());
			relatorioListarCirurgiasPdtDescProcCirurgiaController.setDdtSeq(null);
			relatorioListarCirurgiasPdtDescProcCirurgiaController.setCrgSeq(crgSelecionada.getCrgSeq());
			relatorioListarCirurgiasPdtDescProcCirurgiaController.setVoltarPara(PAGE_BLOCO_LISTAR_CIRURGIAS);
			try {
				return "blococirurgico-relatorio-".concat(this.blocoCirurgicoFacade.mbcImpressao(crgSelecionada.getCrgSeq()));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);				
			}
		}	
		return null;
	}
	
	private void setarParametrosDescricaoCirurgica(MbcDescricaoCirurgica descricaoCirurgica) {
		dcgCrgSeq = descricaoCirurgica.getId().getCrgSeq();
		dcgSeqp = descricaoCirurgica.getId().getSeqp();
	}
	
	private void setarParametrosDescricaoPdt(PdtDescricao descricaoPdt) {
		MbcCirurgias cirurgia = descricaoPdt.getMbcCirurgias();
		if (cirurgia != null) {
			ddtCrgSeq = cirurgia.getSeq();
		}
		ddtSeq = descricaoPdt.getSeq();
	}

	public TelaListaCirurgiaVO getTela() {
		return tela;
	}
	
	//@Restrict("#{s:hasPermission('assinaturaDigital','alterarContextoProfissional')}")
	public String redirecionarListarPendenciasAssinatura(Integer pacCodigo) {
		RapServidores profissional;
		try {
			profissional = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			profissional = null;
		}

		boolean habilitado = this.certificacaoDigitalFacade.verificaProfissionalHabilitado();

		if (habilitado) {
			final AipPacientes pac = pacienteFacade.buscaPaciente(pacCodigo);
			final Long count = this.certificacaoDigitalFacade.listarPendentesResponsavelPacienteCount(profissional, pac);
			
			if(count != null && count.intValue() > 0 ){
				this.setTipo("1");
			}else {
				this.setTipo("3");
			}
			
			listarPendenciasAssinaturaPaginatorController.setTipo(this.getTipo());
			listarPendenciasAssinaturaPaginatorController.setPacCodigo(pacCodigo);
			listarPendenciasAssinaturaPaginatorController.setVoltarPara(PAGE_BLOCO_LISTAR_CIRURGIAS);
			
			return PAGE_REDIRECIONAR_LISTA_PENDENCIAS_ASSINATURA;
		}
		
		return null;
	}
	
	private void habilitarBotaoAvaliacaoPreAnestesica(CirurgiaVO crgSelecionada){
		if(crgSelecionada != null & tela.getUnidade() != null){
			try {
				if(blocoCirurgicoFacade.validarAvalicaoPreAnestesica(crgSelecionada.getCrgSeq())){
					isImpPreAne = Boolean.TRUE;
				}
				else {
					isImpPreAne = Boolean.FALSE;
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);				
			}
		} else{
			isImpPreAne = Boolean.FALSE;
		}
	}
	
	public String redirecionarRelatorioAvaliacaoPreAnestesica(){
		if (this.crgSelecionada != null && isImpPreAne){
			try {
				consultarAmbulatorioPOLController.setVoltarPara(PAGE_BLOCO_LISTAR_CIRURGIAS);
				consultarAmbulatorioPOLController.setProntuario(crgSelecionada.getProntuario());
				consultarAmbulatorioPOLController.setAtendimentosSelecionados(
						blocoCirurgicoFacade.validarExitenciaAvalicaoPreAnestesica(crgSelecionada.getPacCodigo()));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);				
				return null;
			}
			consultarAmbulatorioPOLController.setReadOnly(Boolean.TRUE);
			return imprimirRelatorioAvaliacaoPreAnestesica();
		}else{
			return null;
		}
	}
	
	private String  imprimirRelatorioAvaliacaoPreAnestesica() {
		String ret = null;
		try {
			ret = consultarAmbulatorioPOLController.print();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		} catch (JRException e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());			
		} catch (SystemException e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());			
		} catch (IOException e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());			
		}	
		return ret;
	}

	public Boolean habilitarAnestesia() {
		
		Boolean habilita = Boolean.FALSE;
		
		if (crgSelecionada == null || crgSelecionada.getCrgSeq() == null) {
			return Boolean.FALSE;
		}
		
		try {
			habilita = blocoCirurgicoFacade.habilitarAnestesia(crgSelecionada);
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		
		return habilita;
	}

	private String verificarExistenciaFichaAnestesica() throws BaseException {

		return  (crgSelecionada.getFichaPendente()!=null) ? DominioSimNao.S.toString() : DominioSimNao.N.toString();
		
	}
	
	public void processarUrlBaseAnestesia() {
		StringBuilder url = new StringBuilder(50) ;
		try {
				url.append( blocoCirurgicoFacade.obterUrlFichaAnestesica())
				.append("?aghw_token=")
				.append(obterTokenUsuarioLogado())
				.append("&ficha.seq=")
				.append(obterSeqFichaAnestesica())
				.append("&existeFicha=")
				.append(verificarExistenciaFichaAnestesica());
			} catch (BaseException e) {				
				super.apresentarExcecaoNegocio(e);
			}
			urlFichaAnestesica = url.toString();
			RequestContext.getCurrentInstance().execute("anestesia('"+urlFichaAnestesica+"')");
		}
		
	
	public String executarAnestesia() throws UnknownHostException {
		String nomeMicrocomputador, mensagemRetorno = "";
		
		if (this.crgSelecionada != null) {
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				mensagemRetorno = blocoCirurgicoFacade.identificarAnestesia(
												crgSelecionada.getCrgSeq(),	nomeMicrocomputador, crgSelecionada.getNomePaciente(),
												crgSelecionada.getCrgProcPrinc());
			} catch (BaseException e) {
				super.apresentarExcecaoNegocio(e);
			}
		}
		return mensagemRetorno;
	}
	
	public String obterTokenUsuarioLogadoAnestesia() {
		return super.obterTokenUsuarioLogado().toString();
	}

	private Integer obterSeqFichaAnestesica() throws ApplicationBusinessException {
	     if(crgSelecionada.getCrgSeq() != null) {
			try {
				return blocoCirurgicoFacade.obterSeqFichaAnestesica(crgSelecionada);
			} catch (BaseException e) {
				super.apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}
	
	public Object obterTokenUsuarioLogado() {
		return super.obterTokenUsuarioLogado();
	}
	
	public void renderizarBotoes(CirurgiaVO cir){
		setSeqCrgSelecionada(cir.getCrgSeq());
		setAtdSeqSelecionado(cir.getAtdSeq());
		setPacCodigo(cir.getPacCodigo());
		setPacCodigoSelecionado(cir.getPacCodigo());
		setProntuarioSelecionado(cir.getProntuario());
		this.carregarCirurgiaSelecionada();
	}
	
	
	public boolean habilitarContgExamesAGHWeb() {

		try {
			String habilitar = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_CONTINGENCIA_SOLIC_EXAMES_AGHWEB);

			return "S".equals(habilitar);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return false;
	}
	
	public String getUrlBaseWebForms() {
		return urlBaseWebForms;
	}
	
	public void setTela(TelaListaCirurgiaVO tela) { this.tela = tela; }
	 
	public List<CirurgiaVO> getCirurgias() { return cirurgias; }

	public void setCirurgias(List<CirurgiaVO> cirurgias) { this.cirurgias = cirurgias; }

	public String getCurrentSortProperty() { return currentSortProperty; }

	public void setCurrentSortProperty(String currentSortProperty) {
		this.currentSortProperty = currentSortProperty;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public Integer getSeqCrgSelecionada() {
		return seqCrgSelecionada;
	}

	public void setSeqCrgSelecionada(Integer seqCrgSelecionada) {
		this.seqCrgSelecionada = seqCrgSelecionada;		
	}

	public CirurgiaVO getCrgSelecionada() {
		return crgSelecionada;
	}

	public void setCrgSelecionada(CirurgiaVO crgSelecionada) {
		this.crgSelecionada = crgSelecionada;
	}

	public String getLoginUsuarioLogado() {
		return loginUsuarioLogado;
	}

	public void setLoginUsuarioLogado(String loginUsuarioLogado) {
		this.loginUsuarioLogado = loginUsuarioLogado;
	}

	public MotivoCancelamentoVO getMotivoCancelamentoVO() {
		return motivoCancelamentoVO;
	}

	public void setMotivoCancelamentoVO(MotivoCancelamentoVO motivoCancelamentoVO) {
		this.motivoCancelamentoVO = motivoCancelamentoVO;
	}

	public MbcValorValidoCanc getValorValidoCanc() {
		return valorValidoCanc;
	}

	public void setValorValidoCanc(MbcValorValidoCanc valorValidoCanc) {
		this.valorValidoCanc = valorValidoCanc;
	}

	public String getComplementoCanc() {
		return complementoCanc;
	}

	public void setComplementoCanc(String complementoCanc) {
		this.complementoCanc = complementoCanc;
	}

	public Integer getAtdSeqSelecionado() {
		return atdSeqSelecionado;
	}

	public void setAtdSeqSelecionado(Integer atdSeqSelecionado) {
		this.atdSeqSelecionado = atdSeqSelecionado;
	}

	public Integer getPacCodigoSelecionado() {
		return pacCodigoSelecionado;
	}

	public void setPacCodigoSelecionado(Integer pacCodigoSelecionado) {
		this.pacCodigoSelecionado = pacCodigoSelecionado;
	}

	public Integer getProntuarioSelecionado() {
		return prontuarioSelecionado;
	}

	public void setProntuarioSelecionado(Integer prontuarioSelecionado) {
		this.prontuarioSelecionado = prontuarioSelecionado;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getDcgCrgSeq() {
		return dcgCrgSeq;
	}

	public void setDcgCrgSeq(Integer dcgCrgSeq) {
		this.dcgCrgSeq = dcgCrgSeq;
	}

	public Short getDcgSeqp() {
		return dcgSeqp;
	}

	public void setDcgSeqp(Short dcgSeqp) {
		this.dcgSeqp = dcgSeqp;
	}

	public Boolean getTogglePesquisaOpened() {
		return togglePesquisaOpened;
	}

	public void setTogglePesquisaOpened(Boolean togglePesquisaOpened) {
		this.togglePesquisaOpened = togglePesquisaOpened;
	}

	public boolean isDispararPesquisa() {
		return dispararPesquisa;
	}

	public void setDispararPesquisa(boolean dispararPesquisa) {
		this.dispararPesquisa = dispararPesquisa;
	}

	public Integer getDdtSeq() {
		return ddtSeq;
	}

	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	public Integer getDdtCrgSeq() {
		return ddtCrgSeq;
	}

	public void setDdtCrgSeq(Integer ddtCrgSeq) {
		this.ddtCrgSeq = ddtCrgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public MbcQuestao getQuestao() {
		return questao;
	}

	public void setQuestao(MbcQuestao questao) {
		this.questao = questao;
	}

	public Boolean getExibeModalCancelarCrgComDescricao() {
		return exibeModalCancelarCrgComDescricao;
	}

	public void setExibeModalCancelarCrgComDescricao(
			Boolean exibeModalCancelarCrgComDescricao) {
		this.exibeModalCancelarCrgComDescricao = exibeModalCancelarCrgComDescricao;
	}

	public Map<String, String> getLegendaTitles() {
		return legendaTitles;
	}

	public void setLegendaTitles(Map<String, String> legendaTitles) {
		this.legendaTitles = legendaTitles;
	}

	public Map<String, String> getLegendaIcos() {
		return legendaIcos;
	}

	public void setLegendaIcos(Map<String, String> legendaIcos) {
		this.legendaIcos = legendaIcos;
	}

	public String getNomeMicrocomputador() {
		return nomeMicrocomputador;
	}

	public void setNomeMicrocomputador(String nomeMicrocomputador) {
		this.nomeMicrocomputador = nomeMicrocomputador;
	}

	public Boolean getIsImpPreAne() {
		return isImpPreAne;
	}

	public void setIsImpPreAne(Boolean isImpPreAne) {
		this.isImpPreAne = isImpPreAne;
	}

	public List<CirurgiaComPacEmTransOperatorioVO> getCirurgiasComPacientesEmTransOperatorio() {
		return cirurgiasComPacientesEmTransOperatorio;
	}

	public void setCirurgiasComPacientesEmTransOperatorio(
			List<CirurgiaComPacEmTransOperatorioVO> cirurgiasComPacientesEmTransOperatorio) {
		this.cirurgiasComPacientesEmTransOperatorio = cirurgiasComPacientesEmTransOperatorio;
	}

	public Long getTempoRecarregarCirurgiasEmTransOperatorio() {
		return tempoRecarregarCirurgiasEmTransOperatorio;
	}

	public void setTempoRecarregarCirurgiasEmTransOperatorio(
			Long tempoRecarregarCirurgiasEmTransOperatorio) {
		this.tempoRecarregarCirurgiasEmTransOperatorio = tempoRecarregarCirurgiasEmTransOperatorio;
	}

	public Boolean getRefreshComCamposPesquisaFechados() {
		return refreshComCamposPesquisaFechados;
	}

	public void setRefreshComCamposPesquisaFechados(
			Boolean refreshComCamposPesquisaFechados) {
		this.refreshComCamposPesquisaFechados = refreshComCamposPesquisaFechados;
	}

	public Integer getSeqSolicitacaoInternacao() {
		return seqSolicitacaoInternacao;
	}

	public void setSeqSolicitacaoInternacao(Integer seqSolicitacaoInternacao) {
		this.seqSolicitacaoInternacao = seqSolicitacaoInternacao;
	}

	public Boolean getPlayAlertaNovoPacEmTransOperatorio() {
		return playAlertaNovoPacEmTransOperatorio;
	}

	public void setPlayAlertaNovoPacEmTransOperatorio(
			Boolean playAlertaNovoPacEmTransOperatorio) {
		this.playAlertaNovoPacEmTransOperatorio = playAlertaNovoPacEmTransOperatorio;
	}

	public String getUrlFichaAnestesica() {
		return urlFichaAnestesica;
	}

	public void setUrlFichaAnestesica(String urlFichaAnestesica) {
		this.urlFichaAnestesica = urlFichaAnestesica;
	}

	public Boolean getExisteIntegracaoAghWebAGHU() {
		return existeIntegracaoAghWebAGHU;
	}

	public void setExisteIntegracaoAghWebAGHU(Boolean existeIntegracaoAghWebAGHU) {
		this.existeIntegracaoAghWebAGHU = existeIntegracaoAghWebAGHU;
	}

	public boolean isBotaoPrescreverHabilitado() {
		return botaoPrescreverHabilitado;
	}

	public void setBotaoPrescreverHabilitado(boolean botaoPrescreverHabilitado) {
		this.botaoPrescreverHabilitado = botaoPrescreverHabilitado;
	}

	public String getFiltroPaciente() {
		return filtroPaciente;
	}

	public void setFiltroPaciente(String filtroPaciente) {
		this.filtroPaciente = filtroPaciente;
	}

	public List<CirurgiaVO> getFilteredCirurgias() {
		return filteredCirurgias;
	}

	public void setFilteredCirurgias(List<CirurgiaVO> filteredCirurgias) {
		this.filteredCirurgias = filteredCirurgias;
	}
	
	public void carregarCirurgiaSelecionadaPorCrg(Integer seqCrgSelecionadaGrid) {
		
		this.seqCrgSelecionada = seqCrgSelecionadaGrid;
		carregarCirurgiaSelecionada();
	}

	public boolean isLiberarCamposDoFiltro() {
		return liberarCamposDoFiltro;
	}

	public void setLiberarCamposDoFiltro(boolean liberarCamposDoFiltro) {
		this.liberarCamposDoFiltro = liberarCamposDoFiltro;
	}
	
	public ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return solicitacaoExameFacade;
	}

	public void setSolicitacaoExameFacade(
			ISolicitacaoExameFacade solicitacaoExameFacade) {
		this.solicitacaoExameFacade = solicitacaoExameFacade;
	}

	
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	
	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public AvaliacaoPreSedacaoVO getAvaliacaoPreSedacaoVO() {
		return avaliacaoPreSedacaoVO;
	}

	public void setAvaliacaoPreSedacaoVO(AvaliacaoPreSedacaoVO avaliacaoPreSedacaoVO) {
		this.avaliacaoPreSedacaoVO = avaliacaoPreSedacaoVO;
	}

	public String getUrlPendenciaDigital() {
		return urlPendenciaDigital;
	}

	public void setUrlPendenciaDigital(String urlPendenciaDigital) {
		this.urlPendenciaDigital = urlPendenciaDigital;
	}

	public boolean isDirecionaAghDocs() {
		return direcionaAghDocs;
	}

	public void setDirecionaAghDocs(boolean direcionaAghDocs) {
		this.direcionaAghDocs = direcionaAghDocs;
	}

	public String getPaginaOrigem() {
		return paginaOrigem;
	}

	public void setPaginaOrigem(String paginaOrigem) {
		this.paginaOrigem = paginaOrigem;
	}

	public String getMensagemTempoCirurgiaMinimo() {
		return mensagemTempoCirurgiaMinimo;
	}

	public void setMensagemTempoCirurgiaMinimo(String mensagemTempoCirurgiaMinimo) {
		this.mensagemTempoCirurgiaMinimo = mensagemTempoCirurgiaMinimo;
	}
	
	
}
