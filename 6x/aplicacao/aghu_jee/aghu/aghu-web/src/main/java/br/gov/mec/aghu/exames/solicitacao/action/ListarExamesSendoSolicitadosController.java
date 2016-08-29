package br.gov.mec.aghu.exames.solicitacao.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioDeteccaoLesao;
import br.gov.mec.aghu.dominio.DominioFormaRespiracao;
import br.gov.mec.aghu.dominio.DominioOutrosFarmacos;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSismamaHistoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSismamaSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDadoQuestionario;
import br.gov.mec.aghu.dominio.DominioTipoPesquisaExame;
import br.gov.mec.aghu.dominio.DominioTipoTransporte;
import br.gov.mec.aghu.dominio.DominioTipoTransporteQuestionario;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.exames.sismama.business.ISismamaFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.business.TipoCampoDataHoraISE;
import br.gov.mec.aghu.exames.solicitacao.sismama.action.QuestionarioSismamaController;
import br.gov.mec.aghu.exames.solicitacao.sismama.action.QuestionarioSismamaInfComplementaresController;
import br.gov.mec.aghu.exames.solicitacao.vo.AbasIndicadorApresentacaoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DataProgramadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameSuggestionVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTmpIntervaloColeta;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AghuTooManyMethods","PMD.ExcessiveClassLength"})
public class ListarExamesSendoSolicitadosController extends ActionController {
	private static final Log LOG = LogFactory.getLog(ListarExamesSendoSolicitadosController.class);
	private static final long serialVersionUID = 8413809346969917210L;
	private static final int RANGEINI = -99;
	private static final int RANGEFIM = 99;

	@EJB
	private IExamesFacade examesFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade ;
	@EJB
	private ISismamaFacade sismamaFacade;	
	@Inject
	private QuestionarioSismamaInfComplementaresController questionarioSismamaInfComplementaresController ;
	@Inject
	private QuestionarioSismamaController questionarioSismamaController ;
	private SolicitacaoExameVO solicitacaoExameVo = null;
	private ItemSolicitacaoExameVO itemSolicitacaoExameVo = new ItemSolicitacaoExameVO();
	private ItemSolicitacaoExameVO itemSolicitacaoExameCopiaEmEdicao = null;
	private List<AelGrupoQuestao> grupoQuestaosCopy;
	private AghUnidadesFuncionais unidadeTrabalho;
	@Inject
	private SolicitacaoExameController solicitacaoExameController;
	private Map<String, Object> respostasSismamaBiopsia;
	private Boolean respondeCancerMama;	
	private boolean habilitouSuggestionCidsQuestionario = false;

    private boolean bloqueiaCalendar = false;
    
    @Inject
    private ExamesSuggestionMB examesSuggestionController;

	/**
	 * Guarda informacoes quando dados de Tipo Transporte forem informados.
	 */
	private ItemSolicitacaoExameVO itemSolicitacaoExameVoSugestao = null;

	private List<AelSitItemSolicitacoes> situacaoItemSolicitacoes;
				 
	private Map<String, Object> questionarioSismama = new HashMap<String, Object>();

    private List<DataProgramadaVO> listaDataHoraProgramada;

    /**
     * Guarda dados do último item de exame para ser reaproveitado para o item seguinte
     * Melhoria em produção #48020
     */
    private ItemExameReferencia itemExameReferencia;
    
    private ExameSuggestionVO exameSuggestionVO;
    
    private DominioTipoPesquisaExame tipoPesquisa = DominioTipoPesquisaExame.INICIO;
    
    private Integer indexAbaAtiva = 0;

    protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
		this.itemExameReferencia = null;
		examesSuggestionController.obterExamesSuggetion("", solicitacaoExameController.getAtendimentoSeq(), solicitacaoExameController.isUsuarioSolicExameProtocoloEnfermagem(), solicitacaoExameController.isOrigemInternacao(), tipoPesquisa);
	}

	public String getAbaId() {
		return "ABA1_";
	}
	
	protected void initController(SolicitacaoExameVO solicEx) {
		initControllerUnfExecutaExame(solicEx);
	}
	
	protected void initControllerUnfExecutaExame(SolicitacaoExameVO solicEx) {
		if (getItemSolicitacaoExameVo().getUnfExecutaExame() == null) {
			this.setSolicitacaoExameVo(solicEx);
			//this.setSituacaoItemSolicitacoes(examesFacade.listarTodosAelSitItemSolicitacoes());
			this.setSituacaoItemSolicitacoes(examesFacade.listarTodosPorSituacaoEMostrarSolicExames(DominioSituacao.A, true));
			initController();
			this.getItemSolicitacaoExameVo().setSolicitacaoExameVO(getSolicitacaoExameVo());
		}
		unidadeTrabalho = solicitacaoExameController.obterUnidadeTrabalho();
	}

	protected void initController() {
		this.initRegiaoAnatomica();
		this.initDataProgramada();
		this.getItemSolicitacaoExameVo().setUrgente(Boolean.FALSE);
		this.initFlagAbasInternas();
	}

	private void initDataProgramada() {
		this.getItemSolicitacaoExameVo().setCalendar(Boolean.TRUE);
		getItemSolicitacaoExameVo().setDataProgramada(new Date());
	}

	private void initRegiaoAnatomica() {
		this.itemSolicitacaoExameVo.setCadastroRegiaoAnatomica(DominioOutrosFarmacos.CADASTRADO);
	}

	/**
	 * Marca todas as abas internas como NAO selecionadas.
	 */
	private void initFlagAbasInternas() {
		this.itemSolicitacaoExameVo.setMostrarAbaTipoTransporte(Boolean.FALSE);
		this.itemSolicitacaoExameVo.setMostrarAbaIntervColeta(Boolean.FALSE);
		this.itemSolicitacaoExameVo.setMostrarAbaNoAmostras(Boolean.FALSE);
		this.itemSolicitacaoExameVo.setMostrarAbaConcentO2(Boolean.FALSE);
		this.itemSolicitacaoExameVo.setMostrarAbaRegMatAnalise(Boolean.FALSE);
		this.itemSolicitacaoExameVo.setMostrarAbaRecomendacoes(Boolean.FALSE);
		this.itemSolicitacaoExameVo.setMostrarAbaExamesOpcionais(Boolean.FALSE);
		this.itemSolicitacaoExameVo.setMostrarAbaQuestionario(Boolean.FALSE);
		this.itemSolicitacaoExameVo.setMostrarAbaQuestionarioSismama(Boolean.FALSE);
		this.itemSolicitacaoExameVo.setMostrarAbaQuestionarioSismamaBiopsia(Boolean.FALSE);
	}

	/**
	 * Verifica se alguma aba serah apresentada.
	 */
	public Boolean existeAbaAtiva() {
		return (existeAbaDeEdicaoAtiva()
		|| this.itemSolicitacaoExameVo.getMostrarAbaRecomendacoes()
		|| this.itemSolicitacaoExameVo.getUnfExecutaExame()!=null);
	}
	
	public Boolean nenhumaAbaAtiva() {
		return (!existeAbaDeEdicaoAtiva()
		&& !itemSolicitacaoExameVo.getMostrarAbaRecomendacoes()
		&& itemSolicitacaoExameVo.getUnfExecutaExame() != null);
	}

	/**
	 * Verifica se existe alguma aba a ser apresentada de preenchimento de dados.
	 */
	public Boolean existeAbaDeEdicaoAtiva() {
		return (this.itemSolicitacaoExameVo.getMostrarAbaTipoTransporte()
				|| this.itemSolicitacaoExameVo.getMostrarAbaIntervColeta()
				|| this.itemSolicitacaoExameVo.getMostrarAbaNoAmostras()
				|| this.itemSolicitacaoExameVo.getMostrarAbaConcentO2()
				|| this.itemSolicitacaoExameVo.getMostrarAbaRegMatAnalise()
				|| this.itemSolicitacaoExameVo.getMostrarAbaExamesOpcionais()
				|| this.itemSolicitacaoExameVo.getMostrarAbaQuestionario()
				|| this.itemSolicitacaoExameVo.getMostrarAbaQuestionarioSismama()
				|| this.itemSolicitacaoExameVo.getMostrarAbaQuestionarioSismamaBiopsia());
	}

	/**
	 * Verifica se existe alguma aba não suportada pelo adição automática de item de exame.
	 */
	public Boolean existeAbaNaoSuportadaAdicaoAutomatica() {
		return (this.itemSolicitacaoExameVo.getMostrarAbaIntervColeta()
				|| this.itemSolicitacaoExameVo.getMostrarAbaNoAmostras()
				|| this.itemSolicitacaoExameVo.getMostrarAbaRegMatAnalise()
				|| this.itemSolicitacaoExameVo.getMostrarAbaExamesOpcionais()
				|| this.itemSolicitacaoExameVo.getMostrarAbaQuestionarioSismama()
				|| this.itemSolicitacaoExameVo.getMostrarAbaQuestionarioSismamaBiopsia());
	}

	public void limparCamposDataHoraESituacaoItem(){
	    this.itemSolicitacaoExameVo.setTipoTransporte(null);
	    this.itemSolicitacaoExameVo.setOxigenioTransporte(null);
	}

	public void alterarItemSolicitacaoExame() {
		try {
			if (!validarCamposObrigatorios()) {
				return;
			}
			ItemSolicitacaoExameVO itemSolicitacaoExameVO = this.getItemSolicitacaoExameVo();
			//Executa validações antes de adicionar o exame.
			solicitacaoExameFacade.validarItemSolicitacaoExameErros(itemSolicitacaoExameVO, this.getQuestionarioSismama());
			//Deleta os exames opcionais e recria
			if (!itemSolicitacaoExameVO.getDependentesOpcionais().isEmpty()) {
				this.getSolicitacaoExameVo().getItemSolicitacaoExameVos().removeAll(itemSolicitacaoExameVO.getDependentesOpcionais());
			}
			this.getSolicitacaoExameVo().getItemSolicitacaoExameVos().addAll(solicitacaoExameFacade.obterDependentesOpcionaisSelecionados(this.getItemSolicitacaoExameVo()));
			if (this.itemSolicitacaoExameCopiaEmEdicao != null) {
				this.itemSolicitacaoExameCopiaEmEdicao.doCopiaPropriedades(this.getItemSolicitacaoExameVo());
				this.itemSolicitacaoExameCopiaEmEdicao.setEmEdicao(Boolean.FALSE);
				this.itemSolicitacaoExameCopiaEmEdicao = null;
			}
			itemSolicitacaoExameVO.setQuestionarioSismama(this.questionarioSismama);
			this.initItemSolicitacaoExameVo();
			// Verifica se tem alguma informação para apresentar na tela.
			BaseListException info = solicitacaoExameFacade.validarItemSolicitacaoExameMensagens(itemSolicitacaoExameVO);
			if (info.hasException()) {
				throw info;
			}
		} catch (BaseListException e) {
			super.apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		} catch(Exception e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}
	}

	public void cancelarEdicaoItemSolicitacaoExame() {
		if (this.itemSolicitacaoExameCopiaEmEdicao != null) {
			this.itemSolicitacaoExameCopiaEmEdicao.setEmEdicao(Boolean.FALSE);
			this.itemSolicitacaoExameCopiaEmEdicao.setGruposQuestao(this.grupoQuestaosCopy);
			this.itemSolicitacaoExameCopiaEmEdicao = null;
			this.grupoQuestaosCopy = null;
		}
		this.initItemSolicitacaoExameVo();
	}

	public void doSetItemSolicitacaoExameVoEdicao(ItemSolicitacaoExameVO vo) {
		questionarioSismamaInfComplementaresController.limpar();
		questionarioSismamaController.limpar();
		if (this.itemSolicitacaoExameCopiaEmEdicao != null) {
			this.itemSolicitacaoExameCopiaEmEdicao.setEmEdicao(Boolean.FALSE);
			this.itemSolicitacaoExameCopiaEmEdicao = null;
		}
		ItemSolicitacaoExameVO copia = new ItemSolicitacaoExameVO(vo);
		copia.setEmEdicao(Boolean.TRUE);
		vo.setEmEdicao(Boolean.TRUE);
		this.itemSolicitacaoExameCopiaEmEdicao = vo;
		if (vo.getGruposQuestao() != null && !vo.getGruposQuestao().isEmpty()) {
			this.grupoQuestaosCopy = new ArrayList<AelGrupoQuestao>();
			for (final AelGrupoQuestao aelGrupoQuestao : vo.getGruposQuestao()) {
				try {
					final AelGrupoQuestao clone = (AelGrupoQuestao) BeanUtils.cloneBean(aelGrupoQuestao);
					clone.setAelRespostaQuestaos(new ArrayList<AelRespostaQuestao>());
					for(final AelRespostaQuestao respostaQuestao : aelGrupoQuestao.getAelRespostaQuestaos()){
						clone.getAelRespostaQuestaos().add((AelRespostaQuestao)BeanUtils.cloneBean(respostaQuestao));
					}
					this.grupoQuestaosCopy.add(clone);
				} catch (Exception e) {
					LOG.error("Erro ao clonar.", e);
				}
			}
		}

		carregarListaDataHoraProgramada(copia);

		this.setItemSolicitacaoExameVo(copia);
		this.setQuestionarioSismama(copia.getQuestionarioSismama());
		this.questionarioSismamaController.desabilitarCamposCirurgia();
		this.questionarioSismamaController.desabilitarCamposComCaroco();
		this.questionarioSismamaController.desabilitarCampoSemCaroco();
		this.questionarioSismamaController.desabilitarCampoQuando();
		this.questionarioSismamaController.desabilitarCamposRadioterapia();
		this.questionarioSismamaController.desabilitarCampoAnoRadioterapiaDireita();
		this.questionarioSismamaController.desabilitarCampoAnoRadioterapiaEsquerda();
		this.questionarioSismamaInfComplementaresController.habilitarCamposMamoDiag();
		this.questionarioSismamaInfComplementaresController.habilitarCamposAvaliacaoRegiao();
	}

	public String acaoAdicionarItemSolicitacaoExame() throws BaseException {
		adicionarItemSolicitacaoExame();
		return null;
	}

    public void validaDataProgramadaUsuario() {
    	if (DateUtil.validaDataMenor(itemSolicitacaoExameVo.getDataProgramada(), new Date())) {
    		itemSolicitacaoExameVo.setDataProgramada(new Date());
    		apresentarMsgNegocio(Severity.ERROR, "DATA_PROGRAMADA_PASSADO");
            
        }    	
    }
    
	/**
	 * Metodo para adicionar item de solicitacao exame na lista.
	 * @throws BaseException 
	 */
	public boolean adicionarItemSolicitacaoExame() throws BaseException {
		boolean adicionou = false;
		try {
			if (!validarCamposObrigatorios()) {
				return  adicionou;
			} 
			if (this.solicitacaoExameVo != null && getSolicitacaoExameVo().getIsSus() != null){
				validaUnidadeSus();
			}
			ItemSolicitacaoExameVO itemSolicitacaoExameVO = this.getItemSolicitacaoExameVo();
			this.itemSolicitacaoExameVo.setQuestionarioSismamaBiopsia(respostasSismamaBiopsia);
			//Seta a unidade de trabalho
			itemSolicitacaoExameVO.getSolicitacaoExameVO().setUnidadeTrabalho(unidadeTrabalho);

			//Executa validações antes de adicionar o exame.
			solicitacaoExameFacade.validarItemSolicitacaoExameErros(itemSolicitacaoExameVO, this.questionarioSismama);
			//this.getItemSolicitacaoExameVo().setSequencial(this.getSolicitacaoExameVo().proximoSequencial())
			this.getItemSolicitacaoExameVo().setSequencial(this.solicitacaoExameController.retornarProximoSeqItemExame());
			itemSolicitacaoExameVO.setStyleClass("silk-icon silk-exames-pol");

            if (bloqueiaCalendar) {
                itemSolicitacaoExameVO.setDataProgramada(new Date());
            }
			this.getSolicitacaoExameVo().getItemSolicitacaoExameVos().add(itemSolicitacaoExameVO);
			this.getSolicitacaoExameVo().getItemSolicitacaoExameVos().addAll(itemSolicitacaoExameVO.getDependentesObrigratorios());
			this.getSolicitacaoExameVo().getItemSolicitacaoExameVos().addAll(solicitacaoExameFacade.obterDependentesOpcionaisSelecionados(this.getItemSolicitacaoExameVo()));
			// Tarefas pos adicao: Marcar ultimo Tipo Transporta adicionado para sugestão na proxima adicao.
			this.marcarUltimoTipoTransporteAdicionado();
			this.marcarUltimoHorarioRotina();
			if (this.getItemSolicitacaoExameVo().getMostrarAbaQuestionario()) {
				this.solicitacaoExameController.adicionarRespostas(this.getItemSolicitacaoExameVo().getGruposQuestao(), this.getItemSolicitacaoExameVo().getSequencial());
			}
			itemSolicitacaoExameVO.setQuestionarioSismama(this.questionarioSismama);
			this.initItemSolicitacaoExameVo();
			adicionou = true;
			// Verifica se tem alguma informação para apresentar na tela. Neste ponto o item ja foi adicionado.
			BaseListException info = solicitacaoExameFacade.validarItemSolicitacaoExameMensagens(itemSolicitacaoExameVO);
			if (info.hasException()) {
				throw info;
			}
		} catch (BaseListException e) {
			super.apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		} catch(Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_AO_ADICIONAR_EXAME", e.getMessage());
			LOG.error(e.getMessage(), e);
		}
		return adicionou;
	}

	private void validaUnidadeSus() throws ApplicationBusinessException {
	    if (getSolicitacaoExameVo().getIsSus()) {
		if(!solicitacaoExameFacade.validaUnidadeSolicitanteSus(getSolicitacaoExameVo().getUnfSeq())){
		    throw new ApplicationBusinessException("MSG_UNIDADE_N_PODE_SOLIC", Severity.ERROR);
		}
	    }
	    
	}

	public String acaoAdicionarMaterialAnaliseCopia() throws BaseException {
		adicionarMaterialAnaliseCopia();
		return null;
	}

	public String posAdicionarExameAutomaticamente() throws BaseException {
		adicionarExameAutomaticamente();
		this.limparCamposDataHoraESituacaoItem();
		return null;
	}

	/**
	 * Método para adicionar automaticamente um item de solicitação de exame.
	 * baseado nos dados do primeiro item adicionado
	 *
	 * @throws BaseException
	 */
	private void adicionarExameAutomaticamente() throws BaseException {

		if (isPrimeiroItemExame()) {
			return;
		}

		ItemSolicitacaoExameVO itemReferencia = obterUltimoItemExameNaoDependente();

		if (itemReferencia == null) {
			return;
		}

		if (itemExameReferencia == null) {
			itemExameReferencia = new ItemExameReferencia();
		}

		itemExameReferencia.carregarDados(itemReferencia);

		adicionarItemExameComDadosDeReferencia();
	}

	private boolean isPrimeiroItemExame() {

		if (this.getSolicitacaoExameVo() == null ||
				this.getSolicitacaoExameVo().getItemSolicitacaoExameVos() == null ||
				this.getSolicitacaoExameVo().getItemSolicitacaoExameVos().size() == 0) {

			return true;
		}

		return false;
	}

	private void adicionarItemExameComDadosDeReferencia() throws BaseException {

		boolean encontrouTodosOsDados = true;
		ItemSolicitacaoExameVO itemParaAdicionar = this.itemSolicitacaoExameVo;
		Boolean permiteUrgente = solicitacaoExameFacade.verificarExamePodeSolicitarUrgente(itemParaAdicionar.getUnfExecutaExame().getExameSigla(),
					itemParaAdicionar.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq(),
					itemParaAdicionar.getUnfExecutaExame().getUnfExecutaExame().getUnidadeFuncional().getSeq(),
					getSolicitacaoExameVo().getUnidadeFuncional().getSeq());
		itemParaAdicionar.setPermiteUrgente(permiteUrgente);
		if (permiteUrgente) {
			itemParaAdicionar.setUrgente(itemExameReferencia.getUrgente());
		} else {
			itemParaAdicionar.setUrgente(false);
		}
		//itemParaAdicionar.setCalendar(itemExameReferencia.isCalendar());
		if (itemParaAdicionar.getUrgente() != Boolean.TRUE) {
			if (itemParaAdicionar.getCalendar()) {
				if (itemExameReferencia.getDataProgramada() != null) {
					itemParaAdicionar.setDataProgramada(itemExameReferencia.getDataProgramada());
				} else {
					encontrouTodosOsDados = false;
				}
			} else {
				if (itemExameReferencia.getDataProgramadaCombo() != null) {
					itemParaAdicionar.setDataProgramada(itemExameReferencia.getDataProgramadaCombo());
				} else {
					encontrouTodosOsDados = false;
				}
			}
		}

		adicionarDadosSalvosDeAbasAtivas(itemParaAdicionar);

		if (!possuiItensAdicionaisParaSeremInformados() && encontrouTodosOsDados) {
			adicionarItemSolicitacaoExame();
		} else {
			itemParaAdicionar.setDataProgramada(new Date());
		}
			
	}

	private void adicionarDadosSalvosDeAbasAtivas(ItemSolicitacaoExameVO itemParaAdicionar) {

		if (itemParaAdicionar.getMostrarAbaTipoTransporte() != null && itemParaAdicionar.getMostrarAbaTipoTransporte()) {
			itemParaAdicionar.setTipoTransporte(itemExameReferencia.getTipoTransporte());
			itemParaAdicionar.setOxigenioTransporte(itemExameReferencia.getOxigenioTransporte());
		}

		if (itemParaAdicionar.getMostrarAbaConcentO2() != null && itemParaAdicionar.getMostrarAbaConcentO2()) {
			itemParaAdicionar.setFormaRespiracao(itemExameReferencia.getFormaRespiracao());
			itemParaAdicionar.setLitrosOxigenio(itemExameReferencia.getLitrosOxigenio());
			itemParaAdicionar.setPercOxigenio(itemExameReferencia.getPercOxigenio());
		}

		preencherComDadosSalvosDeQuestionariosAnteriores(itemParaAdicionar, this.getSolicitacaoExameVo().getItemSolicitacaoExameVos());
	}

	private void preencherComDadosSalvosDeQuestionariosAnteriores(ItemSolicitacaoExameVO itemParaAdicionar, List<ItemSolicitacaoExameVO> listaItensExameSolicitacao) {

		if (itemParaAdicionar.getMostrarAbaQuestionario() != null &&
			itemParaAdicionar.getMostrarAbaQuestionario() &&
			itemParaAdicionar.getQuestionarios() != null) {

			for(AelQuestionarios q : itemParaAdicionar.getQuestionarios()) {

				if (q == null || q.getSeq() == null) {
					continue;
				}

				List<AelRespostaQuestao> respostas = obterRespostasQuestionarioPreenchidoSolicitacao(listaItensExameSolicitacao, q.getSeq());

				if (respostas != null) {
					copiarRespostasQuestionarioItemExameAnterior(itemParaAdicionar, respostas);
				}
			}
		}
	}

	private List<AelRespostaQuestao> obterRespostasQuestionarioPreenchidoSolicitacao(List<ItemSolicitacaoExameVO> listaItensExameSolicitacao, Integer codigoQuestionario) {

		if (listaItensExameSolicitacao == null || listaItensExameSolicitacao.isEmpty()) {
			return null;
		}

		Short sequencialItem = obterSequencialUltimoItemExameQuestionarioPreenchido(listaItensExameSolicitacao, codigoQuestionario);

		if (sequencialItem == null) {
			return null;
		}

		return obterRespostasItemExamePorSequencial(listaItensExameSolicitacao, sequencialItem);
	}

	private Short obterSequencialUltimoItemExameQuestionarioPreenchido(List<ItemSolicitacaoExameVO> listaItensExameSolicitacao, Integer codigoQuestionario) {

		int itensSize = listaItensExameSolicitacao.size();

		// Procura a partir das últimas respostas para encontrar o último item
		for (int i = itensSize - 1; i >= 0; i--) { // procura a partir do último item

			ItemSolicitacaoExameVO item = listaItensExameSolicitacao.get(i);

			if (item == null || item.getRespostasQuestoes() == null || item.getRespostasQuestoes().isEmpty()) {
				continue;
			}

			for (AelRespostaQuestao resposta : item.getRespostasQuestoes()) {

				if (resposta == null ||
						resposta.getQuestionario() == null ||
						resposta.getQuestionario().getSeq() == null ||
						resposta.getSequencial() == null ||
						!resposta.getQuestionario().getSeq().equals(codigoQuestionario)) {

					continue;
				}

				// Encontrou o último item de exame com mesmo questionário preenchido.
				return resposta.getSequencial();
			}
		}

		return null;
	}

	private List<AelRespostaQuestao> obterRespostasItemExamePorSequencial(List<ItemSolicitacaoExameVO> listaItensExameSolicitacao, Short sequencialItem) {

		if (listaItensExameSolicitacao == null || listaItensExameSolicitacao.isEmpty() || sequencialItem == null) {
			return null;
		}

		List<AelRespostaQuestao> respostasQuestionarioBusca = new ArrayList<>();

		int itensSize = listaItensExameSolicitacao.size();

		// Procura a partir das últimas respostas para encontrar o último item
		for (int i = itensSize - 1; i >= 0; i--) { // procura a partir do último item

			ItemSolicitacaoExameVO item = listaItensExameSolicitacao.get(i);

			if (item == null || item.getRespostasQuestoes() == null || item.getRespostasQuestoes().isEmpty()) {
				continue;
			}

			for (AelRespostaQuestao resposta : item.getRespostasQuestoes()) {

				if (resposta.getQuestionario() == null ||
						resposta.getQuestionario().getSeq() == null ||
						resposta.getSequencial() == null ||
						!resposta.getSequencial().equals(sequencialItem)) {

					continue;
				}

				respostasQuestionarioBusca.add(resposta);
			}

			if (respostasQuestionarioBusca.size() > 0) {
				// Já encontrou respostas do item procurado. Logo, pode já retornar.
				return respostasQuestionarioBusca;
			}
		}

		return respostasQuestionarioBusca;
	}

	private void copiarRespostasQuestionarioItemExameAnterior(ItemSolicitacaoExameVO itemParaAdicionar, List<AelRespostaQuestao> respostas) {

		if (itemParaAdicionar == null ||
				itemParaAdicionar.getGruposQuestao() == null ||
				itemParaAdicionar.getGruposQuestao().isEmpty() ||
				respostas == null ||
				respostas.isEmpty() ||
				itemParaAdicionar.getRespostasQuestoes() == null ||
				itemParaAdicionar.getRespostasQuestoes().isEmpty()) {
			return;
		}

		for (AelGrupoQuestao grupo : itemParaAdicionar.getGruposQuestao()) {
			for (AelRespostaQuestao aelRespostaQuestao : grupo.getAelRespostaQuestaos()) {

				AelQuestoesQuestionario questaoQuestionario = aelRespostaQuestao.getQuestaoQuestionario();

				for (final AelRespostaQuestao aelRespostaQuestaoRespondidas : respostas) {

					if (!questaoQuestionario.getAelQuestionarios().getSeq().equals(aelRespostaQuestaoRespondidas.getQuestionario().getSeq()) ||
							!questaoQuestionario.getQuestao().getSeq().equals(aelRespostaQuestaoRespondidas.getQuestaoQuestionario().getQuestao().getSeq())) {
						continue;
					}

					if (aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getAelValorValidoQuestaos() == null
							|| aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getAelValorValidoQuestaos().isEmpty()) {
						if (DominioTipoDadoQuestionario.C.equals(aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getTipoDados())) {
							aelRespostaQuestao.setAghCid(aelRespostaQuestaoRespondidas.getAghCid());
						} else {
							aelRespostaQuestao.setResposta(aelRespostaQuestaoRespondidas.getResposta());
						}
					} else {
						aelRespostaQuestao.setAelValorValidoQuestao(aelRespostaQuestaoRespondidas.getAelValorValidoQuestao());
					}
				}
			}
		}
	}

	private boolean possuiItensAdicionaisParaSeremInformados() {

		return this.existeAbaNaoSuportadaAdicaoAutomatica();
	}

	private ItemSolicitacaoExameVO obterUltimoItemExameNaoDependente() {

		int itensSize = this.getSolicitacaoExameVo().getItemSolicitacaoExameVos().size();

		for (int i = itensSize - 1; i >= 0; i--) {
			ItemSolicitacaoExameVO item = this.getSolicitacaoExameVo().getItemSolicitacaoExameVos().get(i);

			if (item == null) {
				return null;
			}

			if (!item.getEhDependenteObrigatorio() && !item.getEhDependenteOpcional()) {
				return item;
			}
		}

		return null;
	}

	/**
	 * Metodo para adicionar uma copia de material de analise sem apagar os dados da tela.
	 * @throws BaseException
	 */
	public boolean adicionarMaterialAnaliseCopia() throws BaseException {
		final ItemSolicitacaoExameVO itemSolicitacaoExameVOCo = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVOCo.doCopiaPropriedades(this.itemSolicitacaoExameVo);
		boolean adicionou = this.adicionarItemSolicitacaoExame();
		if (adicionou) {
			this.itemSolicitacaoExameVo.doCopiaPropriedades(itemSolicitacaoExameVOCo);
			this.verificarStatusCadastroRegiaoAnatomica();
		}
		return adicionou;
	}

	/**
	 * Valida os campos obrigatórios da aba de exames sendo solicitados.
	 * @return boolean
	 */
	private boolean validarCamposObrigatorios() throws ApplicationBusinessException {
		boolean validou = true;
		boolean validouPercOxigenios = true;

		int intervaloInicioPercOxigenios = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_RANGE_INICIO_PERC_OXIGENIO);
		int intervaloFimPercOxigenios = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_RANGE_FIM_PERC_OXIGENIO);

		List<ISECamposObrigatoriosValidator> validators = new ArrayList<ISECamposObrigatoriosValidator>();
		ISECamposObrigatoriosValidator solicitacaoExameValidator = new SolicitacaoExameValidator(getItemSolicitacaoExameVo(), super.getBundle());
		ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = getValidatorISE();
		validators.add(solicitacaoExameValidator);
		validators.add(itemSolicitacaoExameValidator);
		validators.addAll(getAbasValidators());
		for (ISECamposObrigatoriosValidator validator : validators) {
			validou = validou && validator.validate();
			if (validator instanceof AbaConcentO2Validator) {
				validouPercOxigenios = ((AbaConcentO2Validator) validator).validatePercOxigenios(intervaloInicioPercOxigenios, intervaloFimPercOxigenios);
				validou = validouPercOxigenios ? validou : false;
			}
		}
		return validou;
	}

	protected ISECamposObrigatoriosValidator getValidatorISE() {
		return new ItemSolicitacaoExameValidator(getItemSolicitacaoExameVo(), getBundle());
	}

	/**
	 * Escolhe as abas que irá validar.
	 * @return
	 */
	protected List<ISECamposObrigatoriosValidator> getAbasValidators() {
		List<ISECamposObrigatoriosValidator> validators = new ArrayList<ISECamposObrigatoriosValidator>();

		if (getItemSolicitacaoExameVo().getMostrarAbaConcentO2()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaConcentO2Validator(getItemSolicitacaoExameVo(), getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaIntervColeta()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaIntervColetaValidator(getItemSolicitacaoExameVo(), getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaNoAmostras()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaNoAmostrasValidator(getItemSolicitacaoExameVo(), getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaRegMatAnalise()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaRegMatAnaliseValidator(getItemSolicitacaoExameVo(), getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaTipoTransporte()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaTipoTransporteValidator(getItemSolicitacaoExameVo(), getBundle());
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaQuestionario()) {
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaQuestionarioValidator(getItemSolicitacaoExameVo(),
					getBundle(), 0);
			validators.add(itemSolicitacaoExameValidator);
		}
		if (getItemSolicitacaoExameVo().getMostrarAbaQuestionarioSismama()) {
			ItemSolicitacaoExameVO itemVO = new ItemSolicitacaoExameVO();
			itemVO.setQuestionarioSismama(getQuestionarioSismama());
			ISECamposObrigatoriosValidator itemSolicitacaoExameValidator = new AbaQuestionarioSismamaValidator(itemVO,
					getBundle(), "0");
			validators.add(itemSolicitacaoExameValidator);
		}
		return validators;
	}

	public void initItemSolicitacaoExameVo() {
		// Inicializa Item para nova Adicao. 
                bloqueiaCalendar = false;
                listaDataHoraProgramada = null;
                exameSuggestionVO = null;
		this.setItemSolicitacaoExameVo(new ItemSolicitacaoExameVO());
		this.getItemSolicitacaoExameVo().setSolicitacaoExameVO(getSolicitacaoExameVo());
		this.setQuestionarioSismama(new HashMap<String, Object>());
		this.initController();		
	}

	private void marcarUltimoTipoTransporteAdicionado() {
		if (this.getItemSolicitacaoExameVo().getTipoTransporte() != null
				&& this.getItemSolicitacaoExameVo().getOxigenioTransporte() != null) {
			if (itemSolicitacaoExameVoSugestao == null) {
				this.itemSolicitacaoExameVoSugestao = new ItemSolicitacaoExameVO();
			}
			this.itemSolicitacaoExameVoSugestao.setTipoTransporte(this.getItemSolicitacaoExameVo().getTipoTransporte());
			this.itemSolicitacaoExameVoSugestao.setOxigenioTransporte(this.getItemSolicitacaoExameVo().getOxigenioTransporte());
		}
	}

	private void marcarUltimoHorarioRotina() {
		if (this.getItemSolicitacaoExameVo().getDataProgramada() != null && !this.getItemSolicitacaoExameVo().getCalendar()) {
			if (itemSolicitacaoExameVoSugestao == null) {
				this.itemSolicitacaoExameVoSugestao = new ItemSolicitacaoExameVO();
			}
			this.itemSolicitacaoExameVoSugestao.setDataProgramada(this.getItemSolicitacaoExameVo().getDataProgramada());
		}
	}

	private void sugerirNumeroAmostra() {
		if (getItemSolicitacaoExameVo().getNumeroAmostra() == null) {
			//		Verifica se tem cadastrado no exame sugestão para No amostras
			Short noAmostraDefault = getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getNroAmostraDefault();
			if (noAmostraDefault != null) {
				getItemSolicitacaoExameVo().setNumeroAmostra(noAmostraDefault.intValue());
			}
		}
	}

	private void sugerirUltimoTipoTransporte() {
		if (this.itemSolicitacaoExameVoSugestao != null && this.getItemSolicitacaoExameVo().getMostrarAbaTipoTransporte()) {
			if (getItemSolicitacaoExameVo().getTipoTransporte() == null) {
				this.getItemSolicitacaoExameVo().setTipoTransporte(this.itemSolicitacaoExameVoSugestao.getTipoTransporte());
			}
			if (getItemSolicitacaoExameVo().getOxigenioTransporte() == null) { 
				this.getItemSolicitacaoExameVo().setOxigenioTransporte(this.itemSolicitacaoExameVoSugestao.getOxigenioTransporte());
			}
		}
	}

	private void sugerirUltimoHorarioRotina() {
		if (this.itemSolicitacaoExameVoSugestao != null && !this.getItemSolicitacaoExameVo().getCalendar()) {
			this.getItemSolicitacaoExameVo().setDataProgramada(this.itemSolicitacaoExameVoSugestao.getDataProgramada());
		}
	}

	/**
	 * Método invocado quando o usuário marca/desmarca o checkbox "Urgente".
	 */
	public void checkUrgente() {
		if (this.getItemSolicitacaoExameVo().getUnfExecutaExame() != null) {
			try {
				getItemSolicitacaoExameVo().setDataProgramada(new Date());
				AelSitItemSolicitacoes situacao = this.solicitacaoExameFacade.obterSituacaoExameSugestao(this.getSolicitacaoExameVo().getUnidadeFuncional(),
						this.getSolicitacaoExameVo().getAtendimento(), this.getSolicitacaoExameVo().getAtendimentoDiverso(), this.getItemSolicitacaoExameVo()
						.getUnfExecutaExame().getUnfExecutaExame(), unidadeTrabalho, this.getItemSolicitacaoExameVo(), this.solicitacaoExameVo);
				this.getItemSolicitacaoExameVo().setSituacaoCodigo(situacao);
				reLoadDataHoraProgramada();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public void reLoadDataHoraProgramada() {
		if (this.getItemSolicitacaoExameVo().getUnfExecutaExame() != null) {
			try {
				this.desenhaTipoCampoData();
				if (getItemSolicitacaoExameVo().getCalendar()) {
					getItemSolicitacaoExameVo().setDataProgramada(new Date());
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	/**
	 * Carrega a lista de dt/hr de rotina para aquele exame, unidade executora e unidade solicitante.
	 */
	public List<DataProgramadaVO> doGetListaDatasHorasProgramadas(ItemSolicitacaoExameVO itemVO) {
		List<DataProgramadaVO> lista = null;
		// if (itemVO != null && itemVO.getCalendar() == Boolean.FALSE) 
		if (itemVO != null) {
			try {
				lista = solicitacaoExameFacade.getHorariosRotina(itemVO, solicitacaoExameVo.getUnidadeFuncional());
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}

		return lista;
	}

	public List<DataProgramadaVO> getListaDatasHorasProgramadas() {
		return doGetListaDatasHorasProgramadas(this.itemSolicitacaoExameVo);
	}

	/**
	 * Verifica regras de desenho do campo data/hora e seta no item vo.
	 * @throws BaseException 
	 */
	private void desenhaTipoCampoData() throws BaseException {
		if (getItemSolicitacaoExameVo() != null && solicitacaoExameVo != null) {
			Boolean calendar = Boolean.TRUE;
            TipoCampoDataHoraISE tipoCampoDataHoraValidado = solicitacaoExameFacade.
                    verificarCampoDataHora(getItemSolicitacaoExameVo(), solicitacaoExameVo.getUnidadeFuncional());
			calendar = (tipoCampoDataHoraValidado != TipoCampoDataHoraISE.COMBO);
            if (tipoCampoDataHoraValidado == TipoCampoDataHoraISE.CALENDAR_BLOQUEADO) {
                bloqueiaCalendar = true;
                getItemSolicitacaoExameVo().setDataProgramada(new Date());
            }

			getItemSolicitacaoExameVo().setCalendar(calendar);
		}

		if (!getItemSolicitacaoExameVo().getCalendar()) {
			this.sugerirUltimoHorarioRotina();
		}
	}

	/**
	 * Metodo utilizado pela suggestionbox para pesquisa de exames.
	 */
	public List<ExameSuggestionVO> obterExames(String nomeExame) {
		indexAbaAtiva=0;
		List<ExameSuggestionVO> result = null;
		if(this.getSolicitacaoExameVo() != null){
			if(this.getSolicitacaoExameVo().getUnidadeFuncional() != null){
				Integer seqAtendimento = null;
				if (this.getSolicitacaoExameVo() != null && this.getSolicitacaoExameVo().getAtendimento() != null && this.getSolicitacaoExameVo().getAtendimento().getSeq() != null){
					seqAtendimento = this.getSolicitacaoExameVo().getAtendimento().getSeq();
				}				
				result = examesSuggestionController.obterExamesSuggetion(nomeExame, seqAtendimento, solicitacaoExameController.isUsuarioSolicExameProtocoloEnfermagem(), solicitacaoExameController.isOrigemInternacao(), tipoPesquisa);
			}else{
				apresentarMsgNegocio(Severity.ERROR, "MSG_UNIDADE_FUNCIONAL_NAO_INFORMADA");
			}
		} 
		List<ExameSuggestionVO> listaExames = this.returnSGWithCount(result,(result.size()>=100? examesSuggestionController.getCountExames():result.size()));
		if (solicitacaoExameController.isUsuarioSolicExameProtocoloEnfermagem()
				&& (listaExames == null || listaExames.isEmpty())){
			apresentarMsgNegocio(Severity.INFO, this.getBundle().getString("USUARIO_SEM_PROTOCOLO_EXAMES"));
		}
		return listaExames;
	}
	
	/**
	 * Metodo utilizado pela suggestionbox para limpar campos apos a delecao de um exame.
	 */
	public void posDeleteActionSbExames() {
		questionarioSismamaInfComplementaresController.limpar();
		questionarioSismamaController.limpar();
		initItemSolicitacaoExameVo();
	}
	
	/**
	 * Metodo utilizado pela suggestionbox para validar campos apos a selecao de um exame.
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean validarSbExames(boolean criarQuestionario) {
		boolean validou = true;
		try {
			//TODO verificar essa regra
			this.getItemSolicitacaoExameVo().setSolicitacaoExameVO(getSolicitacaoExameVo());
			
			if (this.getSolicitacaoExameVo()!=null && this.getSolicitacaoExameVo().getUnidadeFuncional() != null) {
				
				Boolean permiteUrgente = solicitacaoExameFacade.verificarExamePodeSolicitarUrgente(getItemSolicitacaoExameVo().getUnfExecutaExame().getExameSigla(),
						getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq(),
						getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getUnidadeFuncional().getSeq(),
						getSolicitacaoExameVo().getUnidadeFuncional().getSeq());
				getItemSolicitacaoExameVo().setPermiteUrgente(permiteUrgente);
				Boolean ehUrgente = false;
				if (permiteUrgente){
					ehUrgente = this.solicitacaoExameFacade.verificarUrgenciaItemSolicitacaoExame(this.getSolicitacaoExameVo().getUnidadeFuncional());
				}
				getItemSolicitacaoExameVo().setUrgente(ehUrgente);

				AelSolicitacaoExames soe = new AelSolicitacaoExames();
				soe.setAtendimento(this.getSolicitacaoExameVo().getAtendimento());
				soe.setCriadoEm(new Date());
				
				
				
				AbasIndicadorApresentacaoVO flagAbas = solicitacaoExameFacade.obterIndicadorApresentacaoAbas(
						this.getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame(),
						this.getSolicitacaoExameVo().getUnidadeFuncional(),
						solicitacaoExameController.obterUnidadeTrabalho(),
						getSolicitacaoExameVo().getOrigem(), soe,
						this.getSolicitacaoExameVo().getAtendimento() == null ? null : this.getSolicitacaoExameVo().getAtendimento().getSeq(),
								this.getSolicitacaoExameVo().getAtendimentoDiverso() == null ? null : this.getSolicitacaoExameVo().getAtendimentoDiverso().getSeq(),
										this.itemSolicitacaoExameVo.getIndGeradoAutomatico(), solicitacaoExameController.getIncludeUnidadeTrabalho(),
										getItemSolicitacaoExameVo().getTipoTransporte() == null ? null : DominioTipoTransporteQuestionario.getDominioTipoTransporteQuestionario(getItemSolicitacaoExameVo().getTipoTransporte().toString()));
				
				// Verifica se já existe algum questionario de sismama respondido na lista, se sim, carrega os dados de resposta do ultimo.
				verificarQuestinarioSismamaBiopsiaExistente(flagAbas);
				Map<String, Object> mapSismama = verificarQuestinarioSismamaExistente(flagAbas);				
				this.setQuestionarioSismama(mapSismama);
				if (!mapSismama.isEmpty()) {
					this.questionarioSismamaController.desabilitarCamposCirurgia();
					this.questionarioSismamaController.desabilitarCamposComCaroco();
					this.questionarioSismamaController.desabilitarCampoSemCaroco();
					this.questionarioSismamaController.desabilitarCampoQuando();
					this.questionarioSismamaController.desabilitarCamposRadioterapia();
					this.questionarioSismamaController.desabilitarCampoAnoRadioterapiaDireita();
					this.questionarioSismamaController.desabilitarCampoAnoRadioterapiaEsquerda();
					this.questionarioSismamaInfComplementaresController.habilitarCamposMamoDiag();
					this.questionarioSismamaInfComplementaresController.habilitarCamposAvaliacaoRegiao();
				}
				//Verifica o código situacao default
				verificarSitCodigo();
				this.getItemSolicitacaoExameVo().setMostrarAbaConcentO2(flagAbas.getMostrarAbaConcentO2());
				this.getItemSolicitacaoExameVo().setMostrarAbaIntervColeta(flagAbas.getMostrarAbaIntervColeta());
				this.getItemSolicitacaoExameVo().setMostrarAbaNoAmostras(flagAbas.getMostrarAbaNoAmostras());
				this.getItemSolicitacaoExameVo().setMostrarAbaRegMatAnalise(flagAbas.getMostrarAbaRegMatAnalise());
				this.getItemSolicitacaoExameVo().setMostrarAbaTipoTransporte(flagAbas.getMostrarAbaTipoTransporte());
				this.getItemSolicitacaoExameVo().setMostrarAbaRecomendacoes(flagAbas.getMostrarAbaRecomendacoes());
				this.getItemSolicitacaoExameVo().setMostrarAbaQuestionario(flagAbas.getMostrarAbaQuestionario());
				this.getItemSolicitacaoExameVo().setMostrarAbaQuestionarioSismama(flagAbas.getMostrarAbaQuestionarioSismama());
				this.getItemSolicitacaoExameVo().setMostrarAbaQuestionarioSismamaBiopsia(flagAbas.getMostrarAbaQuestionarioSismamaBiopsia());
				this.getItemSolicitacaoExameVo().setMostrarAbaSituacao(flagAbas.getMostrarAbaSituacao());
				//Busca exames opcionais do exame.
				this.getItemSolicitacaoExameVo().setDependentesOpcionais(solicitacaoExameFacade.obterDependentesOpcionais(this.getItemSolicitacaoExameVo()));
				this.getItemSolicitacaoExameVo().setMostrarAbaExamesOpcionais(!this.getItemSolicitacaoExameVo().getDependentesOpcionais().isEmpty());
				this.desenhaTipoCampoData();
				this.sugerirUltimoTipoTransporte();
				this.sugerirNumeroAmostra();
				//#2249
				if(this.getItemSolicitacaoExameVo().getMostrarAbaRecomendacoes()) {		
					this.getItemSolicitacaoExameVo().setRecomendacaoExameList(flagAbas.getRecomendacaoExameList());			
					List<AelRecomendacaoExame> listaRecomendacaoExamesAlterada = solicitacaoExameFacade.verificarRecomendacaoExameQueSeraoExibidas(this.getItemSolicitacaoExameVo().getRecomendacaoExameList(),this.getItemSolicitacaoExameVo());
					this.getItemSolicitacaoExameVo().setRecomendacaoExameList(listaRecomendacaoExamesAlterada);
					Collections.sort(this.itemSolicitacaoExameVo.getRecomendacaoExameList(), 
							new Comparator<AelRecomendacaoExame>() {
						@Override
						public int compare(AelRecomendacaoExame recomendacaoExame1, AelRecomendacaoExame recomendacaoExame2) {
							int result = recomendacaoExame1.getResponsavel().compareTo(recomendacaoExame2.getResponsavel());
							return result;
						}
					});
				}
				if(this.getItemSolicitacaoExameVo().getMostrarAbaRegMatAnalise()) {
					this.verificarStatusCadastroRegiaoAnatomica();
				}
				if(this.getItemSolicitacaoExameVo().getMostrarAbaQuestionario()) {
				    this.solicitacaoExameController.setRespostas(new ArrayList<AelRespostaQuestao>());
					this.getItemSolicitacaoExameVo().setQuestionarios(flagAbas.getQuestionarios());
					if (validaCriacaoQuestionario(criarQuestionario)){
						this.getItemSolicitacaoExameVo().setRespostasQuestoes(questionarioExamesFacade.criarRespostasQuestionario(flagAbas.getQuestionarios()));					
						Boolean habilitaSuggestionCidsQuestionario = questionarioExamesFacade.verificarSuggestionCidSeraExibidaNoQuestionarioExame(this.getItemSolicitacaoExameVo());
						this.setHabilitouSuggestionCidsQuestionario(habilitaSuggestionCidsQuestionario);
					}	
					this.getItemSolicitacaoExameVo().setGruposQuestao(this.criarGruposQuestao(this.getItemSolicitacaoExameVo().getRespostasQuestoes()));
				 	this.getItemSolicitacaoExameVo().setAghCids(flagAbas.getAghCids());
				} else {
					this.getItemSolicitacaoExameVo().setRespostasQuestoes(null);
					this.getItemSolicitacaoExameVo().setGruposQuestao(null);
				}
			} else {
				apresentarMsgNegocio(Severity.INFO, "MSG_UNIDADE_SOLICIANTE_NAO_INFORMADA");
				initItemSolicitacaoExameVo();
				validou = false;
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			this.getUnfExecutaExame().setSelecionado(false);
			initItemSolicitacaoExameVo();
			validou = false;
		}
		return validou;
	}

	private boolean validaCriacaoQuestionario(boolean criarQuestionario) {
		return criarQuestionario || this.getItemSolicitacaoExameVo().getRespostasQuestoes() == null || this.getItemSolicitacaoExameVo().getRespostasQuestoes().isEmpty();
	}

	// #2257
	protected List<AelGrupoQuestao> criarGruposQuestao(final List<AelRespostaQuestao> respostasQuestoes) {
		AelGrupoQuestao grupo = null;
		
		final String outros = getBundle().getString("LABEL_GRUPO_QUESTAO_NULL");
		
		final List<AelGrupoQuestao> grupos = new ArrayList<AelGrupoQuestao>();
		for (final AelRespostaQuestao aelRespostaQuestao : respostasQuestoes) {
			if (grupo == null
					|| (!grupo.getDescricao().equals(outros) && !grupo.getDescricao().equals(
							aelRespostaQuestao.getQuestaoQuestionario().getAelGrupoQuestao() == null ? null : aelRespostaQuestao.getQuestaoQuestionario()
									.getAelGrupoQuestao().getDescricao()))) {
				if (grupo != null) {
					grupos.add(grupo);
				}
				grupo = this.copiarDadosGrupo(aelRespostaQuestao.getQuestaoQuestionario().getAelGrupoQuestao(), outros);
				grupo.setAelRespostaQuestaos(new ArrayList<AelRespostaQuestao>());
			}
			// busca valor resposta já respondida
			if (this.solicitacaoExameController.getRespostas() != null && !this.solicitacaoExameController.getRespostas().isEmpty()) {
				for (final AelRespostaQuestao aelRespostaQuestaoRespondidas : this.solicitacaoExameController.getRespostas()) {
					if (aelRespostaQuestaoRespondidas.getQuestaoQuestionario().getQuestao().getSeq()
							.equals(aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getSeq())) {
						if (aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getAelValorValidoQuestaos() == null
							|| aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getAelValorValidoQuestaos().isEmpty()) {
							if (DominioTipoDadoQuestionario.C.equals(aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getTipoDados())) {
							aelRespostaQuestao.setAghCid(aelRespostaQuestaoRespondidas.getAghCid());
							} else {
								aelRespostaQuestao.setResposta(aelRespostaQuestaoRespondidas.getResposta());
							}
						} else {
							aelRespostaQuestao.setAelValorValidoQuestao(aelRespostaQuestaoRespondidas.getAelValorValidoQuestao());
						}
					}
				}
			}
			if (aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getAelValorValidoQuestaos() != null
					&& !aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getAelValorValidoQuestaos().isEmpty()) {
				aelRespostaQuestao.getQuestaoQuestionario().getQuestao().setAelValorValidoQuestaosList(this.questionarioExamesFacade.buscarValoresValidosAtivosQuestaoPorQuestao(aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getSeq()));
			}
			grupo.getAelRespostaQuestaos().add(aelRespostaQuestao);
		}
		if (grupo != null) {
			grupos.add(grupo);
		}
		return grupos;
	}

	private AelGrupoQuestao copiarDadosGrupo(final AelGrupoQuestao aelGrupoQuestao, final String outros) {
		if(aelGrupoQuestao == null){
			return new AelGrupoQuestao(0, outros);
		}
		final AelGrupoQuestao retorno = new AelGrupoQuestao();
		retorno.setDescricao(aelGrupoQuestao.getDescricao());
		return retorno;
	}

	public Map<String, Object> verificarQuestinarioSismamaExistente(
			AbasIndicadorApresentacaoVO flagAbas) {
		Map<String,Object> mapSismama = this.itemSolicitacaoExameVo.getQuestionarioSismama();
		if(flagAbas.getMostrarAbaQuestionarioSismama()){
			for(ItemSolicitacaoExameVO item : this.getSolicitacaoExameVo().getItemSolicitacaoExameVos()){
				if(!item.getQuestionarioSismama().isEmpty()){
					Set<String> keys = item.getQuestionarioSismama().keySet();
					for(String key: keys){
						mapSismama.put(key, item.getQuestionarioSismama().get(key));
					}
				}
			}
			if(mapSismama.isEmpty()){
				mapSismama = this.solicitacaoExameFacade.inicializarMapSismama();
			}
		}
		return mapSismama;
	}
	
	public void verificarQuestinarioSismamaBiopsiaExistente(AbasIndicadorApresentacaoVO flagAbas) {
		respostasSismamaBiopsia = this.itemSolicitacaoExameVo.getQuestionarioSismamaBiopsia();
		if(flagAbas.getMostrarAbaQuestionarioSismamaBiopsia()){
			for(ItemSolicitacaoExameVO item : this.getSolicitacaoExameVo().getItemSolicitacaoExameVos()){
				if(!item.getQuestionarioSismamaBiopsia().isEmpty()){
					respostasSismamaBiopsia =  new HashMap<String, Object>();
					Set<String> keys = item.getQuestionarioSismamaBiopsia().keySet();
					for(String key : keys){
						respostasSismamaBiopsia.put(key, item.getQuestionarioSismamaBiopsia().get(key));
					}
				}
			}
			if (respostasSismamaBiopsia==null || respostasSismamaBiopsia.isEmpty()){
				respostasSismamaBiopsia = this.sismamaFacade.recuperarQuestoesRespostasBiopsia();						
			}
			respondeCancerMama = DominioSismamaSimNao.SIM.equals(respostasSismamaBiopsia.get("C_CLI_TANT"));
		}
	}

	/**
	 * Verifica qual código deve ser colocado por default na comboBox de situação do item de exame.
	 * @throws BaseException 
	 */
	private void verificarSitCodigo() throws BaseException {
		AelSitItemSolicitacoes situacao = this.solicitacaoExameFacade.obterSituacaoExameSugestao(
				this.getSolicitacaoExameVo().getUnidadeFuncional(),
				this.getSolicitacaoExameVo().getAtendimento(),
				this.getSolicitacaoExameVo().getAtendimentoDiverso(),
				this.getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame(),
				solicitacaoExameController.obterUnidadeTrabalho(),
				this.getItemSolicitacaoExameVo(),
				this.solicitacaoExameVo
		);
		this.getItemSolicitacaoExameVo().setSituacaoCodigo(situacao);
	}
	/**
	 * Busca a unidade de trabalho.
	 * Se o usuário for Executor então busca sua unidade de trabalho,
	 * Senão retorna null.
	 * a suggestion de unidade executora de trabalho.	
	 * @throws BaseException 
	 */		 
	protected AghUnidadesFuncionais obterUnidadeTrabalho() {
		AghUnidadesFuncionais unidFuncional = null;	
		try {
			unidFuncional = solicitacaoExameFacade.obterUnidadeTrabalhoSolicitacaoExame(solicitacaoExameVo);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		
		return unidFuncional;
	}
	
	public void changeFormaRespiracao() {
		this.getItemSolicitacaoExameVo().setLitrosOxigenio(null);
		this.getItemSolicitacaoExameVo().setPercOxigenio(null);
	}


	public List<AelTmpIntervaloColeta> sbObterIntervaloColeta(String objPesquisa) {
		return this.solicitacaoExameFacade.listarPesquisaIntervaloColeta((String) objPesquisa, this.getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame());
	}

	public void verificaIntervaloPercOxigenio() {
		if(!CoreUtil.isBetweenRange(this.getItemSolicitacaoExameVo().getPercOxigenio(), RANGEINI, RANGEFIM)) {
			apresentarMsgNegocio(Severity.INFO, "MESSAGE_INTERVALO", RANGEINI, RANGEFIM);
		}
	}

	/**	#2254 **/
	public List<AelRegiaoAnatomica> sbObterRegiaoAnatomica(String objPesquisa) {
		if(itemSolicitacaoExameVo.getMostrarAbaQuestionarioSismamaBiopsia()) {
			List<Integer> regioesMama = new ArrayList<Integer>();
			BigDecimal rmd = parametroFacade.getAghParametro(AghuParametrosEnum.P_REGIAO_MAMA_DIR).getVlrNumerico();
			BigDecimal rme = parametroFacade.getAghParametro(AghuParametrosEnum.P_REGIAO_MAMA_ESQ).getVlrNumerico();

			regioesMama.add(rmd.intValue());
			regioesMama.add(rme.intValue());
			return this.solicitacaoExameFacade.listarRegiaoAnatomica((String) objPesquisa, regioesMama);
		} else {
			return this.solicitacaoExameFacade.listarRegiaoAnatomica((String) objPesquisa, null);
		}
	}

	public void verificarStatusCadastroRegiaoAnatomica() throws ApplicationBusinessException {
		AghParametros param = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COD_MATERIAIS_DIVERSOS);
		boolean compareCodigoMaterialExame = false;
		if (param != null) {
			BigDecimal vlrParam = param.getVlrNumerico();			
			compareCodigoMaterialExame = (
					getItemSolicitacaoExameVo().getUnfExecutaExame() != null &&
					getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame() != null &&
					getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise() != null &&
					getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises() != null &&
					getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq() != null &&
					getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq().intValue()
					== vlrParam.intValue()
			);
		}
		if(this.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getIndExigeDescMatAnls()
				|| compareCodigoMaterialExame){
			this.itemSolicitacaoExameVo.setIsExigeDescMatAnls(Boolean.TRUE);
		}

		if(DominioOutrosFarmacos.NAO_CADASTRADO == this.itemSolicitacaoExameVo.getCadastroRegiaoAnatomica()) {
			this.getItemSolicitacaoExameVo().setIsCadastroRegiaoAnatomica(Boolean.TRUE);
		} else {
			this.getItemSolicitacaoExameVo().setIsCadastroRegiaoAnatomica(Boolean.FALSE);
		}

		if(this.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getIndExigeRegiaoAnatomica()) {
			this.getItemSolicitacaoExameVo().setIsExigeRegiaoAnatomica(Boolean.TRUE);
		}

	}

	public void tratarCancerMama(){
		respondeCancerMama = DominioSismamaSimNao.SIM.equals(respostasSismamaBiopsia.get("C_CLI_TANT"));
		if (!respondeCancerMama){
			respostasSismamaBiopsia.put(DominioSismamaHistoCadCodigo.C_CLI_TCIRUR.name(),Boolean.FALSE);
			respostasSismamaBiopsia.put(DominioSismamaHistoCadCodigo.C_CLI_TCIRUROM.name(),Boolean.FALSE);
			respostasSismamaBiopsia.put(DominioSismamaHistoCadCodigo.C_CLI_TQUIM.name(),Boolean.FALSE);
			respostasSismamaBiopsia.put(DominioSismamaHistoCadCodigo.C_CLI_TRADIO.name(),Boolean.FALSE);
			respostasSismamaBiopsia.put(DominioSismamaHistoCadCodigo.C_CLI_TRADIOOM.name(),Boolean.FALSE);
			respostasSismamaBiopsia.put(DominioSismamaHistoCadCodigo.C_CLI_THORM.name(),Boolean.FALSE);
		}
	}

	public void ativaExameImagem(){
		if (!getExameImagem()){
			respostasSismamaBiopsia.put(DominioSismamaHistoCadCodigo.C_CLI_DIAG_IM.name(), null);
		}		
	}	

	public Boolean getExameImagem(){
		return DominioDeteccaoLesao.IMAGEM.equals(respostasSismamaBiopsia.get(DominioSismamaHistoCadCodigo.C_CLI_DETEC.name()));		
	}	
	/**
	 * @param itemSolicitacaoExameVo the itemSolicitacaoExameVo to set
	 */
	public void setItemSolicitacaoExameVo(ItemSolicitacaoExameVO itemSolicitacaoExameVo) {
		this.itemSolicitacaoExameVo = itemSolicitacaoExameVo;
	}
	/**
	 * @return the itemSolicitacaoExameVo
	 */
	public ItemSolicitacaoExameVO getItemSolicitacaoExameVo() {
		return itemSolicitacaoExameVo;
	}
	/**
	 * @param solicitacaoExameVo the solicitacaoExameVo to set
	 */
	public void setSolicitacaoExameVo(SolicitacaoExameVO solicitacaoExameVo) {
		this.solicitacaoExameVo = solicitacaoExameVo;
	}

	/**
	 * @return the solicitacaoExameVo
	 */
	public SolicitacaoExameVO getSolicitacaoExameVo() {
		return solicitacaoExameVo;
	}

	/**
	 * @param situacaoItemSolicitacoes the situacaoItemSolicitacoes to set
	 */
	public void setSituacaoItemSolicitacoes(List<AelSitItemSolicitacoes> situacaoItemSolicitacoes) {
		this.situacaoItemSolicitacoes = situacaoItemSolicitacoes;
	}

	/**
	 * @return the situacaoItemSolicitacoes
	 */
	public List<AelSitItemSolicitacoes> getSituacaoItemSolicitacoes() {
		return situacaoItemSolicitacoes;
	}

	public Boolean getIntervaloHorasObrigatorio() {
		if (getItemSolicitacaoExameVo() != null
				&& getItemSolicitacaoExameVo().getUnfExecutaExame() != null
				&& getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame() != null
				&& getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise() != null) {

			if (getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getUnidTempoColetaAmostras() == DominioUnidTempo.H) {
				if (getItemSolicitacaoExameVo().getIntervaloHoras() == null) {
					//Verifica se tem cadastrado no exame sugestão para horas
					Date horaDefault = getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getTempoHoraAmostraDefault();
					if (horaDefault != null) {
						getItemSolicitacaoExameVo().setIntervaloHoras(horaDefault);
					}
				}
				return true;
			} else {
				if (getItemSolicitacaoExameVo().getIntervaloDias() == null) {
					//		Verifica se tem cadastrado no exame sugestão para dias
					Byte diaDefault = getItemSolicitacaoExameVo().getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getTempoDiaAmostraDefault();
					if (diaDefault != null) {
						getItemSolicitacaoExameVo().setIntervaloDias(diaDefault.intValue());
					}
				}
			}
		}
		return false;
	}

	public UnfExecutaSinonimoExameVO getUnfExecutaExame() {
		return getItemSolicitacaoExameVo().getUnfExecutaExame();
	}
	
	public void setUnfExecutaExame(UnfExecutaSinonimoExameVO unfExecutaExame){
		setUnfExecutaExame(unfExecutaExame, true);
	}
	
	public void setUnfExecutaExame(UnfExecutaSinonimoExameVO unfExecutaExame, boolean criarQuestionario) {
		getItemSolicitacaoExameVo().setUnfExecutaExame(unfExecutaExame);
		if (unfExecutaExame != null && !validarSbExames(criarQuestionario)) {
			getItemSolicitacaoExameVo().setUnfExecutaExame(null);
			unfExecutaExame.setSelecionado(false);
		}

		carregarListaDataHoraProgramada(getItemSolicitacaoExameVo());
	}

	private void carregarListaDataHoraProgramada(ItemSolicitacaoExameVO itemVO) {

		this.listaDataHoraProgramada = doGetListaDatasHorasProgramadas(itemVO);

		if (!itemVO.getCalendar()
				&& (listaDataHoraProgramada == null || listaDataHoraProgramada.isEmpty())) {
			itemVO.setCalendar(true);
			itemVO.setDataProgramada(new Date());
			bloqueiaCalendar = true;
		}
	}

	public Map<String, Object> getRespostasSismamaBiopsia() {
		return respostasSismamaBiopsia;
	}

	public void setRespostasSismamaBiopsia(
			Map<String, Object> respostasSismamaBiopsia) {
		this.respostasSismamaBiopsia = respostasSismamaBiopsia;
	}

	public Boolean getRespondeCancerMama() {
		return respondeCancerMama;
	}

	public void setRespondeCancerMama(Boolean respondeCancerMama) {
		this.respondeCancerMama = respondeCancerMama;
	}

	public void setQuestionarioSismama(Map<String, Object> questionarioSismama) {
		this.questionarioSismama = questionarioSismama;
	}

	public Map<String, Object> getQuestionarioSismama() {
		return questionarioSismama;
	}

	public ItemSolicitacaoExameVO getItemSolicitacaoExameVoSugestao() {
		return itemSolicitacaoExameVoSugestao;
	}

	public void setItemSolicitacaoExameVoSugestao(
			ItemSolicitacaoExameVO itemSolicitacaoExameVoSugestao) {
		this.itemSolicitacaoExameVoSugestao = itemSolicitacaoExameVoSugestao;
	}

	public boolean isHabilitouSuggestionCidsQuestionario() {
		return habilitouSuggestionCidsQuestionario;
	}

	public void setHabilitouSuggestionCidsQuestionario(
			boolean habilitouSuggestionCidsQuestionario) {
		this.habilitouSuggestionCidsQuestionario = habilitouSuggestionCidsQuestionario;
	}

    public boolean isBloqueiaCalendar() {
        return bloqueiaCalendar;
    }

    public void setBloqueiaCalendar(boolean bloqueiaCalendar) {
        this.bloqueiaCalendar = bloqueiaCalendar;
    }

    public List<DataProgramadaVO> getListaDataHoraProgramada() {
        return listaDataHoraProgramada;
    }

    public void setListaDataHoraProgramada(List<DataProgramadaVO> listaDataHoraProgramada) {
        this.listaDataHoraProgramada = listaDataHoraProgramada;
    }

    public ItemExameReferencia getItemExameReferencia() {
        return itemExameReferencia;
    }

    public void setItemExameReferencia(ItemExameReferencia itemExameReferencia) {
        this.itemExameReferencia = itemExameReferencia;
    }

	private class ItemExameReferencia {

        private Boolean urgente;
        private Date dataProgramada;
        private Date dataProgramadaCombo;
        private boolean calendar;
		private DominioTipoTransporte tipoTransporte;
		private DominioSimNao oxigenioTransporte;
		private DominioFormaRespiracao formaRespiracao;
		private BigDecimal litrosOxigenio;
		private Integer percOxigenio;
		
		
		public ItemExameReferencia() {
			// TODO Auto-generated constructor stub
		}

		public void carregarDados(ItemSolicitacaoExameVO itemReferenciaVO) {

			this.setUrgente(itemReferenciaVO.getUrgente());

			if (itemReferenciaVO.getCalendar()) {
				this.setDataProgramada(itemReferenciaVO.getDataProgramada());
			} else {
				this.setDataProgramadaCombo(itemReferenciaVO.getDataProgramada());
			}
			
//			this.setCalendar(itemReferenciaVO.getCalendar());
			
			if (itemReferenciaVO.getMostrarAbaTipoTransporte() != null && itemReferenciaVO.getMostrarAbaTipoTransporte()) {
				if (itemReferenciaVO.getTipoTransporte() != null) {
					this.setTipoTransporte(itemReferenciaVO.getTipoTransporte());
				}

				if (itemReferenciaVO.getOxigenioTransporte() != null) {
					this.setOxigenioTransporte(itemReferenciaVO.getOxigenioTransporte());
				}
			}

			if (itemReferenciaVO.getMostrarAbaConcentO2() != null && itemReferenciaVO.getMostrarAbaConcentO2()) {

				if (itemReferenciaVO.getFormaRespiracao() != null) {
					this.setFormaRespiracao(itemReferenciaVO.getFormaRespiracao());
				}

				if (itemReferenciaVO.getLitrosOxigenio() != null) {
					this.setLitrosOxigenio(itemReferenciaVO.getLitrosOxigenio());
				}

				if (itemReferenciaVO.getPercOxigenio() != null) {
					this.setPercOxigenio(itemReferenciaVO.getPercOxigenio());
				}
			}
		}

		public Boolean getUrgente() {
            return urgente;
        }

        public void setUrgente(Boolean urgente) {
            this.urgente = urgente;
        }

        public Date getDataProgramada() {
            return dataProgramada;
        }

        public void setDataProgramada(Date dataProgramada) {
            this.dataProgramada = dataProgramada;
        }

        public Date getDataProgramadaCombo() {
            return dataProgramadaCombo;
        }

        public void setDataProgramadaCombo(Date dataProgramadaCombo) {
            this.dataProgramadaCombo = dataProgramadaCombo;
        }

		public DominioTipoTransporte getTipoTransporte() {
			return tipoTransporte;
		}

		public void setTipoTransporte(DominioTipoTransporte tipoTransporte) {
			this.tipoTransporte = tipoTransporte;
		}

		public DominioSimNao getOxigenioTransporte() {
			return oxigenioTransporte;
		}

		public void setOxigenioTransporte(DominioSimNao oxigenioTransporte) {
			this.oxigenioTransporte = oxigenioTransporte;
		}

		public DominioFormaRespiracao getFormaRespiracao() {
			return formaRespiracao;
		}

		public void setFormaRespiracao(DominioFormaRespiracao formaRespiracao) {
			this.formaRespiracao = formaRespiracao;
		}

		public BigDecimal getLitrosOxigenio() {
			return litrosOxigenio;
		}

		public void setLitrosOxigenio(BigDecimal litrosOxigenio) {
			this.litrosOxigenio = litrosOxigenio;
		}

		public Integer getPercOxigenio() {
			return percOxigenio;
		}

		public void setPercOxigenio(Integer percOxigenio) {
			this.percOxigenio = percOxigenio;
		}

		public boolean isCalendar() {
			return calendar;
		}

		public void setCalendar(boolean calendar) {
			this.calendar = calendar;
		}
    }

	
	public ExameSuggestionVO getExameSuggestionVO() {
	    if (exameSuggestionVO == null){
		 UnfExecutaSinonimoExameVO unfExecutaExame = getUnfExecutaExame();
		 if (unfExecutaExame!=null){
		     try {
		    	 exameSuggestionVO = new ExameSuggestionVO(unfExecutaExame.getExameSigla(), unfExecutaExame.getExameSigla(),
				     unfExecutaExame.getUnfExecutaExame().getAelExamesMaterialAnalise().getAelExames().getDescricaoUsual(),
				     unfExecutaExame.getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao(),
				     unfExecutaExame.getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq(),
				     unfExecutaExame.getUnfExecutaExame().getUnidadeFuncional().getSeq(),
				     unfExecutaExame.getUnfExecutaExame().getUnidadeFuncional().getDescricao());
		     } catch (Exception e ){
		    	 LOG.error(e.getMessage(), e);
			 	exameSuggestionVO = new ExameSuggestionVO();
		     }
		 }
	    }
	    return exameSuggestionVO;
	}
	
	public void setExameSuggestionVO(ExameSuggestionVO exameSuggestionVO) {
	    this.exameSuggestionVO = exameSuggestionVO;
	    if (exameSuggestionVO != null){
			AelUnfExecutaExames unfexe =  examesFacade.obterAelUnfExecutaExames(exameSuggestionVO.getExameSigla(), exameSuggestionVO.getManSeq(), exameSuggestionVO.getUfeUnfSeq());
			UnfExecutaSinonimoExameVO unfExecutaExame = new UnfExecutaSinonimoExameVO(unfexe);
			setUnfExecutaExame(unfExecutaExame);
	    }
	}
	
	public void pesquisarQuestionarioPorExame() {
		
		validarSbExames(true);
		/*
		ItemSolicitacaoExameVO vo = getItemSolicitacaoExameVo();
		
		if (vo.getTipoTransporte() == null) {
			vo.setMostrarAbaQuestionario(false);
		}
		else {
			try {
				List<AelQuestionarios> questionarios = questionarioExamesFacade.pesquisarQuestionarioPorExame(exameSuggestionVO.getExameSigla(), 
						exameSuggestionVO.getManSeq(), getSolicitacaoExameVo().getOrigem(), 
						DominioTipoTransporteQuestionario.getDominioTipoTransporteQuestionario(vo.getTipoTransporte().toString()));
				
				vo.setMostrarAbaQuestionario(!questionarios.isEmpty());
				
				if(this.getItemSolicitacaoExameVo().getMostrarAbaQuestionario()) {
				    this.solicitacaoExameController.setRespostas(new ArrayList<AelRespostaQuestao>());
					this.getItemSolicitacaoExameVo().setQuestionarios(questionarios);
	
					this.getItemSolicitacaoExameVo().setRespostasQuestoes(questionarioExamesFacade.criarRespostasQuestionario(vo.getQuestionarios()));					
					Boolean habilitaSuggestionCidsQuestionario = questionarioExamesFacade.verificarSuggestionCidSeraExibidaNoQuestionarioExame(this.getItemSolicitacaoExameVo());
					this.setHabilitouSuggestionCidsQuestionario(habilitaSuggestionCidsQuestionario);
					this.getItemSolicitacaoExameVo().setGruposQuestao(this.criarGruposQuestao(this.getItemSolicitacaoExameVo().getRespostasQuestoes()));
				 	//this.getItemSolicitacaoExameVo().setAghCids(flagAbas.getAghCids());
				} else {
					this.getItemSolicitacaoExameVo().setRespostasQuestoes(null);
					this.getItemSolicitacaoExameVo().setGruposQuestao(null);
				}

			} catch(BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}*/
	}

	public DominioTipoPesquisaExame getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(DominioTipoPesquisaExame tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	
	public Integer getIndexAbaAtiva() {
		return indexAbaAtiva;
	}

	
	public void setIndexAbaAtiva(Integer indexAbaAtiva) {
		this.indexAbaAtiva = indexAbaAtiva;
	}
	
	
}
