package br.gov.mec.aghu.exames.action;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.util.Eval;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.ejb.EJB;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectItem;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.convert.NumberConverter;
import javax.faces.validator.DoubleRangeValidator;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.dialog.Dialog;
import org.primefaces.component.editor.Editor;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.tooltip.Tooltip;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.components.UIDiv;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioExibicaoParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioFormaRespiracao;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioSubTipoImpressaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.business.IMascaraExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.DesenhoMascaraExameVO;
import br.gov.mec.aghu.exames.vo.NumeroApTipoVO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExtratoItemSolicHist;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadosExamesHist;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelValorNormalidCampo;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.CyclomaticComplexity", "PMD.NcssTypeCount", "PMD.NcssMethodCount", "PMD.NPathComplexity"})
public class MascaraExamesComponentes extends ActionController implements Serializable {
	private static final long serialVersionUID = 4045815309343737605L;
	private static final Log LOG = LogFactory.getLog(MascaraExamesComponentes.class);
	
	private static final String FONT_FAMILY_COURIER_NEW_FONT_SIZE_8PT = "font-family: Courier New; font-size: 8pt;";
	private static final String HEIGHT = " height: ";
	private static final String WIDTH = " width: ";
	private static final String WIDTH_680PX = " width: 680px;";
	private static final String PX = "px;";
	private static final String LEFT = " left: ";
	private static final String TOP = " top: ";
	private static final String _HIFEN_ = " - ";
	private static final String TEMP_ = "temp_";
	private static final String SUB_DIV = "subDiv";
	private static final Integer MIN_HEIGHT_TEXTAREA = 100;
	
	private List<Editor> listaEditor = new ArrayList<Editor>();
	private List<Editor> listaEditorGrande = new ArrayList<Editor>();
	private Integer contListaEditor = 0;
	private static final String ID_EDITOR_MODAL = "modalEditor";
	

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IMascaraExamesFacade mascaraExamesFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject
	private ManterMascaraExamesPreviaController manterMascaraExamesPreviaController;
	
	@Inject
	private CadastroResultadoPadraoLaudoController cadastroResultadoPadraoLaudoController;

	@EJB
	private IExamesFacade examesFacade;

	private int unique = 0;
	
	private String tipoLaudoMascara;
	

	
	
	/**
	 * Constante para o nome do atributo de cada componente que armazenará a
	 * instância de AelParametroCamposLaudo correspondente.
	 */
	protected static final String NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO = "PARAMETRO_CAMPO_LAUDO";

	/**
	 * Constante para o nome do atributo que representa o identificador, que
	 * seria o atributo nome de AelCampoLaudo concatenado com o sequencial de
	 * AelParametrosCampoLaudo. Usado principalmente nas formulas de campo do
	 * tipo expressão.
	 */
	protected static final String NOME_ATRIBUTO_IDENTIFICADOR = "IDENTIFICADOR";


	/** Idade usada na prévia da mascara de laudo */
	protected static final Integer IDADE_PREVIA = 50;

	/** Sexo usado na prévia da mascara de laudo */
	protected static final String SEXO = "M";

	public enum MascaraExamesONExceptionCode implements BusinessExceptionCode {
		MSG_VERSAO_LAUDO, MSG_VERSAO_LAUDO_NE, MSG_EXCESSAO_EXPRESSAO_PARENTESES, MSG_EXCESSAO_EXPRESSAO_COLCHETES, MSG_EXCESSAO_VALOR_NORMAL_INVALIDO,
		MSG_EXCESSAO_CAMPO_LAUDO_NAO_ENCONTRADO;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

		public void throwExceptionRollback(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

		public void throwException(Throwable cause, Object... params) throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}
	
	public enum TipoLaudoMascara{
		LAUDO_PREVIA,
		LAUDO_RESULTADO_PADRAO,
		LAUDO_RESULTADO_NOTA_ADICIONAL;
	}

	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	private DesenhoMascaraExameVO montaPreviaMascarasExamesVO(List<AelParametroCamposLaudo> camposDaVersaoLaudoPrevia,
			AelVersaoLaudo versaoLaudo, NumeroApTipoVO solicNumeroAp)
			throws BaseException {
		AelItemSolicitacaoExames itemSolicitacaoExame = null;

		DesenhoMascaraExameVO desenhoMascaraExameVO = new DesenhoMascaraExameVO();

		Map<String, Set<String>> mapaDependentesExpressao = new HashMap<String, Set<String>>();
		Map<String, String> mapaFormulas = new HashMap<String, String>();
		Map<String, Script> mapaScripts = new HashMap<String, Script>();

		// busca os resultados para o item do exame, se existirem.
		Map<AelParametroCamposLaudo, AelResultadoExame> resultadosPrevia = this.mascaraExamesFacade
				.obterMapaResultadosFicticiosExames(camposDaVersaoLaudoPrevia);

		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		OutputPanel formularioDinamicoPanel = (OutputPanel) application.createComponent(OutputPanel.COMPONENT_TYPE);

		Integer maiorAltura = 0;
		Integer acrescimoCamposTexto = 0;
		
		List<UIComponent> listaComponentes = new ArrayList<UIComponent>(camposDaVersaoLaudoPrevia.size());
		List<String> listaComponentesDinamicos = new ArrayList<String>();
		
		Integer tamanhoLista = camposDaVersaoLaudoPrevia.size() - 1;
		Integer ultimoItemLista = 0;
		Integer calculoPosicao = 0;
		Integer tamanhoAdicionalDivPrincipal = 100;
		this.setTipoLaudoMascara(TipoLaudoMascara.LAUDO_PREVIA);
		for (AelParametroCamposLaudo parametroCampoLaudo : camposDaVersaoLaudoPrevia) {
			UIComponent componente = criarComponente(context, parametroCampoLaudo, resultadosPrevia.get(parametroCampoLaudo),
					itemSolicitacaoExame, mapaFormulas, mapaDependentesExpressao, desenhoMascaraExameVO, true);

			if (componente != null) {
				StringBuffer estilo = new StringBuffer();
				estilo.append("position: absolute;");

				if(DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(parametroCampoLaudo.getObjetoVisual()) 
						|| DominioObjetoVisual.TEXTO_ALFANUMERICO.equals(parametroCampoLaudo.getObjetoVisual())){
					estilo.append(" z-index:10; ");
				}
				
				if (parametroCampoLaudo.getLarguraObjetoVisual() != null) {
					if (parametroCampoLaudo.getLarguraObjetoVisual() > 700) {
						estilo.append(WIDTH_680PX);
					} else {
						estilo.append(WIDTH).append(parametroCampoLaudo.getLarguraObjetoVisual()).append(PX);
					}
				}

				if (parametroCampoLaudo.getAlturaObjetoVisual() != null) {
					estilo.append(HEIGHT).append(parametroCampoLaudo.getAlturaObjetoVisual()).append(PX);
				}

					int altura = parametroCampoLaudo.getPosicaoLinhaTela();

					if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())
							&& (parametroCampoLaudo.getAlturaObjetoVisual() == null || parametroCampoLaudo.getAlturaObjetoVisual() == 0)) {
						parametroCampoLaudo.setAlturaObjetoVisual((short) 100);
					}

					if (parametroCampoLaudo.getAlturaObjetoVisual() != null) {
						altura += parametroCampoLaudo.getAlturaObjetoVisual();
					}
					
					if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())) {
						altura += 30; //se for texto logo adicionar 30 que Ã© da toolbar do componente
					}

					if (altura > maiorAltura) {
						maiorAltura = altura;
					}

					if (parametroCampoLaudo.getPosicaoLinhaTela() != null) {
						calculoPosicao = parametroCampoLaudo.getPosicaoLinhaTela().intValue() + acrescimoCamposTexto;
						estilo.append(TOP).append(calculoPosicao).append(PX);
					}
					
					if(tamanhoLista == ultimoItemLista){
						manterMascaraExamesPreviaController.setAlturaDiv(calculoPosicao + tamanhoAdicionalDivPrincipal);
					}
					
					if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())) {
						if(parametroCampoLaudo.getAlturaObjetoVisual() < 100){
							acrescimoCamposTexto += 100 - parametroCampoLaudo.getAlturaObjetoVisual();
						}
					}
					
					if (parametroCampoLaudo.getPosicaoColunaTela() != null) {
						estilo.append(LEFT).append(parametroCampoLaudo.getPosicaoColunaTela()).append(PX);
					}
					
					if(listaComponentesDinamicos.contains(componente.getId())) {
						continue;
					}

					UIComponent div = criarDiv(null, estilo.toString());
					div.getChildren().add(componente);
					
					ultimoItemLista++;
					listaComponentesDinamicos.add(componente.getId());
					listaComponentes.add(div);

			}
		}

		/* Cabeçalho dos exames CriaCabecExame */

		/*
		 * Informações sobre respiração
		 */
		AelItemSolicitacaoExames itemSolicitacaoExameAux = new AelItemSolicitacaoExames();
		itemSolicitacaoExameAux.setFormaRespiracao(DominioFormaRespiracao.TRES);
		itemSolicitacaoExameAux.setPercOxigenio((short) 50);

		/*
		 * Informações sobre médico
		 */
		itemSolicitacaoExameAux.setServidorResponsabilidade(new RapServidores());

		UIComponent divPrincipal = criarDiv("divPrincipal0_" + new Date().getTime(), "position: relative; width:800px; height: "
				+ (maiorAltura + 30) + "px; margin-bottom: 20px;");

		divPrincipal.getChildren().addAll(listaComponentes);

		// criando botao
		formularioDinamicoPanel.getChildren().add(divPrincipal);

		// Adiciona reRender ao componentes que possuem dependentes.
		adicionarAjaxSupport(application, formularioDinamicoPanel, mapaDependentesExpressao, context);

		// cria o mapa de scripts
		criarMapaScripts(formularioDinamicoPanel, mapaFormulas, mapaScripts);

		desenhoMascaraExameVO.setFormularioDinamicoPanel(formularioDinamicoPanel);
		desenhoMascaraExameVO.setMapaDependentesExpressao(mapaDependentesExpressao);
		desenhoMascaraExameVO.setMapaFormulas(mapaFormulas);
		desenhoMascaraExameVO.setMapaScripts(mapaScripts);
		// desenhoMascaraExameVO.setItemSolicitacaoExame(itemSolicitacaoExame);

		StringBuffer descricaoExameMaterial = new StringBuffer(70);
		descricaoExameMaterial.append("sigla prévia - descrição exame prévia");

		if (itemSolicitacaoExameAux.getMaterialAnalise() != null) {
			descricaoExameMaterial.append(_HIFEN_);
			descricaoExameMaterial.append("material análise prévia");
		}

		desenhoMascaraExameVO.setDescricaoExameMaterial(descricaoExameMaterial.toString());

		List<DesenhoMascaraExameVO> lista = new ArrayList<DesenhoMascaraExameVO>();
		lista.add(desenhoMascaraExameVO);
		// TODO
		// this.atribuirContextoSessao(VariaveisSessaoEnum.AEL_LISTA_DESENHOS_MASCARAS_EXAMES.toString(),
		// lista);

		return desenhoMascaraExameVO;
	}

	/**
	 * @HIST MascaraExamesHistON.montaDesenhosMascarasExamesVOHist
	 * @param solicitacaoExameSeq
	 * @param itemSolicitacaoExameSeq
	 * @param tipoMascaraExame
	 * @param velSeqp
	 * @param isHist
	 * @return
	 * @throws BaseException
	 */
	private List<DesenhoMascaraExameVO> montaDesenhosMascarasExamesVO(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq,
		    DominioSubTipoImpressaoLaudo subTipoImpressaoLaudo, Integer velSeqp,
			Boolean isHist, boolean ultimoItem, List<Short> seqps) throws BaseException {
		return montaDesenhosMascarasExamesVO(solicitacaoExameSeq, itemSolicitacaoExameSeq, subTipoImpressaoLaudo, velSeqp, isHist, ultimoItem, seqps, null, null);
	}
	
	
	
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity", "PMD.NcssMethodCount" })
	private List<DesenhoMascaraExameVO> montaDesenhosMascarasExamesVO(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq,
			DominioSubTipoImpressaoLaudo subTipoImpressaoLaudo, Integer velSeqp,
			Boolean isHist, boolean ultimoItem, List<Short> seqps, Map<Integer, NumeroApTipoVO> solicNumeroAp, Object item) throws BaseException {
		List<DesenhoMascaraExameVO> lista = new ArrayList<DesenhoMascaraExameVO>();

		List<AelItemSolicitacaoExames> itensSolicitacoesExames = null;
		List<AelItemSolicExameHist> itensSolicitacoesExamesHist = null;

		Integer sizeListaItensExames=1;
		
		if (item==null){
			if (isHist) {
				itensSolicitacoesExamesHist = mascaraExamesFacade.pesquisarItensSolicitacoesExamesHist(solicitacaoExameSeq,
						itemSolicitacaoExameSeq);
				sizeListaItensExames = itensSolicitacoesExamesHist.size();
			} else {
				itensSolicitacoesExames = this.mascaraExamesFacade.pesquisarItensSolicitacoesExames(solicitacaoExameSeq,
						itemSolicitacaoExameSeq);
				sizeListaItensExames = itensSolicitacoesExames.size();
			}
		}	

		for (int i = 0; i < sizeListaItensExames; i++) {
			AelItemSolicitacaoExames itemSolicitacaoExame = null;
			AelItemSolicExameHist itemSolicitacaoExameHist = null;
			Object itemSolicitacaoExameObject = null;
			
			if (item!=null){
				if (item instanceof AelItemSolicExameHist) {
					itemSolicitacaoExameHist = (AelItemSolicExameHist) item;
					itemSolicitacaoExameObject = item;
				} else {
					itemSolicitacaoExame = (AelItemSolicitacaoExames)item;
					itemSolicitacaoExameObject = item;
				}				
			}else{
				if (isHist) {
					itemSolicitacaoExameHist = itensSolicitacoesExamesHist.get(i);
					itemSolicitacaoExameObject = itemSolicitacaoExameHist;
				} else {
					itemSolicitacaoExame = itensSolicitacoesExames.get(i);
					itemSolicitacaoExameObject = itemSolicitacaoExame;
				}
			}	

			DesenhoMascaraExameVO desenhoMascaraExameVO = new DesenhoMascaraExameVO();

			Map<String, Set<String>> mapaDependentesExpressao = new HashMap<String, Set<String>>();
			Map<String, String> mapaFormulas = new HashMap<String, String>();
			Map<String, Script> mapaScripts = new HashMap<String, Script>();

			// busca os resultados para o item do exame, se existirem.
			Map<AelParametroCampoLaudoId, Object> resultados = mascaraExamesFacade.obterMapaResultadosExames(itemSolicitacaoExameObject);
			if (resultados != null && !resultados.isEmpty()) {
				if (isHist) {
					AelResultadosExamesHist resultado = (AelResultadosExamesHist) resultados.values().iterator().next();
					velSeqp = resultado.getId().getPclVelSeqp();
				} else {
					AelResultadoExame resultado = (AelResultadoExame) resultados.values().iterator().next();
					velSeqp = resultado.getId().getPclVelSeqp();
				}
			}

			desenhoMascaraExameVO.setPossuiResultados(resultados != null && !resultados.isEmpty());

			// obtem a versão do laudo referente ao exame e material de análise.
			AelVersaoLaudo versaoLaudo = null;
			AelExames aelExame = null;
			AelMateriaisAnalises aelMateriaisAnalises;
			if (isHist) {
				aelExame = examesFacade.obterAelExamesPeloId(itemSolicitacaoExameHist.getExame().getSigla());
				aelMateriaisAnalises = itemSolicitacaoExameHist.getMaterialAnalise();
			} else {
				aelExame = examesFacade.obterAelExamesPeloId(itemSolicitacaoExame.getExame().getSigla());
				aelMateriaisAnalises = itemSolicitacaoExame.getMaterialAnalise();
			}
			versaoLaudo = this.mascaraExamesFacade.obterVersaoLaudoPorItemSolicitacaoExame(aelExame, aelMateriaisAnalises, velSeqp);
			if (versaoLaudo == null) {
				throw new ApplicationBusinessException(MascaraExamesONExceptionCode.MSG_VERSAO_LAUDO_NE, aelExame.getSigla());
			}

			// busca os campos a serem exibidos na máscara.
			List<AelParametroCamposLaudo> camposDaVersaoLaudo = null;
			camposDaVersaoLaudo = this.mascaraExamesFacade.pesquisarCamposTelaPorVersaoLaudo(versaoLaudo);

			FacesContext context = FacesContext.getCurrentInstance();
			Application application = context.getApplication();
			OutputPanel formularioDinamicoPanel = (OutputPanel) application
					.createComponent(OutputPanel.COMPONENT_TYPE);

			Map<AelParametroCamposLaudo, UIComponent> camposMap = new HashMap<AelParametroCamposLaudo, UIComponent>();
			this.setTipoLaudoMascara(TipoLaudoMascara.LAUDO_RESULTADO_NOTA_ADICIONAL);
			for (AelParametroCamposLaudo parametroCampoLaudo : camposDaVersaoLaudo) {
				UIComponent componente = this.criarComponente(context,
						parametroCampoLaudo,
						resultados.get(parametroCampoLaudo.getId()),
						itemSolicitacaoExameObject, mapaFormulas,
						mapaDependentesExpressao, desenhoMascaraExameVO, false);

				camposMap.put(parametroCampoLaudo, componente);
			}

			Map<Integer, PosicaoTelaVO> mapPosicoesTelaVO = new HashMap<Integer, PosicaoTelaVO>();

			Integer maiorAltura = 0;
			List<UIComponent> listaComponentes = new ArrayList<UIComponent>(camposDaVersaoLaudo.size());

			/* Imprime o recebimento do exame */
			int descontoEspacoBranco = 0;
			int acrescimoCamposTexto = 0;
			int acrescimoCamposTextoFinalDiv = 0;
			int ultimaLinha = 0;
			int qtdeCamposLongosLaudoImpressao = 0;

			for (AelParametroCamposLaudo parametroCampoLaudo : camposDaVersaoLaudo) {
				UIComponent componente = camposMap.get(parametroCampoLaudo);
				this.mascaraExamesFacade.desatacharAelParametroCamposLaudo(parametroCampoLaudo);
				// para evitar alteração nesse objeto

				if (componente != null) {
					Integer posicaoFinalLinhaTela = parametroCampoLaudo.posicaoFinalLinhaTela();

					PosicaoTelaVO vo = null;
					if (mapPosicoesTelaVO.containsKey(posicaoFinalLinhaTela)) {
						vo = mapPosicoesTelaVO.get(posicaoFinalLinhaTela);
						mapPosicoesTelaVO.remove(posicaoFinalLinhaTela);
					}

					if (LOG.isDebugEnabled()) {
						LOG.debug("-- " + parametroCampoLaudo.getId());
						if (vo != null) {
							LOG.debug(vo);
						}
						LOG.debug("descontoEspacoBranco " + descontoEspacoBranco);
						LOG.debug("acrescimoCamposTexto " + acrescimoCamposTexto);
						LOG.debug("acrescimoCamposTextoFinalDiv " + acrescimoCamposTextoFinalDiv);
						LOG.debug("ultimaLinha " + ultimaLinha);
						LOG.debug("qtdeCamposLongosLaudoImpressao " + qtdeCamposLongosLaudoImpressao);
					}

					StringBuffer estilo = new StringBuffer();
					estilo.append(" position: absolute;");
					
					if(DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(parametroCampoLaudo.getObjetoVisual()) 
							|| DominioObjetoVisual.TEXTO_ALFANUMERICO.equals(parametroCampoLaudo.getObjetoVisual())){
						estilo.append(" z-index:10; ");
					}

					if (parametroCampoLaudo.getLarguraObjetoVisual() != null) {
						if (parametroCampoLaudo.getLarguraObjetoVisual() > 700) {
							estilo.append(WIDTH_680PX);
						} else {
							estilo.append(WIDTH).append(parametroCampoLaudo.getLarguraObjetoVisual()).append(PX);
						}
					}

					if (parametroCampoLaudo.getAlturaObjetoVisual() != null) {
						estilo.append(HEIGHT).append(parametroCampoLaudo.getAlturaObjetoVisual()).append(PX);
					}

						int altura = parametroCampoLaudo.getPosicaoLinhaTela();

						if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())
								&& (parametroCampoLaudo.getAlturaObjetoVisual() == null || parametroCampoLaudo.getAlturaObjetoVisual() == 0)) {
							parametroCampoLaudo.setAlturaObjetoVisual((short) 100);
							altura += 40;
						}

						if (parametroCampoLaudo.getAlturaObjetoVisual() != null) {
							altura += parametroCampoLaudo.getAlturaObjetoVisual();
						}
						
						if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())) {
							altura += 30; //se for texto logo adicionar 30 que Ã© da toolbar do componente
						}
						
						if (altura > maiorAltura) {
							maiorAltura = altura;
						}

						if (parametroCampoLaudo.getPosicaoLinhaTela() != null) {
							int calculoPosicao = parametroCampoLaudo.getPosicaoLinhaTela().intValue() + acrescimoCamposTexto;
							estilo.append(TOP).append(calculoPosicao).append(PX);
						}
						
						if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())) {
							if(parametroCampoLaudo.getAlturaObjetoVisual() < 100){
								acrescimoCamposTexto += 100 - parametroCampoLaudo.getAlturaObjetoVisual();
							}
						}

						if (parametroCampoLaudo.getPosicaoColunaTela() != null) {
							estilo.append(LEFT).append(parametroCampoLaudo.getPosicaoColunaTela()).append(PX);
						}

						UIComponent div = criarDiv(null, estilo.toString());
						div.getChildren().add(componente);

						listaComponentes.add(div);

				}
			}

			maiorAltura = maiorAltura - descontoEspacoBranco + acrescimoCamposTexto;
			/* Cabeçalho dos exames CriaCabecExame */

			// Notas adicionais

			UIComponent divPrincipal = criarDiv("divPrincipal" + i + "_" + new Date().getTime(),
					"position: relative; width:750px; height: " + (maiorAltura) + "px; margin-bottom: 30px;");

			divPrincipal.getChildren().addAll(listaComponentes);

			// criando botao
			formularioDinamicoPanel.getChildren().add(divPrincipal);

			// Adiciona reRender ao componentes que possuem dependentes.
			adicionarAjaxSupport(application, formularioDinamicoPanel, mapaDependentesExpressao, context);

			// cria o mapa de scripts
			criarMapaScripts(formularioDinamicoPanel, mapaFormulas, mapaScripts);

			desenhoMascaraExameVO.setFormularioDinamicoPanel(formularioDinamicoPanel);
			desenhoMascaraExameVO.setMapaDependentesExpressao(mapaDependentesExpressao);
			desenhoMascaraExameVO.setMapaFormulas(mapaFormulas);
			desenhoMascaraExameVO.setMapaScripts(mapaScripts);
			desenhoMascaraExameVO.setItemSolicitacaoExame(itemSolicitacaoExameObject);

			AelMateriaisAnalises materialAnalise;
			if (isHist) {
				materialAnalise = examesFacade.obterMaterialAnalisePeloId(itemSolicitacaoExameHist.getMaterialAnalise().getSeq());
			} else {
				materialAnalise = examesFacade.obterMaterialAnalisePeloId(itemSolicitacaoExame.getMaterialAnalise().getSeq());
			}

			StringBuffer descricaoExameMaterial = new StringBuffer();
			descricaoExameMaterial.append(aelExame.getSigla())
			.append(_HIFEN_)
			.append(aelExame.getDescricao());

			if (materialAnalise != null) {
				descricaoExameMaterial.append(_HIFEN_)
				.append(materialAnalise.getDescricao());
			}

				desenhoMascaraExameVO.setDescricaoExameMaterial(descricaoExameMaterial.toString());
				desenhoMascaraExameVO.setNomeExamePatologia(null);

			lista.add(desenhoMascaraExameVO);
		}

		// TODO
		// this.atribuirContextoSessao(VariaveisSessaoEnum.AEL_LISTA_DESENHOS_MASCARAS_EXAMES.toString(),
		// lista);
		return lista;
	}

	/**
	 * Método que cria o mapa de scripts a partir do mapa de formulas.
	 * 
	 * @param mapaFormulas
	 * @param formularioDinamicoPanel
	 * @param mapaScripts
	 */
	protected void criarMapaScripts(OutputPanel formularioDinamicoPanel, Map<String, String> mapaFormulas,
			Map<String, Script> mapaScripts) throws ApplicationBusinessException {
		for (Entry<String, String> entradaMapaFormulas : mapaFormulas.entrySet()) {
			String formula = comandosToLowerCase(entradaMapaFormulas.getValue());
			String[] operandos = StringUtils.substringsBetween(formula, "[", "]");
			Binding binding = new Binding();
			if (operandos != null) {
				UIInput input = null;
				for (String operando : operandos) {
					input = (UIInput) this.encontrarComponentePorAtributoIdentificador(operando, formularioDinamicoPanel.getChildren());
					if(input == null){
						throw new ApplicationBusinessException(MascaraExamesONExceptionCode.MSG_EXCESSAO_CAMPO_LAUDO_NAO_ENCONTRADO, Severity.ERROR, operando);
					}
					String id = input.getId();
					formula = StringUtils.replace(formula, "[" + operando + "]", id);
					binding.setVariable(id, null);
				}
				GroovyShell shell = new GroovyShell(binding);
				// incluindo import para correta avaliação das funções oracle
				// nas formulas.
				StringBuffer formulasb = new StringBuffer("import static br.gov.mec.aghu.core.persistence.dialect.FuncoesOracle.*; ");
				formulasb.append(retornaParametroCampoLaudoTextoLivreSemTag(formula));
				Script script = shell.parse(formulasb.toString());
				mapaScripts.put(entradaMapaFormulas.getKey(), script);
			}
		}
	}

	/**
	 * Método que adiciona ajaxSupport aos campos que precisam reRenderizar
	 * outros, geralmente porque as formulas daqueles dependem do valor destes.
	 * 
	 * @param application
	 * @param mapaDependentesExpressao
	 * @param formularioDinamicoPanel
	 */
	protected void adicionarAjaxSupport(Application application, OutputPanel formularioDinamicoPanel,
			Map<String, Set<String>> mapaDependentesExpressao, FacesContext context) {
		for (Entry<String, Set<String>> entradaMapaDependencias : mapaDependentesExpressao.entrySet()) {
			UIComponentBase operando = (UIComponentBase)this.encontrarComponentePorAtributoIdentificador(entradaMapaDependencias.getKey(), formularioDinamicoPanel.getChildren());
			
			if (operando != null) {
				AjaxBehavior support = (AjaxBehavior) application.createBehavior(AjaxBehavior.BEHAVIOR_ID);
				support.setProcess("@this");
				support.setUpdate(StringUtils.substringBetween(entradaMapaDependencias.getValue().toString(), "[", "]"));
					
				if (operando instanceof SelectOneMenu || operando instanceof HtmlSelectOneMenu) {
					operando.addClientBehavior( "change", support);
				} else {
					operando.addClientBehavior( "blur", support);
				}
			}
		}
	}

	public List<DesenhoMascaraExameVO> montaDesenhosMascarasExamesResultadoPadrao(final AelResultadosPadrao resultadoPadrao,
			final Integer velSeqp, final Integer solicitacaoExameSeq, final Short itemSolicitacaoExameSeq) throws BaseException {

		List<DesenhoMascaraExameVO> lista = new ArrayList<DesenhoMascaraExameVO>();
		DesenhoMascaraExameVO desenhoMascaraExameVO = new DesenhoMascaraExameVO();

		Map<String, Set<String>> mapaDependentesExpressao = new HashMap<String, Set<String>>();
		Map<String, String> mapaFormulas = new HashMap<String, String>();
		Map<String, Script> mapaScripts = new HashMap<String, Script>();

		AelItemSolicitacaoExames itemSolicitacaoExame = this.mascaraExamesFacade.obterItemSolicitacaoExamePorId(solicitacaoExameSeq,
				itemSolicitacaoExameSeq);

		// obtem a versão do laudo referente ao exame e material de análise.
		AelVersaoLaudo versaoLaudo = this.mascaraExamesFacade.obterVersaoLaudoPorEmaExaSiglaEManSeq(resultadoPadrao
				.getExameMaterialAnalise().getId().getExaSigla(), resultadoPadrao.getExameMaterialAnalise().getId().getManSeq(), velSeqp);
		if (versaoLaudo == null) {
			throw new ApplicationBusinessException(MascaraExamesONExceptionCode.MSG_VERSAO_LAUDO_NE, resultadoPadrao
					.getExameMaterialAnalise().getId().getExaSigla());
		}
		// busca os campos a serem exibidos na máscara.
		List<AelParametroCamposLaudo> camposDaVersaoLaudo = this.mascaraExamesFacade.pesquisarCamposTelaPorVersaoLaudo(versaoLaudo);

		// busca os resultados para o item do exame, se existirem.
		Map<AelParametroCampoLaudoId, AelResultadoExame> resultados = mascaraExamesFacade.obterMapaResultadosPadraoExames(resultadoPadrao);

		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		OutputPanel formularioDinamicoPanel = (OutputPanel) application.createComponent(OutputPanel.COMPONENT_TYPE);

		Integer maiorAltura = 0;
		List<UIComponent> listaComponentes = new ArrayList<UIComponent>(camposDaVersaoLaudo.size());
		Map<Integer, PosicaoTelaVO> mapPosicoesTelaVO = new HashMap<Integer, PosicaoTelaVO>();
		
		int descontoEspacoBranco = 0;
		int acrescimoCamposTexto = 0;
		int acrescimoCamposTextoFinalDiv = 0;
		int ultimaLinha = 0;
		int qtdeCamposLongosLaudoImpressao = 0;
		
		Integer tamanhoLista = camposDaVersaoLaudo.size() - 1;
		Integer ultimoItemLista = 0;
		Integer calculoPosicao = 0;
		Integer tamanhoAdicionalDivPrincipal = 100;
		this.setTipoLaudoMascara(TipoLaudoMascara.LAUDO_RESULTADO_PADRAO);
		for (AelParametroCamposLaudo parametroCampoLaudo : camposDaVersaoLaudo) {
			UIComponent componente = criarComponente(context, parametroCampoLaudo, resultados.get(parametroCampoLaudo.getId()),
					itemSolicitacaoExame, mapaFormulas, mapaDependentesExpressao, desenhoMascaraExameVO, false);

			
			if (componente != null) {
				Integer posicaoFinalLinhaTela = parametroCampoLaudo.posicaoFinalLinhaTela();

				PosicaoTelaVO vo = null;
				if (mapPosicoesTelaVO.containsKey(posicaoFinalLinhaTela)) {
					vo = mapPosicoesTelaVO.get(posicaoFinalLinhaTela);
					mapPosicoesTelaVO.remove(posicaoFinalLinhaTela);
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("-- " + parametroCampoLaudo.getId());
					if (vo != null) {
						LOG.debug(vo);
					}
					LOG.debug("descontoEspacoBranco " + descontoEspacoBranco);
					LOG.debug("acrescimoCamposTexto " + acrescimoCamposTexto);
					LOG.debug("acrescimoCamposTextoFinalDiv " + acrescimoCamposTextoFinalDiv);
					LOG.debug("ultimaLinha " + ultimaLinha);
					LOG.debug("qtdeCamposLongosLaudoImpressao " + qtdeCamposLongosLaudoImpressao);
				}
				StringBuffer estilo = new StringBuffer();
				estilo.append("position: absolute;");

				if (parametroCampoLaudo.getLarguraObjetoVisual() != null) {
					if (parametroCampoLaudo.getLarguraObjetoVisual() > 700) {
						estilo.append(WIDTH_680PX);
					} else {
						estilo.append(WIDTH).append(parametroCampoLaudo.getLarguraObjetoVisual()).append(PX);
					}
				}

				if (parametroCampoLaudo.getAlturaObjetoVisual() != null) {
					estilo.append(HEIGHT).append(parametroCampoLaudo.getAlturaObjetoVisual()).append(PX);
				}

				int altura = parametroCampoLaudo.getPosicaoLinhaTela();
				if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())
						&& (parametroCampoLaudo.getAlturaObjetoVisual() == null || parametroCampoLaudo.getAlturaObjetoVisual() == 0)) {
					parametroCampoLaudo.setAlturaObjetoVisual((short) 100);
					altura += 40;
				}

				if (parametroCampoLaudo.getAlturaObjetoVisual() != null) {
					altura += parametroCampoLaudo.getAlturaObjetoVisual();
				}
				
				if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())) {
					altura += 30; //se for texto logo adicionar 30 que Ã© da toolbar do componente
				}

				if (altura > maiorAltura) {
					maiorAltura = altura;
				}

				if (parametroCampoLaudo.getPosicaoLinhaTela() != null) {
					calculoPosicao = parametroCampoLaudo.getPosicaoLinhaTela().intValue() + acrescimoCamposTexto;
					estilo.append(TOP).append(calculoPosicao).append(PX);
				}
				
				if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())) {
					if(parametroCampoLaudo.getAlturaObjetoVisual() < 100){
						acrescimoCamposTexto += 100 - parametroCampoLaudo.getAlturaObjetoVisual();
					}
				}
				
				if (parametroCampoLaudo.getPosicaoColunaTela() != null) {
					estilo.append(LEFT).append(parametroCampoLaudo.getPosicaoColunaTela()).append(PX);
				}
				
				if(tamanhoLista == ultimoItemLista){
					cadastroResultadoPadraoLaudoController.setAlturaDiv(calculoPosicao + tamanhoAdicionalDivPrincipal);
				}

				UIComponent div = criarDiv(null, estilo.toString());
				div.getChildren().add(componente);

				listaComponentes.add(div);
			}
			ultimoItemLista++;
		}
		
		maiorAltura = maiorAltura - descontoEspacoBranco + acrescimoCamposTexto;

		UIComponent divPrincipal = criarDiv("divPrincipal_" + new Date().getTime(), "position: relative; width:800px; height: "
				+ (maiorAltura + 30) + "px; margin-bottom: 20px;");
		divPrincipal.getChildren().addAll(listaComponentes);
		// criando botao
		formularioDinamicoPanel.getChildren().add(divPrincipal);
		// Adiciona reRender ao componentes que possuem dependentes.
		adicionarAjaxSupport(application, formularioDinamicoPanel, mapaDependentesExpressao, context);
		// cria o mapa de scripts
		criarMapaScripts(formularioDinamicoPanel, mapaFormulas, mapaScripts);

		desenhoMascaraExameVO.setFormularioDinamicoPanel(formularioDinamicoPanel);
		desenhoMascaraExameVO.setMapaDependentesExpressao(mapaDependentesExpressao);
		desenhoMascaraExameVO.setMapaFormulas(mapaFormulas);
		desenhoMascaraExameVO.setMapaScripts(mapaScripts);
		desenhoMascaraExameVO.setItemSolicitacaoExame(itemSolicitacaoExame);

		StringBuffer descricaoExameMaterial = new StringBuffer();
		descricaoExameMaterial.append(versaoLaudo.getExameMaterialAnalise().getAelExames().getSigla())
		.append(_HIFEN_)
		.append(versaoLaudo.getExameMaterialAnalise().getAelExames().getDescricao());

		if (versaoLaudo.getExameMaterialAnalise().getAelMateriaisAnalises() != null) {
			descricaoExameMaterial.append(_HIFEN_)
			.append(versaoLaudo.getExameMaterialAnalise().getAelMateriaisAnalises().getDescricao());
		}
		desenhoMascaraExameVO.setDescricaoExameMaterial(descricaoExameMaterial.toString());
		lista.add(desenhoMascaraExameVO);
		// TODO
		// this.atribuirContextoSessao(VariaveisSessaoEnum.AEL_LISTA_DESENHOS_MASCARAS_EXAMES.toString(),
		// lista);
		return lista;
	}

	/**
	 * Procura recursivamente um componente pelo atributo identificador.
	 * 
	 * @param id
	 * @param lista
	 * @return
	 */
	private UIComponent encontrarComponentePorAtributoIdentificador(String identificador, List<UIComponent> lista) {
		UIComponent retorno = null;
		if (lista != null && !lista.isEmpty()) {
			for (UIComponent filho : lista) {
				if (identificador.equals(filho.getAttributes().get(NOME_ATRIBUTO_IDENTIFICADOR))) {
					retorno = filho;
				} else {
					retorno = this.encontrarComponentePorAtributoIdentificador(identificador, filho.getChildren());
				}
				if (retorno != null) {
					break;
				}
			}
		}
		return retorno;

	}

	private UIComponent criarDiv(final String id, final String style) throws BaseException {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		UIDiv div = (UIDiv) application.createComponent(UIDiv.COMPONENT_TYPE);
		if (id != null) {
			div.getAttributes().put("id", id);
		} else {
			Random randomGenerator = new Random();
			div.getAttributes().put("id", "uiDiv_" + randomGenerator.nextInt(10000)+"_"+ ++unique);
		}
		div.getAttributes().put("style", style);

		return div;
	}

	/**
	 * 
	 * @param versaoLaudo
	 * @param tipoMascaraExame
	 * @return
	 * @throws BaseException
	 */
	private List<DesenhoMascaraExameVO> montarDesenhosMascarasExamesResultadoPadraoVO(AelVersaoLaudo versaoLaudo) throws BaseException {

		List<DesenhoMascaraExameVO> lista = new ArrayList<DesenhoMascaraExameVO>();
		DesenhoMascaraExameVO desenhoMascaraExameVO = new DesenhoMascaraExameVO();
		Map<String, Set<String>> mapaDependentesExpressao = new HashMap<String, Set<String>>();
		Map<String, String> mapaFormulas = new HashMap<String, String>();
		Map<String, Script> mapaScripts = new HashMap<String, Script>();

		// busca os campos a serem exibidos na máscara.
		List<AelParametroCamposLaudo> camposDaVersaoLaudo = null;
		camposDaVersaoLaudo = this.mascaraExamesFacade.pesquisarCamposTelaPorVersaoLaudo(versaoLaudo);

		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		OutputPanel formularioDinamicoPanel = (OutputPanel) application.createComponent(OutputPanel.COMPONENT_TYPE);
		Integer maiorAltura = 0;
		List<UIComponent> listaComponentes = new ArrayList<UIComponent>(camposDaVersaoLaudo.size());
		Integer acrescimoCamposTexto = 0;

		Integer tamanhoLista = camposDaVersaoLaudo.size() - 1;
		Integer ultimoItemLista = 0;
		Integer calculoPosicao = 0;
		Integer tamanhoAdicionalDivPrincipal = 100;
		this.setTipoLaudoMascara(TipoLaudoMascara.LAUDO_RESULTADO_PADRAO);
		for (AelParametroCamposLaudo parametroCampoLaudo : camposDaVersaoLaudo) {
			UIComponent componente = criarComponente(context, parametroCampoLaudo, null, null, mapaFormulas, mapaDependentesExpressao,
					desenhoMascaraExameVO, false);

			if (componente != null) {
				StringBuffer estilo = new StringBuffer();
				estilo.append("position: absolute;");

					int altura = parametroCampoLaudo.getPosicaoLinhaTela();
					if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())
							&& (parametroCampoLaudo.getAlturaObjetoVisual() == null || parametroCampoLaudo.getAlturaObjetoVisual() == 0)) {
						parametroCampoLaudo.setAlturaObjetoVisual((short) 100);
					}
					if (parametroCampoLaudo.getAlturaObjetoVisual() != null) {
						altura += parametroCampoLaudo.getAlturaObjetoVisual();
					}
					
					if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())) {
						altura += 30; //se for texto logo adicionar 30 que Ã© da toolbar do componente
					}
					
					if (altura > maiorAltura) {
						maiorAltura = altura;
					}
									
					if (parametroCampoLaudo.getPosicaoLinhaTela() != null) {
						calculoPosicao = parametroCampoLaudo.getPosicaoLinhaTela().intValue() + acrescimoCamposTexto;
						estilo.append(TOP).append(calculoPosicao).append(PX);
					}
					
					if(tamanhoLista == ultimoItemLista){
						cadastroResultadoPadraoLaudoController.setAlturaDiv(calculoPosicao + tamanhoAdicionalDivPrincipal);
					}
					
					if (DominioObjetoVisual.TEXTO_LONGO.equals(parametroCampoLaudo.getObjetoVisual())) {
						if(parametroCampoLaudo.getAlturaObjetoVisual() < 100){
							acrescimoCamposTexto += 100 - parametroCampoLaudo.getAlturaObjetoVisual();
						}
					}
					
					if (parametroCampoLaudo.getPosicaoColunaTela() != null) {
						estilo.append(LEFT);
						estilo.append(parametroCampoLaudo.getPosicaoColunaTela());
						estilo.append(PX);
					}
				UIComponent div = criarDiv(null, estilo.toString());
				div.getChildren().add(componente);
				listaComponentes.add(div);
			}
			ultimoItemLista++;
		}
		UIComponent divPrincipal = criarDiv("divPrincipal" + 1 + "_" + new Date().getTime(), "position: relative; width:800px; height: "
				+ (maiorAltura) + "px; margin-bottom: 20px;");

		divPrincipal.getChildren().addAll(listaComponentes);
		formularioDinamicoPanel.getChildren().add(divPrincipal); 
		adicionarAjaxSupport(application, formularioDinamicoPanel, mapaDependentesExpressao, context); 
		criarMapaScripts(formularioDinamicoPanel, mapaFormulas, mapaScripts); 
		desenhoMascaraExameVO.setFormularioDinamicoPanel(formularioDinamicoPanel);
		desenhoMascaraExameVO.setMapaDependentesExpressao(mapaDependentesExpressao);
		desenhoMascaraExameVO.setMapaFormulas(mapaFormulas);
		desenhoMascaraExameVO.setMapaScripts(mapaScripts);

		StringBuffer descricaoExameMaterial = new StringBuffer();
		descricaoExameMaterial.append(versaoLaudo.getExameMaterialAnalise().getAelExames().getSigla())
		.append(_HIFEN_)
		.append(versaoLaudo.getExameMaterialAnalise().getAelExames().getDescricao());

		if (versaoLaudo.getExameMaterialAnalise().getAelMateriaisAnalises() != null) {
			descricaoExameMaterial.append(_HIFEN_)
			.append(versaoLaudo.getExameMaterialAnalise().getAelMateriaisAnalises().getDescricao());
		}

		desenhoMascaraExameVO.setDescricaoExameMaterial(descricaoExameMaterial.toString());
		lista.add(desenhoMascaraExameVO);
		// TODO
		// this.atribuirContextoSessao(VariaveisSessaoEnum.AEL_LISTA_DESENHOS_MASCARAS_EXAMES.toString(),
		// lista);
		return lista;
	}

	/**
	 * Cria os componentes JSF de acordo com a instância de
	 * AelParametroCamposLaudo passada por parâmetro.
	 * 
	 * @HIST MascaraExamesHistON.criarComponenteHist
	 * @param context
	 * @param campo
	 * @param itemSolicitacaoExame
	 * @param mapaDependentesExpressao
	 * @param mapaFormulas
	 * @param desenhoMascaraExameVO
	 * @param tipoMascaraExame
	 * @return
	 * @throws BaseException
	 */
	protected UIComponent criarComponente(FacesContext context, AelParametroCamposLaudo campo, Object resultadoObject,
			Object itemSolicitacaoExameObject, Map<String, String> mapaFormulas, Map<String, Set<String>> mapaDependentesExpressao,
			DesenhoMascaraExameVO desenhoMascaraExameVO, boolean isPrevia) throws BaseException {
		UIComponent componente;
		switch (campo.getObjetoVisual()) {
		case TEXTO_FIXO:
			componente = this.criarOutputText(campo, context);
			break;

		case TEXTO_ALFANUMERICO:
			componente = criarInputText(campo, context, resultadoObject, itemSolicitacaoExameObject, mapaFormulas,
					mapaDependentesExpressao, desenhoMascaraExameVO, isPrevia);
			break;

		case TEXTO_NUMERICO_EXPRESSAO:
			componente = criarInputText(campo, context, resultadoObject, itemSolicitacaoExameObject, mapaFormulas,
					mapaDependentesExpressao, desenhoMascaraExameVO, isPrevia);
			break;

		case TEXTO_LONGO:
			componente = this.criarInputTextArea(campo, context, resultadoObject);
			break;

		case TEXTO_CODIFICADO:
			componente = this.criarSelectOneMenu(campo, context, resultadoObject);
			break;

		case EQUIPAMENTO:
			componente = this.criarEquipamento(campo, context, itemSolicitacaoExameObject, isPrevia);
			break;

		case METODO:
			componente = this.criarMetodo(campo, context, itemSolicitacaoExameObject, isPrevia);
			break;

		case RECEBIMENTO:
			componente = this.criarRecebimento(campo, context, itemSolicitacaoExameObject, isPrevia);
			break;

		case HISTORICO:
			componente = this.criarHistorico(campo, context, itemSolicitacaoExameObject, isPrevia);
			break;

		case VALORES_REFERENCIA:
			componente = criarValoresReferencia(campo, context, itemSolicitacaoExameObject, isPrevia);
			break;

		default:
			componente = null;
			break;
		}

		return componente;
	}

	/**
	 * @HIST MascaraExamesHistON.criarComponenteInformacaoRespiracaoHist
	 * @param context
	 * @param itemSolicitacaoExame
	 * @return
	 * @throws BaseException
	 */
	protected UIComponent criarComponenteInformacaoRespiracao(FacesContext context, Object itemSolicitacaoExameObject) throws BaseException {
		DominioFormaRespiracao formaRespiracao = null;
		BigDecimal litrosOxigenio = null;
		Short percOxigenio = null;
		if (itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {
			AelItemSolicitacaoExames item = (AelItemSolicitacaoExames) itemSolicitacaoExameObject;
			formaRespiracao = item.getFormaRespiracao();
			litrosOxigenio = item.getLitrosOxigenio();
			percOxigenio = item.getPercOxigenio();
		} else {
			if (itemSolicitacaoExameObject instanceof AelItemSolicExameHist) {
				AelItemSolicExameHist item = (AelItemSolicExameHist) itemSolicitacaoExameObject;
				formaRespiracao = item.getFormaRespiracao();
				litrosOxigenio = item.getLitrosOxigenio();
				percOxigenio = item.getPercOxigenio();
			}
		}
		HtmlOutputText output = null;

		if (formaRespiracao != null) {

			Application application = context.getApplication();

			output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

			output.setId(TEMP_ + unique++);
			output.setStyle("font-family: Courier New; font-size: 10pt; font-weight: bold;");

			if (formaRespiracao.equals(DominioFormaRespiracao.UM)) {
				output.setValue("Respirando em ar ambiente.");
			} else if (formaRespiracao.equals(DominioFormaRespiracao.DOIS)) {
				output.setValue("Recebendo " + litrosOxigenio.toString() + " litros/minuto de oxigênio.");
			} else if (formaRespiracao.equals(DominioFormaRespiracao.TRES)) {
				output.setValue("Em ventilação mecânica com fração inspirada de oxigênio de " + percOxigenio + "%.");
			}
		}
		return output;
	}

	/**
	 * @HIST MascaraExamesHistON.criarComponenteInformacaoMedicoHist
	 * @param context
	 * @param itemSolicitacaoExame
	 * @param isPrevia
	 * @return
	 * @throws BaseException
	 */
	protected UIComponent criarComponenteInformacaoMedico(FacesContext context, Object itemSolicitacaoExameObject, boolean isPrevia)
			throws BaseException {
		RapServidores servidorResponsabilidade = null;
		AelUnfExecutaExames unfExecutaExame = null;

		if (itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {
			AelItemSolicitacaoExames item = (AelItemSolicitacaoExames) itemSolicitacaoExameObject;
			servidorResponsabilidade = item.getServidorResponsabilidade();

			if (item.getAelUnfExecutaExames() != null) {
				unfExecutaExame = examesFacade.obterAelUnfExecutaExames(item.getAelUnfExecutaExames().getId().getEmaExaSigla(), item
						.getAelUnfExecutaExames().getId().getEmaManSeq(), item.getAelUnfExecutaExames().getId().getUnfSeq().getSeq());
			}

		} else if (itemSolicitacaoExameObject instanceof AelItemSolicExameHist) {
			AelItemSolicExameHist item = (AelItemSolicExameHist) itemSolicitacaoExameObject;
			servidorResponsabilidade = item.getServidorResponsabilidade();

			if (item.getAelUnfExecutaExames() != null) {
				unfExecutaExame = examesFacade.obterAelUnfExecutaExames(item.getAelUnfExecutaExames().getId().getEmaExaSigla(), item
						.getAelUnfExecutaExames().getId().getEmaManSeq(), item.getAelUnfExecutaExames().getId().getUnfSeq().getSeq());
			}
		}

		HtmlOutputText output = null;

		if (isPrevia) {
			Application application = context.getApplication();

			output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

			output.setId(TEMP_ + unique++);
			output.setStyle(FONT_FAMILY_COURIER_NEW_FONT_SIZE_8PT);
			output.setValue("Dr.(a) Testes  - PVA:123456789");

		} else {

			if (servidorResponsabilidade != null) {
				Boolean uniFechada = (unfExecutaExame != null && unfExecutaExame.getUnidadeFuncional() != null && aghuFacade
						.possuiCaracteristicaPorUnidadeEConstante(unfExecutaExame.getUnidadeFuncional().getSeq(),
								ConstanteAghCaractUnidFuncionais.AREA_FECHADA));

				// RapServidores servidor =
				// itemSolicitacaoExame.getServidorResponsabilidade();

				List<ConselhoProfissionalServidorVO> conselhos = registroColaboradorFacade.buscaConselhosProfissionalServidorAtivoInativo(
						servidorResponsabilidade.getId().getMatricula(), servidorResponsabilidade.getId().getVinCodigo());

				if (uniFechada && conselhos.size() > 0) {
					ConselhoProfissionalServidorVO conselho = conselhos.get(0);

					Application application = context.getApplication();

					output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

					output.setId(TEMP_ + unique++);
					output.setStyle(FONT_FAMILY_COURIER_NEW_FONT_SIZE_8PT);
					output.setValue("Dr.(a)" + conselho.getNome() + _HIFEN_ + conselho.getSiglaConselho() + ":"
							+ (conselho.getNumeroRegistroConselho() != null ? conselho.getNumeroRegistroConselho() : ""));
				}
			}
		}

		return output;
	}

	protected UIComponent criarComponenteInformacaoColeta(FacesContext context, String textoComponent) throws BaseException {
		HtmlOutputText output = (HtmlOutputText) context.getApplication().createComponent(HtmlOutputText.COMPONENT_TYPE);

		output.setId(TEMP_ + unique++);
		output.setStyle("font-family: Courier New; font-size: 9pt;");
		output.setValue(textoComponent);

		return output;
	}

	/**
	 * Cria um HtmlInputTextarea a partir de um AelParametroCamposLaudo.
	 * 
	 * @param campo
	 * @param context
	 * @param tipoMascaraExame
	 * @return
	 * @throws BaseException 
	 */
	private UIComponent criarInputTextArea(AelParametroCamposLaudo campo, FacesContext context, Object resultadoObject
			) throws BaseException {
		String descricaoResultadoExame = null;
		if (resultadoObject instanceof AelResultadoExame) {
			descricaoResultadoExame = ((AelResultadoExame)resultadoObject).getDescricao() != null ? ((AelResultadoExame)resultadoObject).getDescricao() : null;
		} else {
			if (resultadoObject instanceof AelResultadosExamesHist) {
				descricaoResultadoExame = ((AelResultadosExamesHist)resultadoObject).getDescricao() != null ? ((AelResultadosExamesHist)resultadoObject).getDescricao() : null;
			}
		}
			Application application = context.getApplication();

			Editor inputTextArea = (Editor) application.createComponent(context, "org.primefaces.component.Editor", "org.primefaces.component.EditorRenderer");
			Editor inputTextAreaGrande = (Editor) application.createComponent(context, "org.primefaces.component.Editor", "org.primefaces.component.EditorRenderer");
			
			UIComponent divPrincipal = criarDiv("subDiv" + this.unique++, 
					campo.getLarguraObjetoVisual() != null ? "position:absolute; width:" +campo.getLarguraObjetoVisual() : "");

			CommandButton commandButtonMaximizar = criarBotaoMaximizar(campo, application);
			CommandButton commandButtonFechar = criarBotaoSalvar(campo, application);
			Dialog modal = criarModal(application, commandButtonFechar, inputTextAreaGrande);
			
			String identificador = campo.getCampoLaudo().getNome()
					+ ((campo.getId() != null && campo.getId().getSeqp() != null) ? campo.getId().getSeqp() : new Date().getTime());
			inputTextArea.getAttributes().put(NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
			inputTextArea.getAttributes().put(NOME_ATRIBUTO_IDENTIFICADOR, identificador);
			
			inputTextArea.setId(campo.getId().toString());
			inputTextAreaGrande.setId(campo.getId().toString() + "Grande");
			
			inputTextArea.setControls("bold italic underline strikethrough alignleft center alignright justify font size color");
			inputTextAreaGrande.setControls("bold italic underline strikethrough alignleft center alignright justify font size color");
			
			if (campo.getLarguraObjetoVisual() != null) {
				inputTextArea.setWidth(campo.getLarguraObjetoVisual().intValue());
			}
			if (campo.getAlturaObjetoVisual() != null) {
				inputTextArea.setHeight(campo.getAlturaObjetoVisual().intValue() < MIN_HEIGHT_TEXTAREA ? MIN_HEIGHT_TEXTAREA : campo.getAlturaObjetoVisual().intValue());
			}
			
			if (resultadoObject != null && descricaoResultadoExame != null) {
				inputTextArea.setValue(CoreUtil.converterRTF2Text(descricaoResultadoExame));
				inputTextAreaGrande.setValue(CoreUtil.converterRTF2Text(descricaoResultadoExame));
			}
			
			inputTextAreaGrande.setWidth(1250);
			inputTextAreaGrande.setHeight(420);
			
			listaEditor.add(inputTextArea);
			listaEditorGrande.add(inputTextAreaGrande);
			contListaEditor++;
			divPrincipal.getChildren().add(inputTextArea);
			divPrincipal.getChildren().add(commandButtonMaximizar);
			divPrincipal.getChildren().add(modal);
			
			return divPrincipal;
	}

	private Dialog criarModal(Application application,
			CommandButton commandButtonFechar, Editor inputTextAreaGrande) {
		Dialog modal = (Dialog) application.createComponent(Dialog.COMPONENT_TYPE);
		modal.getChildren().add(inputTextAreaGrande);
		modal.getChildren().add(commandButtonFechar);
		modal.setId(ID_EDITOR_MODAL+unique);
		modal.setClosable(false);
		modal.setResizable(false);
		modal.setMaximizable(false);
		modal.setDraggable(false);
		modal.setWidgetVar("modalEditorWG"+unique);
		modal.setModal(true);
		modal.setStyleClass(ID_EDITOR_MODAL);

		return modal;
	}

	private CommandButton criarBotaoMaximizar(AelParametroCamposLaudo campo,
			Application application) {
		CommandButton commandButtonMaximizar = (CommandButton) application.createComponent(CommandButton.COMPONENT_TYPE);
		Tooltip tooltipMaximizar = (Tooltip) application.createComponent(Tooltip.COMPONENT_TYPE);
		HtmlOutputText textTooltip = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
		
		textTooltip.setId("textTooltip"+unique);
		textTooltip.setValue("Expandir");
		
		tooltipMaximizar.setId("toolTipBtnMaximizar"+unique);
		tooltipMaximizar.setFor("btnMaximizar"+unique);		
		tooltipMaximizar.getChildren().add(textTooltip);
		tooltipMaximizar.setStyle("width: 50px !important;");
		
		commandButtonMaximizar.getChildren().add(tooltipMaximizar);
		commandButtonMaximizar.setId("btnMaximizar"+unique);
		commandButtonMaximizar.setStyleClass("textArea");
		commandButtonMaximizar.setIcon("aghu-icon-enlarge");
		commandButtonMaximizar.setAjax(true);
		commandButtonMaximizar.setUpdate(ID_EDITOR_MODAL+unique +", "+campo.getId().toString() + "Grande");
		commandButtonMaximizar.setProcess(ID_EDITOR_MODAL+unique +","+campo.getId().toString()+","+ "@this");
		commandButtonMaximizar.setOncomplete("PF('modalEditorWG"+unique+"').show();");
		commandButtonMaximizar.setActionExpression(createMethodExpression(
				   "#{mascaraExamesComponentes.setValorInputTextAreaMaximizar("+contListaEditor+")}",String.class));
		return commandButtonMaximizar;
	}

	private CommandButton criarBotaoSalvar(AelParametroCamposLaudo campo,
			Application application) {
		CommandButton commandButtonSalvar = (CommandButton) application.createComponent(CommandButton.COMPONENT_TYPE);
		commandButtonSalvar.setId("btnFechar"+unique);
		commandButtonSalvar.setValue("Salvar e Voltar");
		commandButtonSalvar.setStyle("margin-top: 5px; ");
		commandButtonSalvar.setUpdate(campo.getId().toString());
		commandButtonSalvar.setProcess(ID_EDITOR_MODAL+unique +","+campo.getId().toString()+","+ "@this");
		commandButtonSalvar.setOnclick("PF('modalEditorWG"+unique+"').hide();");
		commandButtonSalvar.setActionExpression(createMethodExpression(
				   "#{mascaraExamesComponentes.setValorInputTextAreaGravar("+contListaEditor+")}",null));
		return commandButtonSalvar;
	}
	
	public void setValorInputTextAreaGravar(Integer contListaEditor){
		listaEditor.get(contListaEditor).setValue(
				listaEditorGrande.get(contListaEditor).getValue() == null ? ""
						: listaEditorGrande.get(contListaEditor).getValue().toString());
	}
	
	public void setValorInputTextAreaMaximizar(Integer contListaEditor){
		listaEditorGrande.get(contListaEditor).setValue(
				listaEditor.get(contListaEditor).getValue() == null ? ""
						: listaEditor.get(contListaEditor).getValue()
								.toString());
	}
	
	 public static MethodExpression createMethodExpression(String expression, Class<?> returnType, Class<?>... parameterTypes) {
	        FacesContext facesContext = FacesContext.getCurrentInstance();
	        return facesContext.getApplication().getExpressionFactory().createMethodExpression(
	            facesContext.getELContext(), expression, returnType, parameterTypes);
	 }
	
	/**
	 * Cria um HtmlOutputText a partir de um AelParametroCamposLaudo
	 * 
	 * @param campo
	 * @param context
	 * @param itemSolicitacaoExame
	 * @return
	 * @throws BaseException 
	 */
	protected UIComponent criarOutputText(AelParametroCamposLaudo campo, FacesContext context) throws BaseException {
		Application application = context.getApplication();

		UIComponent divPrincipal = criarDiv("subDiv" + this.unique++, "text-align: left;");
		HtmlOutputText output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
		
		AelCampoLaudo campoLaudo = examesFacade.obterAelCampoLaudoId(campo.getCampoLaudo().getSeq());
		String identificador = campoLaudo.getNome() + campo.getId().getSeqp();
		
		String textoLivre = getTextoFixoSemTag(campo.getTextoLivre()).replace("<root>", "").replace("</root>", "");

		if (textoLivre != null) {
			output.setValue(textoLivre);
		}
				
		output.getAttributes().put(NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
		output.getAttributes().put(NOME_ATRIBUTO_IDENTIFICADOR, identificador);

		output.setId(campo.getId().toString());

		output.setStyle(obterStyleParametroCampoLaudo(campo));
		divPrincipal.getChildren().add(output);

		return divPrincipal;
	}
	
	/**
	 * Cria um SelectOneMenu (do PrimeFaces) a partir de um AelParametroCamposLaudo
	 * 
	 * @param campo
	 * @param context
	 * @param resultado
	 * @param itemSolicitacaoExame
	 * @param tipoMascaraExame
	 * @return
	 */
	private UIComponent criarSelectOneMenu(AelParametroCamposLaudo campo, FacesContext context, Object resultadoObject
			) {

		String descricaoResultadoExame = null;
		AelResultadoCodificado aelResultadoCodificado = null;
		AelResultadoCaracteristica aelResultadoCaracteristica = null;
		AelCampoLaudo campoLaudo = examesFacade.obterAelCampoLaudoId(campo.getCampoLaudo().getSeq());

		
		if (resultadoObject instanceof AelResultadoExame) {
			descricaoResultadoExame = ((AelResultadoExame)resultadoObject).getDescricao() != null ? ((AelResultadoExame)resultadoObject).getDescricao() : null;
		} else {
			if (resultadoObject instanceof AelResultadosExamesHist) {
				descricaoResultadoExame = ((AelResultadosExamesHist)resultadoObject).getDescricao() != null ? ((AelResultadosExamesHist)resultadoObject).getDescricao() : null;
			}
		}
		
		
		if (resultadoObject instanceof AelResultadoExame) {
			AelResultadoExame aelResultadoExame = (AelResultadoExame) resultadoObject;
			aelResultadoCodificado = aelResultadoExame.getResultadoCodificado();
			aelResultadoCaracteristica = aelResultadoExame.getResultadoCaracteristica();
		} else {
			if (resultadoObject instanceof AelResultadosExamesHist) {
				AelResultadosExamesHist aelResultadoExame = (AelResultadosExamesHist) resultadoObject;
				aelResultadoCodificado = aelResultadoExame.getResultadoCodificado();
				aelResultadoCaracteristica = aelResultadoExame.getResultadoCaracteristica();
			}
		}

			Application application = context.getApplication();

			HtmlSelectOneMenu selectOneMenu = (HtmlSelectOneMenu) application.createComponent(HtmlSelectOneMenu.COMPONENT_TYPE);

			UISelectItem selectitem = null;

			if (campoLaudo.getGrupoResultadoCodificado() != null) {
				List<AelResultadoCodificado> resultadosCodificados = this.mascaraExamesFacade
						.pesquisarResultadosCodificadosPorCampoLaudo(campo.getCampoLaudo());

				// Descrição do resultado do exame
				String descricaoResultado = null;
				if (resultadoObject != null) {
					descricaoResultado = descricaoResultadoExame;
				}

				for (AelResultadoCodificado resultadoCodificado : resultadosCodificados) {
					selectitem = (UISelectItem) application.createComponent(UISelectItem.COMPONENT_TYPE);
					selectitem.setItemLabel(resultadoCodificado.getDescricao());
					selectitem.setItemValue("RESULTADO_CODIFICADO:gtcSeq=" + resultadoCodificado.getId().getGtcSeq() + ",seqp="
							+ resultadoCodificado.getId().getSeqp());

					selectOneMenu.getChildren().add(selectitem);
					if (resultadoObject != null && aelResultadoCodificado != null
							&& resultadoCodificado.getId().getGtcSeq().equals(aelResultadoCodificado.getId().getGtcSeq())
							&& resultadoCodificado.getId().getSeqp().equals(aelResultadoCodificado.getId().getSeqp())) {
						selectOneMenu.setValue("RESULTADO_CODIFICADO:gtcSeq=" + resultadoCodificado.getId().getGtcSeq() + ",seqp="
								+ resultadoCodificado.getId().getSeqp());
					} else {

						// Descrição do resultado codificado
						String descricaoResultadoCodificado = resultadoCodificado.getDescricao();

						// Verifica se a descrição do resultado do exame é
						// equivalente a do resultado codificado atual
						if (descricaoResultado != null && descricaoResultadoCodificado != null
								&& descricaoResultadoCodificado.trim().equalsIgnoreCase(descricaoResultado.trim())) {
							selectOneMenu.setValue("RESULTADO_CODIFICADO:gtcSeq=" + resultadoCodificado.getId().getGtcSeq() + ",seqp="
									+ resultadoCodificado.getId().getSeqp());
						}
					}

				}
			} else {
				List<AelExameGrupoCaracteristica> exameGrupoCaracteristicaList = this.mascaraExamesFacade
						.pesquisarExameGrupoCarateristicaPorCampo(campo);

				for (AelExameGrupoCaracteristica exameGrupoCaracteristica : exameGrupoCaracteristicaList) {
					selectitem = (UISelectItem) application.createComponent(UISelectItem.COMPONENT_TYPE);
					selectitem.setItemLabel(exameGrupoCaracteristica.getResultadoCaracteristica().getDescricao());
					selectitem.setItemValue("GRUPO_CARACTERISTICA:emaExaSigla=" + exameGrupoCaracteristica.getId().getEmaExaSigla()
							+ ",emaManSeq=" + exameGrupoCaracteristica.getId().getEmaManSeq() + ",cacSeq="
							+ exameGrupoCaracteristica.getId().getCacSeq() + ",gcaSeq=" + exameGrupoCaracteristica.getId().getGcaSeq());

					selectOneMenu.getChildren().add(selectitem);

					if (resultadoObject != null && exameGrupoCaracteristica.getId().getCacSeq().equals(aelResultadoCaracteristica.getSeq())) {
						selectOneMenu.setValue("GRUPO_CARACTERISTICA:emaExaSigla=" + exameGrupoCaracteristica.getId().getEmaExaSigla()
								+ ",emaManSeq=" + exameGrupoCaracteristica.getId().getEmaManSeq() + ",cacSeq="
								+ exameGrupoCaracteristica.getId().getCacSeq() + ",gcaSeq=" + exameGrupoCaracteristica.getId().getGcaSeq());
					}
				}
			}

			selectitem = (UISelectItem) application.createComponent(UISelectItem.COMPONENT_TYPE);
			selectitem.setItemLabel("-- Selecione --");
			selectitem.setItemValue(null);
			selectOneMenu.getChildren().add(selectitem);
			String identificador = campoLaudo.getNome() + campo.getId().getSeqp();

			selectOneMenu.getAttributes().put(NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
			selectOneMenu.getAttributes().put(NOME_ATRIBUTO_IDENTIFICADOR, identificador);
			selectOneMenu.setId(campo.getId().toString());
			selectOneMenu.setLabel(campoLaudo.getNome());

			selectOneMenu.setStyle(obterStyleParametroCampoLaudo(campo));

			return selectOneMenu;
	}

	/**
	 * Exibe os dados de equipamento a partir de um AelParametroCamposLaudo.
	 * 
	 * @HIST MascaraExamesHistON.criarEquipamentoHist
	 * @param campo
	 * @param context
	 * @param resultado
	 * @param itemSolicitacaoExame
	 * @param tipoMascaraExame
	 * @return
	 */
	private UIComponent criarEquipamento(AelParametroCamposLaudo campo, FacesContext context, Object itemSolicitacaoExameObject,
			boolean isPrevia) {

		HtmlOutputText output = null;

		return output;
	}

	/**
	 * Exibe os dados de método a partir de um AelParametroCamposLaudo.
	 * 
	 * @HIST MascaraExamesHistON.criarMetodoHist
	 * @param campo
	 * @param context
	 * @param resultado
	 * @param itemSolicitacaoExame
	 * @param tipoMascaraExame
	 * @return
	 * @throws BaseException
	 */
	private UIComponent criarMetodo(AelParametroCamposLaudo campo, FacesContext context, Object itemSolicitacaoExameObject,
			boolean isPrevia) throws BaseException {

		HtmlOutputText output = null;


		return output;
	}

	/**
	 * Exibe os dados de recebimento a partir de um AelParametroCamposLaudo.
	 * 
	 * @HIST MascaraExamesHistON.criarRecebimentoHist
	 * @param campo
	 * @param context
	 * @param resultado
	 * @param itemSolicitacaoExame
	 * @param tipoMascaraExame
	 * @return
	 * @throws BaseException
	 */
	private UIComponent criarRecebimento(AelParametroCamposLaudo campo, FacesContext context, Object itemSolicitacaoExameObject,
			boolean isPrevia) throws BaseException {

		HtmlOutputText output = null;


		return output;
	}

	/**
	 * Exibe os dados de histórico a partir de um AelParametroCamposLaudo.
	 * 
	 * @HIST MascaraExamesHistON.criarHistoricoHist
	 * @param campo
	 * @param contexto
	 * @param itemSolicitacaoExame
	 * @param tipoMascaraExame
	 * @return
	 * @throws BaseException
	 */
	private UIComponent criarHistorico(AelParametroCamposLaudo campo, FacesContext context, Object itemSolicitacaoExameObject,
			boolean isPrevia) throws BaseException {

		HtmlOutputText output = null;

		return output;
	}

	protected UIComponent criarAssinaturaEletronica(FacesContext context, Object itemSolicitacaoExameObject,
			DesenhoMascaraExameVO desenhoMascaraExameVO) throws BaseException {

		Integer itemSolicSoeSeq = null;
		Short itemSolicSeqpP = null;
		AelUnfExecutaExames unfExecutaExames = null;
		Integer matriculaConselho = null;
		Short vinCodigoConselho = null;

		if (itemSolicitacaoExameObject instanceof AelItemSolicitacaoExames) {

			AelItemSolicitacaoExames itemSolic = examesFacade.obteritemSolicitacaoExamesPorChavePrimaria(
					((AelItemSolicitacaoExames) itemSolicitacaoExameObject).getId(),
					new Enum[] { AelItemSolicitacaoExames.Fields.AEL_UNF_EXECUTA_EXAMES }, null);

			itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
			itemSolicSeqpP = itemSolic.getId().getSeqp();
			unfExecutaExames = itemSolic.getAelUnfExecutaExames();
			AelExtratoItemSolicitacao extratoItemSolic = mascaraExamesFacade.obterUltimoItemSolicitacaoSitCodigo(itemSolicSoeSeq,
					itemSolicSeqpP, DominioSituacaoItemSolicitacaoExame.LI.toString());
			if (extratoItemSolic == null) {
				return null;
			} else {
				if (extratoItemSolic.getServidorEhResponsabilide() != null) {
					matriculaConselho = extratoItemSolic.getServidorEhResponsabilide().getId().getMatricula();
					vinCodigoConselho = extratoItemSolic.getServidorEhResponsabilide().getId().getVinCodigo();
				} else {
					matriculaConselho = extratoItemSolic.getServidor().getId().getMatricula();
					vinCodigoConselho = extratoItemSolic.getServidor().getId().getVinCodigo();
				}
			}

		} else {
			if (itemSolicitacaoExameObject instanceof AelItemSolicExameHist) {

				AelItemSolicExameHist itemSolic = examesFacade.obteritemSolicitacaoExamesHistPorChavePrimaria(
						((AelItemSolicExameHist) itemSolicitacaoExameObject).getId(),
						new Enum[] { AelItemSolicExameHist.Fields.AEL_UNF_EXECUTA_EXAMES }, null);

				itemSolicSoeSeq = itemSolic.getId().getSoeSeq();
				itemSolicSeqpP = itemSolic.getId().getSeqp();
				unfExecutaExames = itemSolic.getAelUnfExecutaExames();
				AelExtratoItemSolicHist extratoItemSolic = mascaraExamesFacade.obterUltimoItemSolicitacaoSitCodigoHist(itemSolicSoeSeq,
						itemSolicSeqpP, DominioSituacaoItemSolicitacaoExame.LI.toString());
				if (extratoItemSolic.getServidorEhResponsabilide() != null) {
					matriculaConselho = extratoItemSolic.getServidorEhResponsabilide().getId().getMatricula();
					vinCodigoConselho = extratoItemSolic.getServidorEhResponsabilide().getId().getVinCodigo();
				} else {
					matriculaConselho = extratoItemSolic.getServidor().getId().getMatricula();
					vinCodigoConselho = extratoItemSolic.getServidor().getId().getVinCodigo();

						if (extratoItemSolic.getServidorEhResponsabilide() != null) {
							matriculaConselho = extratoItemSolic.getServidorEhResponsabilide().getId().getMatricula();
							vinCodigoConselho = extratoItemSolic.getServidorEhResponsabilide().getId().getVinCodigo();
						} else {
							matriculaConselho = extratoItemSolic.getServidor().getId().getMatricula();
							vinCodigoConselho = extratoItemSolic.getServidor().getId().getVinCodigo();
						}
				}
			}
		}
		UIComponent uiComponent = null;
		String assinatura = null;

		boolean uniFechada = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unfExecutaExames.getUnidadeFuncional().getSeq(),
				ConstanteAghCaractUnidFuncionais.AREA_FECHADA);
		boolean chefiaAssEletro = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unfExecutaExames.getUnidadeFuncional().getSeq(),
				ConstanteAghCaractUnidFuncionais.CHEFIA_ASS_ELET);

		if (chefiaAssEletro) {
			if (desenhoMascaraExameVO.getCabecalhoLaudo() != null) {
				if (desenhoMascaraExameVO.getCabecalhoLaudo().getpChefeUnidade() != null) {
					assinatura = desenhoMascaraExameVO.getCabecalhoLaudo().getpChefeUnidade()
							+ _HIFEN_
							+ (desenhoMascaraExameVO.getCabecalhoLaudo().getpConselhoUnid() != null ? desenhoMascaraExameVO
									.getCabecalhoLaudo().getpConselhoUnid() + ": " : "")
							+ (desenhoMascaraExameVO.getCabecalhoLaudo().getpNroConselhoUnid() != null ? desenhoMascaraExameVO
									.getCabecalhoLaudo().getpNroConselhoUnid() : "");
				}
			}
		} else {

			List<ConselhoProfissionalServidorVO> conselhos = null;

			conselhos = registroColaboradorFacade.buscaConselhosProfissionalServidorAtivoInativo(matriculaConselho, vinCodigoConselho);

			if (conselhos.size() > 0) {
				ConselhoProfissionalServidorVO conselho = conselhos.get(0);

				assinatura = conselho.getNome() + _HIFEN_ + conselho.getSiglaConselho()
						+ (conselho.getNumeroRegistroConselho() != null ? " : " + conselho.getNumeroRegistroConselho() : "");
			}
		}

		if (assinatura != null) {
			uiComponent = criarDivAssinaturaEletronica(context, assinatura, uniFechada);
		}

		return uiComponent;

	}

	private UIComponent criarDivAssinaturaEletronica(FacesContext context, String assinatura, boolean uniFechada) throws BaseException {

		UIComponent uiComponent;
		uiComponent = this.criarDiv("assEletro" + unique++, "position: absolute; width:745px; text-align: right;");
		Application application = context.getApplication();
		HtmlOutputText output = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);

		output.setId(TEMP_ + unique++);
		output.setStyle("font-family: Courier New; font-style: italic; font-size: 12px;");
		// if(uniFechada){
		// output.setValue("Dr.(a) " + assinatura);
		// }else{
		// output.setValue(assinatura);
		// }

		if (!uniFechada) {
			output.setValue(assinatura);
		}

		UIComponent subDiv = this.criarDiv(SUB_DIV + unique++, "width:745px; text-align: right;");

		subDiv.getChildren().add(output);
		uiComponent.getChildren().add(subDiv);

		application = context.getApplication();

		if (!uniFechada) {
			HtmlOutputText outputConf = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
			outputConf.setId(TEMP_ + unique++);
			outputConf.setStyle("font-family: Courier New; font-size: 8px;");
			outputConf.setValue("Conferência por Vídeo");
			UIComponent subDivConf = this.criarDiv(SUB_DIV + unique++, "width:745px; text-align: right;");
			subDivConf.getChildren().add(outputConf);
			uiComponent.getChildren().add(subDivConf);
		}

		return uiComponent;
	}

	/**
	 * Cria um HtmlInputText a partir de um AelParametroCamposLaudo.
	 * 
	 * @HIST MascaraExamesHistON.criarInputTextHist
	 * @param campo
	 * @param context
	 * @param itemSolicitacaoExame
	 * @param mapaDependentesExpressao
	 * @param mapaFormulas
	 * @param desenhoMascaraExameVO
	 * @param tipoMascaraExame
	 * @param buscarResultado
	 * @return
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	protected UIComponent criarInputText(AelParametroCamposLaudo campo, FacesContext context, /* AelResultadoExame */Object resultadoObject,
	/* AelItemSolicitacaoExames */Object itemSolicitacaoExameObject, Map<String, String> mapaFormulas,
			Map<String, Set<String>> mapaDependentesExpressao, DesenhoMascaraExameVO desenhoMascaraExameVO,
			boolean isPrevia) {
		String descricaoResultadoExame = null;
		Long valorResultadoExame = null;
		
		if (resultadoObject instanceof AelResultadoExame) {
			descricaoResultadoExame = ((AelResultadoExame)resultadoObject).getDescricao() != null ? ((AelResultadoExame)resultadoObject).getDescricao() : null;
		} else {
			if (resultadoObject instanceof AelResultadosExamesHist) {
				descricaoResultadoExame = ((AelResultadosExamesHist)resultadoObject).getDescricao() != null ? ((AelResultadosExamesHist)resultadoObject).getDescricao() : null;
			}
		}
		
		
		if (resultadoObject instanceof AelResultadoExame) {
			AelResultadoExame resultado = (AelResultadoExame) resultadoObject;
			valorResultadoExame = resultado.getValor();

		} else {
			if (resultadoObject instanceof AelResultadosExamesHist) {
				AelResultadosExamesHist resultado = (AelResultadosExamesHist) resultadoObject;
				valorResultadoExame = resultado.getValor();

			}
		}

			Application application = context.getApplication();
			HtmlInputText input = (HtmlInputText) application.createComponent(HtmlInputText.COMPONENT_TYPE);
			AelCampoLaudo campoLaudo = examesFacade.obterAelCampoLaudoId(campo.getCampoLaudo().getSeq());
			String identificador = campoLaudo.getNome() + campo.getId().getSeqp();

			input.getAttributes().put(NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO, campo);
			input.getAttributes().put(NOME_ATRIBUTO_IDENTIFICADOR, identificador);
			input.setId(campo.getId().toString());
			input.setLabel(campo.getCampoLaudo().getNome());
			input.setAlt(campo.getCampoLaudo().getNome());
			input.setTitle(campo.getCampoLaudo().getNome());

			Short qtdeCasasDecimais = campo.getQuantidadeCasasDecimais();
			Integer somaAdicional = 1;
			if (qtdeCasasDecimais == null) {
				qtdeCasasDecimais = (short) 0;
			}
			
			if(qtdeCasasDecimais == null || qtdeCasasDecimais == 0){
				somaAdicional = 0;
			}
			
			input.setMaxlength(campo.getQuantidadeCaracteres() > 0 ? campo.getQuantidadeCaracteres() + qtdeCasasDecimais + somaAdicional : 999);

			// se for numérico, atribui máscara
			if (campo.getCampoLaudo().getTipoCampo().equals(DominioTipoCampoCampoLaudo.N)) {
				if (qtdeCasasDecimais > 0) {
					int inteiros = campo.getQuantidadeCaracteres() > 0 ? campo.getQuantidadeCaracteres() : 20;
					if (inteiros <= 0) {
						String qtdeCasasDec = StringUtils.leftPad("", qtdeCasasDecimais, '9');
						input.setOnfocus("jQuery(this).mask('0," + qtdeCasasDec + "',{placeholder:''});");
					} else {
						Integer casas = inteiros+1;
						input.setOnfocus("jQuery(this).unpriceFormat();"); 
						input.setOnblur("pos = false; if(this.value > 0){this.value = '-'+this.value ; pos = true;} "
							+ "if(this.value.length > 0 && this.value == 0){  this.value = '--'+this.value ; pos = true; } "
							+ "if (this.value.indexOf('.') != '-1') {this.value = this.value.replace('.',',') } "
							+ "if(',' == this.value.charAt(this.value.length-1) && this.value.length <= "+ casas+ ")"
							+ "{ this.value = this.value+'00'} if (this.value.indexOf(',') == '-1') "
							+ "{ jQuery(this).priceFormat({limit:"
							+ (input.getMaxlength() - 1)
							+ ",centsLimit:"+ qtdeCasasDecimais+ ",centsSeparator: ',', allowNegative: true}); } "
							+ "if(this.value.length == 1) {this.value = '0,0'+this.value} if(pos == true)"
							+ "{ this.value = this.value.replace('-', '')} "
							+ " var value2 = this.value.split(','); var value3 = value2[1]; "
							+ " if(value3.length >=" +qtdeCasasDecimais+"){jQuery(this).priceFormat({limit:"+(input.getMaxlength() - 1)+",centsLimit: "+qtdeCasasDecimais+" ,centsSeparator: ',', allowNegative: true});}");
						
					}
					

					NumberConverter converter = new NumberConverter();
					converter.setMaxFractionDigits(qtdeCasasDecimais.intValue());
					converter.setPattern("#.#");
					converter.setMinFractionDigits(qtdeCasasDecimais.intValue());
					//converter.setMaxIntegerDigits(inteiro);
					input.setConverter(converter);
				}
			}

			
			// Para campos do tipo expressão, constrói um mapa de dependencias
			// e, se necessário, atribui valueBinding para calcular a expressão.
			if (campo.getCampoLaudo().getTipoCampo().equals(DominioTipoCampoCampoLaudo.E)) {
				String formula = comandosToLowerCase(campo.getTextoLivre());
				String[] operandos = StringUtils.substringsBetween(formula, "[", "]");
				boolean dependeCamposMascara = false;

				if (operandos != null) {
					for (String operando : operandos) {
						if (operando.equals("IDADE")) {
							String idade = mascaraExamesFacade.obterIdadePaciente(itemSolicitacaoExameObject) != null ? mascaraExamesFacade
									.obterIdadePaciente(itemSolicitacaoExameObject).toString() : "";
							String valor = isPrevia ? MascaraExamesComponentes.IDADE_PREVIA.toString() : idade;
							formula = StringUtils.replace(formula, "[" + operando + "]", valor);
						} else if (operando.equals("SEXO")) {
							String valor = isPrevia ? MascaraExamesComponentes.SEXO : mascaraExamesFacade
									.obterSexoPaciente(itemSolicitacaoExameObject);
							formula = StringUtils.replace(formula, "[" + operando + "]", valor);
						} else {
							dependeCamposMascara = true;
							Set<String> dependentes = null;
							if (mapaDependentesExpressao.get(operando) == null) {
								dependentes = new HashSet<String>();
								mapaDependentesExpressao.put(operando, dependentes);
							} else {
								dependentes = mapaDependentesExpressao.get(operando);
							}
							dependentes.add(campo.getId().toString());
						}
					}
					mapaFormulas.put(campo.getId().toString(), formula);
				}

				if (!dependeCamposMascara) {
					// se não depende de nenhum outro campo da máscara, avalia
					// logo a expressão e deixa o valor fixo no campo.

					if (possuiOperadores(formula)) {
						StringBuilder formulasb = new StringBuilder(
								"import static br.gov.mec.aghu.core.persistence.dialect.FuncoesOracle.*; ");
						formulasb.append(formula);
						input.setValue(Eval.me(formulasb.toString()));
					} else {
						input.setValue(formula);
					}
				} else {
					// se depende de outros campos da máscara, associa o valor
					ValueExpression valueBinding = null;

					if (isPrevia && tipoLaudoMascara.equals(TipoLaudoMascara.LAUDO_PREVIA.toString())) {
						valueBinding = application.getExpressionFactory().createValueExpression(context.getELContext(),
								"#{manterMascaraExamesPreviaController.calcularValor('" + campo.getId().toString() + "')}",
								String.class);
					} else if(!isPrevia && tipoLaudoMascara.equals(TipoLaudoMascara.LAUDO_RESULTADO_NOTA_ADICIONAL.toString())) {
						valueBinding = application.getExpressionFactory().createValueExpression(context.getELContext(),
								"#{cadastroResultadosNotaAdicionalController.calcularValor('" + campo.getId().toString() + "')}", String.class);
					} else if(!isPrevia && tipoLaudoMascara.equals(TipoLaudoMascara.LAUDO_RESULTADO_PADRAO.toString())){
						valueBinding = application.getExpressionFactory().createValueExpression(context.getELContext(),
								"#{cadastroResultadoPadraoLaudoController.calcularValor('" + campo.getId().toString() + "')}", String.class);
					}
					
					input.setValueExpression("value", valueBinding);
				}
				// Apenas readOnly continua permitindo o foco com TAB.
				input.setDisabled(true);
				input.setReadonly(true);
			} else {
				if (resultadoObject != null && valorResultadoExame != null) {
					if (campo.getCampoLaudo().getTipoCampo().equals(DominioTipoCampoCampoLaudo.N)) {
						if (qtdeCasasDecimais > 0 && valorResultadoExame.toString().length() >= 1) {
							String aux = null;
							boolean negativo = false;
							if(valorResultadoExame < 0){
								aux = valorResultadoExame.toString();
								aux = StringUtils.replace(aux, "-", "");
								negativo = true;
							}else{
								aux = valorResultadoExame.toString();
							}
							aux = StringUtils.leftPad(aux, qtdeCasasDecimais + 1, "0");
							StringBuffer valor = new StringBuffer(aux);
							valor.insert(valor.length() - qtdeCasasDecimais, ".");
							StringBuffer valorReal = valor;
							if(negativo){
								String neg = "-";
								valorReal = new StringBuffer();
								valorReal.append(neg).append(valor.toString());
							}
							input.setValue(Double.valueOf(valorReal.toString()));
						} else {
							StringBuffer valor = new StringBuffer(valorResultadoExame.toString());
							input.setValue(Integer.parseInt(valor.toString()));
						}
					} else if (!campo.getCampoLaudo().getTipoCampo().equals(DominioTipoCampoCampoLaudo.N)
							&& !campo.getCampoLaudo().getTipoCampo().equals(DominioTipoCampoCampoLaudo.E)) {
						if (descricaoResultadoExame != null) {
							input.setValue(CoreUtil.converterRTF2Text(descricaoResultadoExame));
						}
					}
				} else if (resultadoObject != null && campo.getCampoLaudo().getTipoCampo().equals(DominioTipoCampoCampoLaudo.A)
						&& descricaoResultadoExame != null) {
					input.setValue(CoreUtil.converterRTF2Text(descricaoResultadoExame));
				}
			}

			// atribui validators de acordo com normalidade
			/*
			 * List<DoubleRangeValidator> validators =
			 * obterListaValidatorsValoresNormalidade(campo,
			 * itemSolicitacaoExame); if(validators != null &&
			 * !validators.isEmpty()){ for (DoubleRangeValidator
			 * doubleRangeValidator : validators) {
			 * doubleRangeValidator.setTransient(!isPrevia);
			 * input.addValidator(doubleRangeValidator); } }
			 */
			input.setStyle(obterStyleParametroCampoLaudo(campo));
			return input;
	}

	/**
	 * Exibe os dados de valores referência a partir de um
	 * AelParametroCamposLaudo.
	 * 
	 * @HIST MascaraExamesHistON.criarValoresReferenciaHist
	 * @param campo
	 * @param context
	 * @param itemSolicitacaoExame
	 * @param tipoMascaraExame
	 * @return
	 * @throws BaseException
	 */
	protected UIComponent criarValoresReferencia(AelParametroCamposLaudo campo, FacesContext context, Object itemSolicitacaoExameObject,
			boolean isPrevia) throws BaseException {

		HtmlOutputText output = null;

		return output;
	}

	private String converteCorDelphiToWeb(String clColor) {
		String corWeb = "";

		switch(clColor) {
			case "clBlack":
				corWeb = "#000000";
				break;
			case "clWindowText":
				corWeb = "#000000";
				break;
			case "clFuchsia":
				corWeb = "#ff00ff";
				break;
			case "clWhite":
				corWeb = "#ffffff";
				break;
			case "clRed":
				corWeb = "#ff0000";
				break;
			case "clGreen":
				corWeb = "#00ff00";
				break;
			default:
				corWeb = "#000000";
				break;
		}
		return corWeb;
	}
	
	public String obterStyleParametroCampoLaudo(AelParametroCamposLaudo campo) {
		StringBuffer style = new StringBuffer(311);

		if (Boolean.TRUE.equals(campo.getNegrito())) {
			style.append(" font-weight: bold !important; ");
		}

		if (Boolean.TRUE.equals(campo.getItalico())) {
			style.append(" font-style: italic !important; ");
		}

		/* define o underline e outros */
		defineTracos(style, campo);

		if (campo.getTamanhoFonte().shortValue() == 0) {
			campo.setTamanhoFonte((short) 12);
		}

		// Seta a fonte do texto
		style.append(" font-family: ").append(campo.getFonte()).append(", monospace !important; ").append(" font-size: ")
				.append(campo.getTamanhoFonte()).append("pt; ");

		if (campo.getLarguraObjetoVisual() != null) {
			if (campo.getLarguraObjetoVisual() > 700) {
				style.append(WIDTH_680PX);
			} else {
				style.append(WIDTH).append(campo.getLarguraObjetoVisual()).append(PX);
			}
		}

		if (campo.getAlturaObjetoVisual() != null) {
			style.append(HEIGHT).append(campo.getAlturaObjetoVisual()).append(PX);
		}

		if (campo.getCor() != null) {
			if (campo.getCor().substring(0, 2).equalsIgnoreCase("cl")) {
				style.append(" color: ").append(converteCorDelphiToWeb(campo.getCor())).append(" !important;");
			} else {
				style.append(" color: ").append(campo.getCor()).append(" !important;");
			}
		}

		if (DominioObjetoVisual.TEXTO_ALFANUMERICO.equals(campo.getObjetoVisual()) || DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(campo.getObjetoVisual())) {
			style.append(" padding: 0px; margin: 0px; border: 1px solid gray; box-sizing: border-box; border-radius: 3px; "); //precisa sobrescrever o estigo do aghuse senao sobrescreve as unidades/labels
		}
		else if (DominioObjetoVisual.TEXTO_CODIFICADO.equals(campo.getObjetoVisual())) {
			style.append(" padding: 0px!important; margin: 0px!important; ");
		}

		if (campo.getQuantidadeCaracteres() != null) {
			style.append(" maxlength_(" + this.getMaxlength(campo) + ");");
		}

		return style.toString().trim();
	}

	private Integer getMaxlength(AelParametroCamposLaudo campo) {
		// +1 da vírgula
		return (campo.getQuantidadeCasasDecimais() != null && campo.getQuantidadeCasasDecimais() > 0) ? (campo.getQuantidadeCaracteres()
				+ campo.getQuantidadeCasasDecimais() + 1) : campo.getQuantidadeCaracteres();
	}

	private void defineTracos(StringBuffer style, AelParametroCamposLaudo campo) {
		if (Boolean.TRUE.equals(campo.getSublinhado()) && Boolean.FALSE.equals(campo.getRiscado())) {
			style.append(" text-decoration: underline !important; ");
		} else if (Boolean.FALSE.equals(campo.getSublinhado()) && Boolean.TRUE.equals(campo.getRiscado())) {
			style.append(" text-decoration: line-through !important; ");
		} else if (Boolean.TRUE.equals(campo.getSublinhado()) && Boolean.TRUE.equals(campo.getRiscado())) {
			style.append(" text-decoration: underline line-through !important; ");
		}
	}

	public String getTextoFixoSemTag(String textoLivre) {
		return StringUtils.isBlank(textoLivre) ? "" : StringEscapeUtils.unescapeHtml4(retornaParametroCampoLaudoTextoLivreSemTag(textoLivre));
	}

	/**
	 * Retorna o texto livre sem as tags html de formatação e etc
	 * 
	 * @throws Exception
	 * */
	public String retornaParametroCampoLaudoTextoLivreSemTag(String textoLivre) {
		StringBuffer retorno = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			if (textoLivre != null) {
				textoLivre = "<root>" + CoreUtil.converterRTF2Text(textoLivre.trim()) + "</root>";
				textoLivre = textoLivre.replaceAll("&", "#");

				ByteArrayInputStream encXML = new ByteArrayInputStream(textoLivre.getBytes("UTF-8"));

				Document docTextoLivre = db.parse(encXML);

				/* filhos */
				NodeList fontElement = docTextoLivre.getChildNodes();
				retorno = new StringBuffer();

				for (int i = 0; i < fontElement.getLength(); i++) {
					Element element = (Element) fontElement.item(0);
					if (element.getTextContent() != null) {
						retorno.append(element.getTextContent());
					}
				}
			}

		} catch (Exception e) {
			return textoLivre;
		}

		String string = (retorno != null) ? retorno.toString().replaceAll("#", "&") : null;
		
		return string;
	}

	private boolean possuiOperadores(final String formula) {
		if (formula != null) {
			String[] operadores = { "POWER", "SQRT", "DECODE", "GREATEST", "SIGN", "+", "-", "*", "/" };
			for (String operador : operadores) {
				if (formula.toUpperCase().contains(operador)) {
					return true;
				}
			}
		}
		return false;
	}

	private String comandosToLowerCase(String formula) {
		if (formula != null) {
			return formula.replace("POWER", "power").replace("SQRT", "sqrt").replace("DECODE", "decode").replace("GREATEST", "greatest")
					.replace("SIGN", "sign");
		}
		return null;
	}

	public List<DesenhoMascaraExameVO> buscaDesenhosMascarasExamesVO(Integer solicitacaoExameSeq, Short itemSolicitacaoExameSeq,
			Integer velSeqp, Boolean isHist) throws BaseException {
		return montaDesenhosMascarasExamesVO(solicitacaoExameSeq, itemSolicitacaoExameSeq,
				DominioSubTipoImpressaoLaudo.LAUDO_GERAL, velSeqp, isHist, true, null);
	}

	public List<DesenhoMascaraExameVO> buscarDesenhosMascarasExamesResultadoPadraoVO(AelVersaoLaudo versaoLaudo) throws BaseException {
		return this.montarDesenhosMascarasExamesResultadoPadraoVO(versaoLaudo);
	}

	public List<DoubleRangeValidator> obterListaValidatorsValoresNormalidade(AelParametroCamposLaudo parametroCampoLaudo,
			AelItemSolicitacaoExames itemSolicitacaoExame) throws ApplicationBusinessException {
		List<DoubleRangeValidator> listaValidators = new ArrayList<DoubleRangeValidator>();
		// atribui validators de acordo com normalidade

		AelCampoLaudo campoLaudo = examesFacade.obterCampoLaudoPorId(parametroCampoLaudo.getCampoLaudo().getSeq(), true,
				AelCampoLaudo.Fields.VALORES_NORMALIDADE);

		for (AelValorNormalidCampo valorNormalidade : campoLaudo.getValoresNormalidade()) {
			if (itemSolicitacaoExame != null && this.valorNormalidadeAplicavel(valorNormalidade, itemSolicitacaoExame)) {
				Double valorMaximo = null;
				Double valorMinimo = null;
				if (valorNormalidade.getQtdeCasasDecimais() != null && valorNormalidade.getQtdeCasasDecimais() > 0) {
					if (!valorNormalidade.getValorMaximo().isEmpty()) {
						StringBuffer sbMaximo = new StringBuffer(valorNormalidade.getValorMaximo());
						if(sbMaximo.length() - valorNormalidade.getQtdeCasasDecimais() >= 0) {
							sbMaximo.insert(sbMaximo.length() - valorNormalidade.getQtdeCasasDecimais(), ".");
						}
						valorMaximo = Double.valueOf(sbMaximo.toString());
					}

					if (!valorNormalidade.getValorMinimo().isEmpty()) {
						StringBuffer sbMinimo = new StringBuffer(valorNormalidade.getValorMinimo());
						if(sbMinimo.length() - valorNormalidade.getQtdeCasasDecimais() >= 0) {
							sbMinimo.insert(sbMinimo.length() - valorNormalidade.getQtdeCasasDecimais(), ".");
						}
						valorMinimo = Double.valueOf(sbMinimo.toString());
					}

				} else {
					try {
						if (!valorNormalidade.getValorMaximo().isEmpty()) {
							valorMaximo = Double.valueOf(valorNormalidade.getValorMaximo());
						}
						if (!valorNormalidade.getValorMinimo().isEmpty()) {
							valorMinimo = Double.valueOf(valorNormalidade.getValorMinimo());
						}
					} catch (Exception e) {
						throw new ApplicationBusinessException(MascaraExamesONExceptionCode.MSG_EXCESSAO_VALOR_NORMAL_INVALIDO);
					}
				}
				if (valorMaximo != null || valorMinimo != null) {
					DoubleRangeValidator validator = new DoubleRangeValidator();
					if (!valorNormalidade.getValorMaximo().isEmpty()) {
						validator.setMaximum(valorMaximo);
					}
					if (!valorNormalidade.getValorMinimo().isEmpty()) {
						validator.setMinimum(valorMinimo);
					}
					listaValidators.add(validator);
				}
			}
		}
		return listaValidators;
	}

	/**
	 * 
	 * Verifica se dada regra de normalidade é aplicável ao paciente referente
	 * ao exame em questão.
	 * 
	 * @HIST MascaraExamesHistON.valorNormalidadeAplicavelHist
	 * @param valorNormalidade
	 * @param itemSolicitacaoExame
	 * @return
	 */
	private boolean valorNormalidadeAplicavel(AelValorNormalidCampo valorNormalidade, AelItemSolicitacaoExames itemSolicitacaoExame) {
		boolean retorno = true;

		if (valorNormalidade.getSituacao() == DominioSituacao.I) {
			retorno = false;
		}

		// Se a idade for específicada, a idade do paciente deve estar no range.
		int idadePaciente = mascaraExamesFacade.obterIdadePaciente(itemSolicitacaoExame);
		boolean idadeMinima = valorNormalidade.getIdadeMinima() != null && valorNormalidade.getIdadeMinima() >= idadePaciente;
		boolean idadeMaxima = valorNormalidade.getIdadeMaxima() != null && valorNormalidade.getIdadeMaxima() <= idadePaciente;

		if (idadeMinima || idadeMaxima) {
			retorno = false;

		}
		if (valorNormalidade.getSexo() != null
				&& !valorNormalidade.getSexo().toString().equals(mascaraExamesFacade.obterSexoPaciente(itemSolicitacaoExame))) {
			retorno = false;
		}

		return retorno;
	}

	/**
	 * Metodo responsável por montar a prévia da mascara de exames
	 * 
	 * @param parametrosPrevia
	 * @param tipoMascaraExame
	 * 
	 */
	public DesenhoMascaraExameVO buscaPreviaMascarasExamesVO(List<AelParametroCamposLaudo> parametrosPrevia, AelVersaoLaudo versaoLaudo,
			NumeroApTipoVO numeroApTipoVO) throws BaseException {
		
		List<AelParametroCamposLaudo> parametrosDistintosPrevia = new ArrayList<AelParametroCamposLaudo>();

		/* Seleciona para mostrar somente os que serão exibidos no relatório pdf */
		for (AelParametroCamposLaudo aelParamCampLa : parametrosPrevia) {
			if (aelParamCampLa.getExibicao().equals(
					DominioExibicaoParametroCamposLaudo.A)
					|| aelParamCampLa.getExibicao().equals(
							DominioExibicaoParametroCamposLaudo.T)) {
				/* Valida se o Campo Laudo de cada campo foi informado */
				cadastrosApoioExamesFacade.validaCampoLaudo(aelParamCampLa);

				aelParamCampLa.setCampoLaudo(examesFacade.obterCampoLaudoPorSeq(aelParamCampLa.getCampoLaudo().getSeq()));
				parametrosDistintosPrevia.add(aelParamCampLa);
			}
		}

			Collections.sort(parametrosDistintosPrevia, new Comparator<AelParametroCamposLaudo>() {
				@Override
				public int compare(AelParametroCamposLaudo item1, AelParametroCamposLaudo item2) {
					return item1.getPosicaoLinhaTela().compareTo(item2.getPosicaoLinhaTela());
				}
			});

		return montaPreviaMascarasExamesVO(parametrosDistintosPrevia, versaoLaudo, numeroApTipoVO);
	}
	
	public void setTipoLaudoMascara(TipoLaudoMascara tipoLaudoMascara){
		this.tipoLaudoMascara = tipoLaudoMascara.toString();
	}

	class PosicaoTelaVO {

		private Integer posicaoTela;

		private Integer diferencaCampos;

		public PosicaoTelaVO(Integer posicaoTela, Integer diferencaCampos) {
			this.posicaoTela = posicaoTela;
			this.diferencaCampos = diferencaCampos;
		}

		public Integer getPosicaoTela() {
			return posicaoTela;
		}

		public void setPosicaoTela(Integer posicaoTela) {
			this.posicaoTela = posicaoTela;
		}

		public Integer getDiferencaCampos() {
			return diferencaCampos;
		}

		public void setDiferencaCampos(Integer diferencaCampos) {
			this.diferencaCampos = diferencaCampos;
		}

		@Override
		public String toString() {
			return "PosicaoTelaVO [" + posicaoTela + ", " + diferencaCampos + "]";
		}

	}

}