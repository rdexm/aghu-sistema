package br.gov.mec.aghu.exames.action;

import groovy.lang.MissingPropertyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.Application;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.jsoup.Jsoup;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.xml.sax.SAXException;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.dominio.DominioExibicaoParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioOpcoesFormulaParametroCamposLaudo;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.laudos.ExamesListaVO;
import br.gov.mec.aghu.exames.vo.CampoLaudoUtilizadoDesenhoMascaraVO;
import br.gov.mec.aghu.exames.vo.DesenhoMascaraExameVO;
import br.gov.mec.aghu.exames.vo.VariaveisValorFormulaMascaraExameVO;
import br.gov.mec.aghu.model.AelCampoCodifRelacionado;
import br.gov.mec.aghu.model.AelCampoCodifRelacionadoId;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelCampoLaudoRelacionado;
import br.gov.mec.aghu.model.AelCampoLaudoRelacionadoId;
import br.gov.mec.aghu.model.AelCampoVinculado;
import br.gov.mec.aghu.model.AelCampoVinculadoId;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.AelVersaoLaudoId;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
public class ManterMascaraExamesController extends ActionController {
	private static final String PX = "px;";
	private static final long serialVersionUID = 2971515088024436215L;
	private static final String PAGE_PREVIA_FORM = "exames-previaForm";
	private static final String PAGE_PREVIA_PDF = "exames-previaPdf";
	private static final String PAGE_NOVO_RESULTADO_PADRAO_LAUDO = "exames-cadastroResultadoPadraoLaudo";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@Inject
	private ManterMascaraExamesPreviaController manterMascaraExamesPreviaController;

	@Inject
	private CalculaValorMascaraExamesComponente calculaValorMascaraExamesComponente;

	@Inject
	private MascaraExamesComponentes mascaraExamesComponentes;

	@Inject
	private ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController;
	
	@Inject
	private SecurityController securityController;
	
	private AelParametroCamposLaudo parametroSelect;
	private Map<String, AelParametroCamposLaudo> parametros;
	List<AelParametroCamposLaudo> parametrosExcluidos;
	private OutputPanel panelFrameComponent;
	private String frameCache = "";
	private Integer counterObjs;
	private String componentId;
	private String componentType;
	private String componentPosY;
	private String componentPosX;
	private AelVersaoLaudo versaoLaudo;
	private String voltarPara;
	private Boolean emEdicao;
	private Boolean emCriacao;
	private String emaExaSigla;
	private Integer emaManSeq;
	private Integer seqp;
	private boolean pendenciaGravacao;
	private boolean exibirPdf = true;
	private List<CampoLaudoUtilizadoDesenhoMascaraVO> listaCamposLaudoUtilizadosDesenhoMascara = null;
	private List<AelResultadosPadrao> listaResultadoPadrao;
	
	public enum DominioTipoRelacionamento {
		C, N;
	}

	private DominioTipoRelacionamento tipoRelacionamento = null;
	private String tipoRelac = null;
	private String resultadoValidacaoFormula;
	private List<VariaveisValorFormulaMascaraExameVO> variaveisFormula;
	private String textoCampo;
	private String styleCampo;
	private boolean voltouDesenhoNovaVersaoMascara;
	private String novaVersaoExaSigla;
	private Integer novaVersaoManSeq;
	private Short novaVersaoUnfSeq;
	private DominioSituacaoVersaoLaudo novaVersaoSituacao;
	private String novaVersaoNomeDesenho;
	private Integer novaVersaoPesquisaPaginada;
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
		
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		emCriacao = false;

		Application application = FacesContext.getCurrentInstance().getApplication();
		this.panelFrameComponent = (OutputPanel) application.createComponent(OutputPanel.COMPONENT_TYPE);
		this.panelFrameComponent.setStyle("top:-10px;left:-10px;width:100%;height:3500px;"); // Seta dimensões do painel de desenho

	}

	public String inicio() {
		tipoRelac = null;
		tipoRelacionamento = null;
		panelFrameComponent.getChildren().clear();
		try {
			if (Boolean.TRUE.equals(this.emEdicao) && this.versaoLaudo == null) {
				AelVersaoLaudoId idExclusao = new AelVersaoLaudoId();
				idExclusao.setEmaExaSigla(this.emaExaSigla);
				idExclusao.setEmaManSeq(this.emaManSeq);
				idExclusao.setSeqp(this.seqp);
				this.versaoLaudo = examesFacade.obterVersaoLaudoPorChavePrimaria(idExclusao);
			}
			if (emCriacao) {
				versaoLaudo = this.examesFacade.criarNovaVersaoLaudo(versaoLaudo);
			}
			counterObjs = 0;
			listaResultadoPadrao = new ArrayList<AelResultadosPadrao>();
			parametros = new HashMap<String, AelParametroCamposLaudo>();
			parametrosExcluidos = new ArrayList<AelParametroCamposLaudo>();
			desSelecionarParametros();

			// --[CARREGA OS OBJETOS PARA TELA]
			if (versaoLaudo != null && versaoLaudo.getId() != null) {
				List<AelParametroCamposLaudo> loadList = this.examesFacade.pesquisarCamposTelaEdicaoMascaraPorVersaoLaudo(versaoLaudo);
				carregaParametros(loadList);
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return voltarPara;
		}
		return null;
	
	}

	public void selecionarInserirParametro() {
		selecionarInserirParametro(true);
	}

	public void atualizarElementoSelecionado() {
		selecionarInserirParametro(false);
	}

	public void onClose(CloseEvent event) {
		if(parametroSelect.getId() == null) {
			RequestContext.getCurrentInstance().execute("mex.contextEvent('delete');");
		}
	}
	
	// --[SINCRONIZA OS COMPONENTES DA TELA COM A CONTROLLER]
	public void selecionarInserirParametro(Boolean converterCss) {
		if (parametros.containsKey(componentId)) {
			parametroSelect = parametros.get(componentId);
			if(parametroSelect.getId() != null) {
				if(Boolean.TRUE.equals(converterCss)) {
					converterParametrosEmCss(parametroSelect);
				} else {
					if(StringUtils.isNotBlank(parametroSelect.getTextoLivre())) {
						parametroSelect.setTextoLivre(Jsoup.parse(parametroSelect.getTextoLivre()).text());
					}				
				}
				// Ao editar uma fórmula, o parâmetro está perdendo os valores abaixo
				if (parametroSelect.getAlturaObjetoVisual() == null && parametroSelect.getLarguraObjetoVisual() == null) {
					this.cadastrosApoioExamesFacade.recarregaValoresPerdidos(parametroSelect);
				}
			}
		} else {
			parametroSelect = this.cadastrosApoioExamesFacade.inserirNovoCampoTela(versaoLaudo, DominioObjetoVisual.valueOf(componentType));
			parametros.put(componentId, parametroSelect);

			if (isInputGroup()) {
				parametroSelect.setExibicao(DominioExibicaoParametroCamposLaudo.A);
			}
			if (isControlGroup() || DominioObjetoVisual.VALORES_REFERENCIA.equals(parametroSelect.getObjetoVisual())) {
				parametroSelect.setExibicao(DominioExibicaoParametroCamposLaudo.R);
				parametroSelect.setQuantidadeCaracteres(0);
			}
		}
		if (tipoRelac == null || tipoRelac.isEmpty()) {
			tipoRelacionamento = null;
		} else {
			if (DominioTipoRelacionamento.C.toString().equals(tipoRelac)) {
				tipoRelacionamento = DominioTipoRelacionamento.C;
			} else if (DominioTipoRelacionamento.N.toString().equals(tipoRelac)) {
				tipoRelacionamento = DominioTipoRelacionamento.N;
			}
		}
		
		validarPosicaoParametro();

		if (componentPosX.indexOf('.') > -1) {
			componentPosX = componentPosX.substring(0, componentPosX.indexOf('.'));
		}
		
		parametroSelect.setPosicaoColunaTela(Short.valueOf(componentPosX.replace("px", "")));

		if (componentPosY.indexOf('.') > -1) {
			componentPosY = componentPosY.substring(0, componentPosY.indexOf('.'));
		}
		

		parametroSelect.setPosicaoLinhaTela(Integer.valueOf(componentPosY.replace("px", "")));
		parametroSelect.setPosicaoColunaImpressao(parametroSelect.getPosicaoColunaTela());
		parametroSelect.setPosicaoLinhaImpressao(parametroSelect.getPosicaoLinhaTela());
		
		executarEventosJS(converterCss);
		
	}
	
	/*
	 * Caso a posição de linha e/ou coluna do parametro esteja com valor negativo inicializa-se esta posição com valor ZERO.
	 */
	private void validarPosicaoParametro(){
		if (parametroSelect.getPosicaoColunaTela() != null && parametroSelect.getPosicaoColunaTela() < 0){
			parametroSelect.setPosicaoColunaTela(Short.valueOf("0"));
		}	
		
		if (parametroSelect.getPosicaoLinhaTela() != null && parametroSelect.getPosicaoLinhaTela() < 0){
			parametroSelect.setPosicaoLinhaTela(0);
		}
		
		try{
			Integer posicaoY = Integer.parseInt(componentPosY);
			Integer posicaoX = Integer.parseInt(componentPosX);
			
			if(posicaoX < 0){
				posicaoX = 0;
				componentPosX = posicaoX.toString();
			}
			
			if(posicaoY < 0){
				posicaoY = 0;
				componentPosY = posicaoY.toString();
			}
			
		}catch(NumberFormatException e){
			apresentarMsgNegocio(Severity.ERROR, "ERRO_FORMATAR_POSICAO_XY");
		}
		
	}
	
	private void executarEventosJS(Boolean converterCss) {
		// O parâmetro converterCss só vai ser true quando a tela de propriedades for aberta.
		if (this.isEditorRenderizado() && Boolean.TRUE.equals(converterCss)) {
			RequestContext.getCurrentInstance().execute("if(PrimeFaces.widgets['editorWG']) { PF('editorWG').focus(); }");
		}
		
		if(parametroSelect.getId() != null) {
			RequestContext.getCurrentInstance().execute("exibirChaveRegistroSelecionado("+parametroSelect.getId().getCalSeq()+","+
					parametroSelect.getId().getSeqp()+",'"+parametroSelect.getCampoLaudo().getNome()+"');");
		}
	}

	// --[DESVINCULA O OBJETO DA TELA COM A CONTROLLER ]
	public void desSelecionarParametros() {
		parametroSelect = null;
		componentId = null;
	}

	// --[INSERE OBJETOS DE PARAMETRO NA TELA]
	public void carregaParametros(List<AelParametroCamposLaudo> params) {
		for (AelParametroCamposLaudo p : params) {
			parametros.put("obj" + counterObjs, p);
			OutputPanel base = componenteBase(p.getPosicaoLinhaTela(), p.getPosicaoColunaTela(), counterObjs + 2);
			titulaComponente(base, p);
			base.setId("obj" + counterObjs);
			getPanelFrameComponent().getChildren().add(base);
			counterObjs++;
		}
		
		// Caso nenhum componente tenha sido adicionado a mascara
				if (counterObjs < 1){
					OutputPanel base = componenteBase(10, 10, counterObjs + 2);
					base.setId("obj" + counterObjs);
					getPanelFrameComponent().getChildren().add(base);
					counterObjs++;
				}
		
		frameCache = "";
	}

	private void converterParametrosEmCss(AelParametroCamposLaudo param) {
		final String BEGIN_STYLE = "<SPAN STYLE='";
		final String CLOSE_STYLE = "'>";
		final String END = "</SPAN>";
		StringBuffer tag = new StringBuffer(500); 

		if(StringUtils.isNotBlank(param.getTextoLivre())) {
			param.setTextoLivre(Jsoup.parse(param.getTextoLivre()).text());
			
			tag.append(BEGIN_STYLE);
			if(param.getNegrito() != null && param.getNegrito()) {
				tag.append("font-weight: bold;");
			}
			if(param.getSublinhado() != null && param.getSublinhado()) {
				tag.append("text-decoration: underline;");
			}
			if(param.getRiscado() != null && param.getRiscado()) {
				tag.append("text-decoration: line-through;");
			}
			if(param.getItalico() != null && param.getItalico()) {
				tag.append("font-style: italic;");
			}
			tag.append("font-family: " + param.getFonte() + "; font-size:" + param.getTamanhoFonte() + ";");
			tag.append(CLOSE_STYLE);
			tag.append(param.getTextoLivre());
			tag.append(END);
			param.setTextoLivre("<DIV STYLE='text-align:"+param.getAlinhamento().getDescricao()+";'>" + tag.toString() + "</DIV>");
		}
	}
	
	// --[EXCLUI OBJETOS DE PARAMETRO]
	public void excluirParametro() {
		Boolean exibirMsg = false;
		if (parametros.containsKey(componentId)) {
			try {
				parametroSelect = parametros.get(componentId);
				if (parametroSelect.getId() != null) {
					this.cadastrosApoioExamesFacade.excluirParametroCamposLaudo(parametroSelect);
					exibirMsg = true;
				}
				parametros.remove(componentId);
				desSelecionarParametros();
				if(exibirMsg) {
					this.apresentarMsgNegocio(Severity.INFO, "MSG_MASCARA_PARAMETRO_EXCLUIDO_SUCESSO");
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	// --[SALVAR todos]
	public void salvar() throws ParserConfigurationException, SAXException, IOException {
		try {
			List<AelParametroCamposLaudo> list = new ArrayList<AelParametroCamposLaudo>(parametros.values());
			if (list != null && list.size() > 0) {
				cadastrosApoioExamesFacade.persistirParametroCamposLaudo(list);
			}
			this.apresentarMsgNegocio(Severity.INFO, "MSG_MASCARA_EXAME_SALVA");
			this.parametroSelect = null;
			pendenciaGravacao = false;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void persistirFormula(Boolean sair){
		validaExpressao(sair);
		persistirParametroSelecionado();
	}
	
	public void persistirParametroSelecionado(){
		try {
			cadastrosApoioExamesFacade.persistirParametroCamposLaudo(this.parametroSelect);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void gravar() {
		persistirParametroSelecionado();
		this.apresentarMsgNegocio(Severity.INFO, "MSG_CAMPO_MASCARA_EXAME_SALVO");
		if(StringUtils.isNotBlank(parametroSelect.getTextoLivre())) {
			converterParametrosEmCss(parametroSelect);
		}
		if (this.isEditorRenderizado()) {
			RequestContext.getCurrentInstance().execute("PF('editorWG').focus();");
		}
	}

	public void abrirModalVincular() {
		String usuarioLogado = super.obterLoginUsuarioLogado();
		if(getPermissionService().usuarioTemPermissao(usuarioLogado, "desenharMascara", "pesquisar") 
				&& this.isParametroSelectPermiteVincular()){
			if(parametros != null && !parametros.isEmpty()) {
				for (Entry<String, AelParametroCamposLaudo> entry : parametros.entrySet())
				{
					if(StringUtils.isNotBlank(entry.getValue().getTextoLivre())) {
						entry.getValue().setTextoLivre(Jsoup.parse(entry.getValue().getTextoLivre()).text());
					}
				}
			}			
			super.openDialog("modalVincularCamposWG");			
		}
	}
	
	public void limpaCampoExpressao() {
		parametroSelect.setTextoLivre(null);
	}

	public void montaFormula(String opcao) {
		DominioOpcoesFormulaParametroCamposLaudo opcaoFormula = DominioOpcoesFormulaParametroCamposLaudo.getInstance(opcao);
		cadastrosApoioExamesFacade.montaFormulaParametroCampoLaudo(parametroSelect, null, opcaoFormula);
	}

	public void montaFormula(AelParametroCamposLaudo parametroCLSelecionado) {
		cadastrosApoioExamesFacade.montaFormulaParametroCampoLaudo(parametroSelect, parametroCLSelecionado, null);
	}

	public void ativarMascara() {
		DominioSituacaoVersaoLaudo situacaoVersaoAntiga = versaoLaudo.getSituacao();
		try {
			versaoLaudo.setSituacao(DominioSituacaoVersaoLaudo.A);
			cadastrosApoioExamesFacade.persistirVersaoLaudo(versaoLaudo);
			desSelecionarParametros();
			this.apresentarMsgNegocio(Severity.INFO, "MSG_MASCARA_EXAME_ATIVADA");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			versaoLaudo.setSituacao(situacaoVersaoAntiga);
		}
	}

	// --[VISUALIZAR PRÉVIA]
	public String previa() {
		try {
			if (parametros == null) {
				inicio();
			}

			if (exibirPdf) {
				ExamesListaVO dadosLaudo = this.examesFacade.buscarDadosLaudoFPreview(new ArrayList<AelParametroCamposLaudo>(parametros.values()), versaoLaudo);
				consultarResultadosNotaAdicionalController.setPreviaPDF(true);
				consultarResultadosNotaAdicionalController.setDadosLaudo(dadosLaudo);
				return PAGE_PREVIA_PDF;
			} else {
				DesenhoMascaraExameVO desenho = this.mascaraExamesComponentes.buscaPreviaMascarasExamesVO( 
						new ArrayList<AelParametroCamposLaudo>(parametros.values()), versaoLaudo, null);
				manterMascaraExamesPreviaController.setPanelPrevia(desenho.getFormularioDinamicoPanel());
				List<DesenhoMascaraExameVO> desenhos = new ArrayList<DesenhoMascaraExameVO>();
				desenhos.add(desenho);
				manterMascaraExamesPreviaController.setDesenhosMascarasExamesVO(desenhos);
				return PAGE_PREVIA_FORM;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void validaExpressao(Boolean sair) {
		Object retorno = null;
		try {
			if (this.parametroSelect != null && this.parametroSelect.getTextoLivre() != null
					&& !this.parametroSelect.getTextoLivre().isEmpty()) {
				this.cadastrosApoioExamesFacade.validarExpressaoFormula(this.parametroSelect.getTextoLivre());

				DesenhoMascaraExameVO desenho = this.mascaraExamesComponentes.buscaPreviaMascarasExamesVO(
						new ArrayList<AelParametroCamposLaudo>(parametros.values()), versaoLaudo, null);

				List<DesenhoMascaraExameVO> desenhos = new ArrayList<DesenhoMascaraExameVO>();
				desenhos.add(desenho);
				calculaValorMascaraExamesComponente.setDesenhosMascarasExamesVO(desenhos);
				calculaValorMascaraExamesComponente.setPrevia(Boolean.TRUE);

				/*
				 * Só vai retornar null se houve erro caso contrário retorna
				 * valor simbólico 'previaForm'
				 */
				retorno = calculaValorMascaraExamesComponente.calcularValor(this.parametroSelect.getId().toString());
				if (retorno != null) {
					resultadoValidacaoFormula = retorno.toString();
				} else {
					resultadoValidacaoFormula = null;
				}
				variaveisFormula = calculaValorMascaraExamesComponente.obterListaVariaveis(this.parametroSelect.getTextoLivre());
				if (!sair) {
					super.openDialog("modalResultadoExpressaoWG");
				} else {
					super.closeDialog("modalFormulaWG");
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (MissingPropertyException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_EXCESSAO_EXPRESSAO");
		} catch (MultipleCompilationErrorsException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_EXCESSAO_EXPRESSAO");
		}
	}
	
	public void atualizarValorVariavel(VariaveisValorFormulaMascaraExameVO vo) {
		this.calculaValorMascaraExamesComponente.atualizarValorVariavel(this.parametroSelect.getId().toString(), vo.getVariavel(), vo.getValorNumerico().toString());
	}
	
	public void atualizarValorVariavelSexo(VariaveisValorFormulaMascaraExameVO vo) {
		this.calculaValorMascaraExamesComponente.atualizarValorVariavel(this.parametroSelect.getId().toString(), vo.getVariavel(), vo.getValorSexo().toUpperCase());
	}
	
	public void atualizarResultado() {
		Object retorno = calculaValorMascaraExamesComponente.calcularValor(this.parametroSelect.getId().toString());
		if (retorno != null) {
			resultadoValidacaoFormula = retorno.toString();
		} else {
			resultadoValidacaoFormula = null;
		}
		super.openDialog("modalResultadoExpressaoWG");
	}

	// --[LIMPAR TODA TELA]
	public void limpar() {
		inicio();
		frameCache = "";
	}

	public String previaPdf() {
		setExibirPdf(true);
		return previa();
	}

	public String previaPreenchimento() {
		setExibirPdf(false);
		return previa();
	}

	public String voltar(Boolean force) {
		if (pendenciaGravacao && !force) {
			super.openDialog("modalConfirmacaoVoltarWG");
			return null;
		} else {
			this.versaoLaudo = null;
			this.parametroSelect = null;
			panelFrameComponent.getChildren().clear();
			return voltarPara;
		}
	}

	// --[MONTA OBJETO BASE]
	private OutputPanel componenteBase(int top, int left, int zindex) {
		Application application = FacesContext.getCurrentInstance().getApplication();
		OutputPanel base = (OutputPanel) application.createComponent(OutputPanel.COMPONENT_TYPE);
		base.setLayout("block");
		StringBuilder css = new StringBuilder(24);
		css.append("top:").append(top).append(PX)
		.append("left:").append(left).append(PX)
		.append("z-index:").append(zindex).append(';');
		base.setStyle(css.toString());
		return base;
	}

	public boolean requiredTextoFixo() {
		return parametroSelect.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_FIXO);
	}

	private void titulaComponente(OutputPanel base, AelParametroCamposLaudo param) {
		String dragClass = "";
		StringBuilder css = new StringBuilder(base.getStyle());

		switch (param.getObjetoVisual()) {
		case TEXTO_FIXO:
			dragClass = "drag1";
			css.append(obterStyleCampo(param));

			Application application = FacesContext.getCurrentInstance().getApplication();
			HtmlOutputText text = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
			text.setValue(getTextoFixoSemTag(param.getTextoLivre()).replace("<root>", "").replace("</root>", ""));
			base.getChildren().add(text);
			break;
		case TEXTO_ALFANUMERICO:
			dragClass = "drag2";
			break;
		case TEXTO_NUMERICO_EXPRESSAO:
			dragClass = "drag3";
			break;
		case TEXTO_LONGO:
			dragClass = "drag41";
			if (param != null && param.getLarguraObjetoVisual() == null || param.getLarguraObjetoVisual() == 0) {
				param.setLarguraObjetoVisual(Short.valueOf("450"));
			}
			if (param != null && param.getAlturaObjetoVisual() == null || param.getAlturaObjetoVisual() == 0) {
				param.setAlturaObjetoVisual(Short.valueOf("100"));
			}
			break;
		case TEXTO_CODIFICADO:
			dragClass = "drag5";
			break;
		case EQUIPAMENTO:
			dragClass = "drag6";
			break;
		case METODO:
			dragClass = "drag7";
			break;
		case RECEBIMENTO:
			dragClass = "drag8";
			break;
		case HISTORICO:
			dragClass = "drag9";
			break;
		case VALORES_REFERENCIA:
			dragClass = "drag10";
			break;
		default:
			break;
		}
		defineAlturaLarguraCampo(base, param, css);
		base.setStyleClass("load " + dragClass + " ui-draggable dragged");
		base.setStyle(css.toString());
	}

	public String getStyleCampo() {
		if (parametroSelect != null) {
			styleCampo = obterStyleCampo(parametroSelect);
		} else {
			styleCampo = "";
		}
		return styleCampo;
	}

	public String getTextoCampo() {
		if (parametroSelect != null && parametroSelect.getTextoLivre() != null) {
			textoCampo = getTextoFixoSemTag(parametroSelect.getTextoLivre());
		} else {
			textoCampo = "";
		}
		return textoCampo;
	}

	public String getTextoFixoSemTag(String textoLivre) {
		return StringUtils.isBlank(textoLivre) ? "" : StringEscapeUtils.unescapeHtml4(this.mascaraExamesComponentes
				.retornaParametroCampoLaudoTextoLivreSemTag(textoLivre));
	}

	public void verificaRenderizarFormulas() {
		selecionarInserirParametro(false);
		if (parametroSelect != null && parametroSelect.getCampoLaudo() != null) {
			if (!DominioTipoCampoCampoLaudo.E.equals(parametroSelect.getCampoLaudo().getTipoCampo())) {
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_FORMULAS_SO_CAMPOS_EXPRESSAO");
			} else {
				super.openDialog("modalFormulaWG");
			}
		}
	}

	private String obterStyleCampo(AelParametroCamposLaudo param) {
		if (param == null) {
			return "";
		}
		StringBuilder css = new StringBuilder();
		if (param.getId() != null) {
			if (param != null && param.getExibicao() != null && !param.getExibicao().equals(DominioExibicaoParametroCamposLaudo.T)
					&& !param.getExibicao().equals(DominioExibicaoParametroCamposLaudo.A)) {
				css.append("opacity:0.3;");
			}
			css.append(this.mascaraExamesComponentes.obterStyleParametroCampoLaudo(param));
		}
		return css.toString();
	}

	private void defineAlturaLarguraCampo(OutputPanel base, AelParametroCamposLaudo param, StringBuilder css) {
		if (!param.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_FIXO)) {
			if (param.getAlturaObjetoVisual() != null) {
				css.append("height:" + param.getAlturaObjetoVisual() + PX);
			}
			if (param.getLarguraObjetoVisual() != null) {
				css.append("width:" + param.getLarguraObjetoVisual() + PX);
			}
		}
	}

	public List<AelCampoLaudo> suggestionCampoLaudo(String descr) {
		DominioTipoCampoCampoLaudo tipoLaudo = null;
		if (!DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(parametroSelect.getObjetoVisual())) {
			tipoLaudo = DominioTipoCampoCampoLaudo.valueOf(parametroSelect.getObjetoVisual().getSiglaLaudo());
		}
		return this.returnSGWithCount(examesFacade.pesquisarAelCampoLaudoSB(tipoLaudo, descr),suggestionCampoLaudoCount(descr));
	}

	public Long suggestionCampoLaudoCount(String descr) {
		DominioTipoCampoCampoLaudo tipoLaudo = null;
		if (!DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(parametroSelect.getObjetoVisual())) {
			tipoLaudo = DominioTipoCampoCampoLaudo.valueOf(parametroSelect.getObjetoVisual().getSiglaLaudo());
		}
		return examesFacade.pesquisarAelCampoLaudoSBCount(tipoLaudo, descr);
	}

	public List<AelParametroCamposLaudo> listaObjetosDigitados() {
		List<AelParametroCamposLaudo> lst = new ArrayList<AelParametroCamposLaudo>();

		List<AelParametroCamposLaudo> listParametros = new ArrayList<AelParametroCamposLaudo>(parametros.values());
		for (AelParametroCamposLaudo aelParamCamLau : listParametros) {
			if (DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(aelParamCamLau.getObjetoVisual())
					&& aelParamCamLau.getCampoLaudo() != null 
					&& DominioTipoCampoCampoLaudo.N.equals(aelParamCamLau.getCampoLaudo().getTipoCampo())) {
				lst.add(aelParamCamLau);
			}
		}
		return lst;
	}
	
	public List<AelCampoLaudo> suggestionCampoLaudoRelacionado(String descr) {
		List<AelCampoLaudo> camposRel = new ArrayList<AelCampoLaudo>();
		Integer seqCampoLaudo = null;
		String campoLaudoRelDesc = null;
		try {
			seqCampoLaudo = Integer.valueOf(descr);
		} catch (ClassCastException e) {
			campoLaudoRelDesc = (String) descr;
		} catch (NumberFormatException e) {
			campoLaudoRelDesc = (String) descr;
		}
		List<AelParametroCamposLaudo> list = new ArrayList<AelParametroCamposLaudo>(parametros.values());
		if (list != null && list.size() > 0) {
			for (AelParametroCamposLaudo aelParamCampLaudo : list) {
				if (aelParamCampLaudo.getCampoLaudo() != null
						&& this.parametroSelect.getCampoLaudo() != null
						&& (DominioObjetoVisual.TEXTO_CODIFICADO.equals(aelParamCampLaudo.getObjetoVisual())
								|| (DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO.equals(aelParamCampLaudo.getObjetoVisual()) && DominioTipoCampoCampoLaudo.N
										.equals(aelParamCampLaudo.getCampoLaudo().getTipoCampo())) || DominioObjetoVisual.TEXTO_LONGO
									.equals(aelParamCampLaudo.getObjetoVisual()))) {
					if (aelParamCampLaudo.getCampoLaudo().getSeq().equals(seqCampoLaudo) && this.parametroSelect.getCampoLaudo() != null
							&& !this.parametroSelect.getCampoLaudo().getSeq().equals(aelParamCampLaudo.getCampoLaudo().getSeq())) {
						camposRel.add(aelParamCampLaudo.getCampoLaudo());
					} else if (campoLaudoRelDesc != null
							&& aelParamCampLaudo.getCampoLaudo().getNome().toLowerCase().contains(campoLaudoRelDesc)
							&& !this.parametroSelect.getCampoLaudo().getSeq().equals(aelParamCampLaudo.getCampoLaudo().getSeq())) {
						camposRel.add(aelParamCampLaudo.getCampoLaudo());
					}
				}
			}
		}
		return camposRel;
	}

	public Boolean isLabelGroup() {
		return parametroSelect != null && DominioObjetoVisual.TEXTO_FIXO.equals(parametroSelect.getObjetoVisual());
	}

	public Boolean isInputGroup() {
		return parametroSelect != null
				&& (DominioObjetoVisual.TEXTO_ALFANUMERICO.equals(parametroSelect.getObjetoVisual())
						|| DominioObjetoVisual.TEXTO_CODIFICADO.equals(parametroSelect.getObjetoVisual())
						|| DominioObjetoVisual.TEXTO_LONGO.equals(parametroSelect.getObjetoVisual()) || DominioObjetoVisual.TEXTO_NUMERICO_EXPRESSAO
							.equals(parametroSelect.getObjetoVisual()));
	}

	public Boolean isControlGroup() {
		return parametroSelect != null
				&& (DominioObjetoVisual.EQUIPAMENTO.equals(parametroSelect.getObjetoVisual())
						|| DominioObjetoVisual.HISTORICO.equals(parametroSelect.getObjetoVisual())
						|| DominioObjetoVisual.METODO.equals(parametroSelect.getObjetoVisual()) || DominioObjetoVisual.RECEBIMENTO
							.equals(parametroSelect.getObjetoVisual()));
	}

	public Boolean isRelacionavel() {
		return parametroSelect != null
				&& (DominioObjetoVisual.VALORES_REFERENCIA.equals(parametroSelect.getObjetoVisual()) || DominioObjetoVisual.HISTORICO
						.equals(parametroSelect.getObjetoVisual()));
	}

	/**
	 * Verifica se o parâmetro campo laudo selecionado permite vinculo
	 */
	public boolean isParametroSelectPermiteVincular() {
		return this.desativarPermissaoVicularRelacionarTipoCampoSelecionado(DominioObjetoVisual.TEXTO_FIXO,
				DominioObjetoVisual.EQUIPAMENTO, DominioObjetoVisual.METODO, DominioObjetoVisual.RECEBIMENTO);
	}

	/**
	 * Verifica se o parâmetro campo laudo selecionado permite relacionamento
	 */
	public boolean isParametroSelectPermiteRelacionar() {
		return this.desativarPermissaoVicularRelacionarTipoCampoSelecionado(DominioObjetoVisual.METODO, DominioObjetoVisual.HISTORICO,
				DominioObjetoVisual.EQUIPAMENTO, DominioObjetoVisual.RECEBIMENTO, DominioObjetoVisual.VALORES_REFERENCIA);
	}

	public void abrirModalRelacionar() {
		if (parametroSelect != null &&  isParametroSelectPermiteRelacionar() &&  !Objects.equals(DominioObjetoVisual.TEXTO_FIXO, parametroSelect.getObjetoVisual()) && securityController.usuarioTemPermissao("desenharMascara", "executar")) {
			if(parametros != null && !parametros.isEmpty()) {
				for (Entry<String, AelParametroCamposLaudo> entry : parametros.entrySet())
				{
					if(StringUtils.isNotBlank(entry.getValue().getTextoLivre())) {
						entry.getValue().setTextoLivre(Jsoup.parse(entry.getValue().getTextoLivre()).text());
					}
				}
			}			
			super.openDialog("modalRelacionamentosWG");			
		}
	}
	/**
	 * Desativa o vinculo/relação quando o parâmetro selecionado pertencer ao
	 * conjunto dos tipos visuais informados
	 * 
	 * @param dominioObjetoVisuals
	 * @return
	 */
	private boolean desativarPermissaoVicularRelacionarTipoCampoSelecionado(DominioObjetoVisual... dominioObjetoVisuals) {
		if (this.parametroSelect != null && dominioObjetoVisuals != null) {
			// Obtém o tipo de objeto visual do parâmetro selecionado
			DominioObjetoVisual tipoSelecionado = this.parametroSelect.getObjetoVisual();
			for (DominioObjetoVisual dominioObjetoVisual : dominioObjetoVisuals) {
				if (dominioObjetoVisual.equals(tipoSelecionado)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Lista campos laudos utilizados no desenho de máscara que está sendo
	 * criado
	 * 
	 * @return
	 */
	public Set<CampoLaudoUtilizadoDesenhoMascaraVO> listarCamposLaudoUtilizadosDesenhoMascara() {
		// Instancia uma nova lista de campos laudos utilizados no desenho
		this.listaCamposLaudoUtilizadosDesenhoMascara = new ArrayList<CampoLaudoUtilizadoDesenhoMascaraVO>();
		// Pesquisa TODOS os parâmetros campos laudo da TELA
		List<AelParametroCamposLaudo> listaParametroCamposLaudosTela = new ArrayList<AelParametroCamposLaudo>(this.parametros.values());
		// O campo selecionado pelo usuário NÃO deve se auto relacionar
		listaParametroCamposLaudosTela.remove(this.parametroSelect);
		// Caso o parâmetro campo laudo selecionado pelo usuário EXISTIR NO
		// BANCO e NÃO CONTER campos vinculados
		if (this.parametroSelect.getId() != null && this.parametroSelect.getCampoVinculados() == null) {
			// Pesquisa no BANCO os parâmetros campo laudo através do campos
			// laudo vinculados ao parâmetro selecionado pelo usuário
			List<AelParametroCamposLaudo> listaCamposLaudosBanco = this
					.pesquisarParametroCamposLaudoCampoVinculadoBanco(this.parametroSelect);
			// Percorre lista de campos laudos da TELA
			for (AelParametroCamposLaudo parametroCamposLaudoTela : listaParametroCamposLaudosTela) {
				boolean selecionado = false;
				// Seleciona itens que já estão vinculados no banco
				if (listaCamposLaudosBanco.contains(parametroCamposLaudoTela)) {
					selecionado = true;
				}
				// Acrescenta e instancia um novo VO através dos campos laudos
				// utilizados
				this.listaCamposLaudoUtilizadosDesenhoMascara.add(new CampoLaudoUtilizadoDesenhoMascaraVO(selecionado,
						parametroCamposLaudoTela));
			}
		} else { // Caso o parâmetro campo laudo selecionado pelo usuário
					// CONTENHA campos vinculados
			// Pesquisa na MEMÓRIA os parâmetros campo laudo através do campos
			// laudo vinculados ao parâmetro selecionado pelo usuário
			List<AelParametroCamposLaudo> listaCampoLaudosParametroSelect = new ArrayList<AelParametroCamposLaudo>();
			if(this.parametroSelect.getCampoVinculados() != null && !this.parametroSelect.getCampoVinculados().isEmpty()) {
				listaCampoLaudosParametroSelect = this
						.pesquisarParametroCamposLaudoCampoVinculadoMemoria(new ArrayList<AelCampoVinculado>(this.parametroSelect.getCampoVinculados()));
			}
			
			// Percorre lista de parâmetros campo laudo da TELA
			for (int i = 0; i < listaParametroCamposLaudosTela.size(); i++) {
				final AelParametroCamposLaudo parametroCamposLaudoTela = listaParametroCamposLaudosTela.get(i);
				// Caso CONTENHA o item de parâmetro campo laudo
				if (listaCampoLaudosParametroSelect.contains(parametroCamposLaudoTela)) {
					// Acrescenta e instancia um novo VO (SELECIONADO) através
					// do parâmetro campo laudo
					this.listaCamposLaudoUtilizadosDesenhoMascara.add(new CampoLaudoUtilizadoDesenhoMascaraVO(true,
							parametroCamposLaudoTela));
				} else {
					// Acrescenta e instancia um novo VO (NÃO SELECIONADO)
					// através do parâmetro campo laudo
					this.listaCamposLaudoUtilizadosDesenhoMascara.add(new CampoLaudoUtilizadoDesenhoMascaraVO(false,
							parametroCamposLaudoTela));
				}
			}
		}
		return new HashSet<CampoLaudoUtilizadoDesenhoMascaraVO>(listaCamposLaudoUtilizadosDesenhoMascara);
	}

	/**
	 * Lista campos laudos utilizados no desenho de máscara que está sendo
	 * criado
	 * 
	 * @return
	 */
	public Set<CampoLaudoUtilizadoDesenhoMascaraVO> listarCamposLaudo() {
		// Instancia uma nova lista de campos laudos utilizados no desenho
		this.listaCamposLaudoUtilizadosDesenhoMascara = new ArrayList<CampoLaudoUtilizadoDesenhoMascaraVO>();
		if (this.tipoRelacionamento != null) {
			// Pesquisa TODOS os parâmetros campos laudo da TELA
			List<AelParametroCamposLaudo> listaParametroCamposLaudosTela = new ArrayList<AelParametroCamposLaudo>(this.parametros.values());
			// Caso o parâmetro campo laudo selecionado pelo usuário NÃO
			// CONTENHA campos vinculados
			if (this.parametroSelect!=null){
			if (((this.parametroSelect.getCampoCodifRelacionado() == null || this.parametroSelect.getCampoCodifRelacionado().isEmpty()) && DominioTipoRelacionamento.C
					.equals(this.tipoRelacionamento))
					|| ((this.parametroSelect.getCamposLaudoRelacionados() == null || this.parametroSelect.getCamposLaudoRelacionados()
							.isEmpty()) && DominioTipoRelacionamento.N.equals(this.tipoRelacionamento))) {

				// Pesquisa no BANCO os parâmetros campo laudo codificados
				// passando o parâmetro selecionado.
				List<AelParametroCamposLaudo> listaCamposLaudosBanco = this.pesquisarParametroCamposLaudos(this.parametroSelect);

				// Percorre lista de campos laudos da TELA
				for (AelParametroCamposLaudo parametroCamposLaudoTela : listaParametroCamposLaudosTela) {
					boolean selecionado = false;
					// Seleciona itens que já estão vinculados no banco
					if (!listaCamposLaudosBanco.isEmpty() && listaCamposLaudosBanco.contains(parametroCamposLaudoTela)) {
						selecionado = true;
					}
					if (this.verificarTipoRelacionamentoModalCodificados(parametroCamposLaudoTela)) {
						// Acrescenta e instancia um novo VO através dos campos
						// laudos utilizados
							if (!this.parametroSelect.getId().equals(parametroCamposLaudoTela.getId())){
						this.listaCamposLaudoUtilizadosDesenhoMascara.add(new CampoLaudoUtilizadoDesenhoMascaraVO(selecionado,
								parametroCamposLaudoTela));
					}
				}
					}
			} else { // Caso o parâmetro campo laudo selecionado pelo usuário
						// CONTENHA campos codificados
				// Pesquisa na MEMÓRIA os parâmetros campo laudo através do
				// campos laudo vinculados ao parâmetro selecionado pelo usuário
				List<AelParametroCamposLaudo> listaCampoLaudosParametroSelect = this
						.pesquisarParametroCamposLaudoMemoria(this.parametroSelect);
				// Percorre lista de parâmetros campo laudo da TELA
				for (AelParametroCamposLaudo parametroCamposLaudoTela : listaParametroCamposLaudosTela) {
					if (!this.verificarTipoRelacionamentoModalCodificados(parametroCamposLaudoTela)) {
						continue;
					}
					// Caso NÃO contenha o item de parâmetro campo laudo
					if (!listaCampoLaudosParametroSelect.contains(parametroCamposLaudoTela)) {
						// Acrescenta e instancia um novo VO (NÃO selecionado)
						// através do parâmetro campo laudo
						if (!parametroSelect.getId().equals(parametroCamposLaudoTela.getId())){
							this.listaCamposLaudoUtilizadosDesenhoMascara.add(new CampoLaudoUtilizadoDesenhoMascaraVO(false,
									parametroCamposLaudoTela));
						}
					} else {
						// Obtém o parâmetro campo laudo da lista de campos
						// laudos do parâmetro selecionado pelo usuário
						int indiceCampoLaudoParametroSelect = listaCampoLaudosParametroSelect.indexOf(parametroCamposLaudoTela);
						AelParametroCamposLaudo campoLaudoParametroSelect = listaCampoLaudosParametroSelect
								.get(indiceCampoLaudoParametroSelect);
						// Acrescenta e instancia um novo VO (SELECIONADO)
						// através do parâmetro campo laudo
						this.listaCamposLaudoUtilizadosDesenhoMascara.add(new CampoLaudoUtilizadoDesenhoMascaraVO(true,
								campoLaudoParametroSelect));
					}
				}
			}
		}
		}
		return new HashSet<CampoLaudoUtilizadoDesenhoMascaraVO>(listaCamposLaudoUtilizadosDesenhoMascara);
	}

	/**
	 * Verificações quando aberta a modal de relacionamento de codificados
	 * 
	 * @param parametroCamposLaudoTela
	 * @return
	 */
	private boolean verificarTipoRelacionamentoModalCodificados(AelParametroCamposLaudo parametroCamposLaudoTela) {
		// Só dara true essa condição de for aberta a modal de relacionamento de
		// codificados */
		return parametroCamposLaudoTela.getCampoLaudo() != null
				&& ((DominioTipoRelacionamento.C.equals(this.tipoRelacionamento)
						&& parametroCamposLaudoTela.getObjetoVisual().equals(DominioObjetoVisual.TEXTO_CODIFICADO)
						&& parametroSelect.getCampoLaudo() != null && parametroCamposLaudoTela.getCampoLaudo().getSeq()
						.equals(parametroSelect.getCampoLaudo().getSeq())) || (DominioTipoRelacionamento.N.equals(this.tipoRelacionamento) && !DominioObjetoVisual.TEXTO_FIXO
						.equals(parametroCamposLaudoTela.getObjetoVisual())));
	}

	/**
	 * Pesquisa no BANCOs parâmetros campo laudo através do campos laudo
	 * vinculados ao parâmetro selecionado pelo usuário
	 * 
	 * @param parametroCamposLaudo
	 * @return
	 */
	private List<AelParametroCamposLaudo> pesquisarParametroCamposLaudoCampoVinculadoBanco(AelParametroCamposLaudo parametroCamposLaudo) {
		List<AelParametroCamposLaudo> retorno = new ArrayList<AelParametroCamposLaudo>();
		List<AelCampoVinculado> listaCamposVinculados = this.cadastrosApoioExamesFacade
				.pesquisarCampoVinculadoPorParametroCampoLaudo(parametroCamposLaudo);
		for (AelCampoVinculado campoVinculado : listaCamposVinculados) {
			retorno.add(campoVinculado.getAelParametroCamposLaudoByAelCvcPclFk2());
		}
		return retorno;
	}

	/**
	 * Pesquisa no BANCO os parâmetros campo laudo Codificados
	 * 
	 * @param parametroCamposLaudo
	 * @return
	 */
	private List<AelParametroCamposLaudo> pesquisarParametroCamposLaudos(AelParametroCamposLaudo parametroCamposLaudo) {
		List<AelParametroCamposLaudo> retorno = new ArrayList<AelParametroCamposLaudo>();

		List<AelCampoCodifRelacionado> listaCamposCodificados = null;
		List<AelCampoLaudoRelacionado> listaCamposLaudosRelacionados = null;

		/* Busca os campos laudos codificados relacionados */
		if (DominioTipoRelacionamento.C.equals(this.tipoRelacionamento)) {
			listaCamposCodificados = this.cadastrosApoioExamesFacade
					.pesquisarCampoCodificadoPorParametroCampoLaudoECampoLaudo(parametroCamposLaudo);
			for (AelCampoCodifRelacionado campoCodificado : listaCamposCodificados) {
				
				AelParametroCampoLaudoId id = new AelParametroCampoLaudoId(campoCodificado.getId().getPclVelEmaExaSigla(), 
						campoCodificado.getId().getPclVelEmaManSeqVinculado(),
						campoCodificado.getId().getPclVelSeqpVinculado(),
						campoCodificado.getId().getPclCalSeqVinculado(),
						campoCodificado.getId().getPclSeqpVinculado());				

				AelParametroCamposLaudo paramCampoLaudo = this.cadastrosApoioExamesFacade.obterAelParametroCamposLaudoPorId(id);
				retorno.add(paramCampoLaudo);
			}
			/* Busca os campos laudos comuns relacionados */
		} else if (DominioTipoRelacionamento.N.equals(this.tipoRelacionamento)) {
			listaCamposLaudosRelacionados = this.cadastrosApoioExamesFacade.pesquisarCampoLaudoPorParametroCampoLaudo(parametroCamposLaudo);
			for (AelCampoLaudoRelacionado campoRelacionado : listaCamposLaudosRelacionados) {
				retorno.add(campoRelacionado.getAelParametroCamposLaudoByAelClvPclFk2());
			}
		}
		return retorno;
	}

	/**
	 * Pesquisa na MEMÓRIA os parâmetros campo laudo através do campos laudo
	 * vinculados ao parâmetro selecionado pelo usuário
	 * 
	 * @param parametroCamposLaudo
	 * @return
	 */
	private List<AelParametroCamposLaudo> pesquisarParametroCamposLaudoCampoVinculadoMemoria(List<AelCampoVinculado> camposVinculados) {
		List<AelParametroCamposLaudo> retorno = new ArrayList<AelParametroCamposLaudo>();
		if (camposVinculados != null) {
			for (AelCampoVinculado campoVinculado : camposVinculados) {
				retorno.add(campoVinculado.getAelParametroCamposLaudoByAelCvcPclFk2());
			}
		}
		return retorno;
	}

	/**
	 * Pesquisa na MEMÓRIA os parâmetros campo laudo através do campos laudo
	 * vinculados ao parâmetro selecionado pelo usuário
	 * 
	 * @param parametroCamposLaudo
	 * @return
	 */
	private List<AelParametroCamposLaudo> pesquisarParametroCamposLaudoMemoria(AelParametroCamposLaudo paramSelected) {
		List<AelParametroCamposLaudo> retorno = new ArrayList<AelParametroCamposLaudo>();
		if (DominioTipoRelacionamento.C.equals(this.tipoRelacionamento)) {
			for (AelCampoCodifRelacionado campoCodificado : paramSelected.getCampoCodifRelacionado()) {
				retorno.add(campoCodificado.getAelParametroCamposLaudoByAelCcrPclFk2());
			}
		} else if (DominioTipoRelacionamento.N.equals(this.tipoRelacionamento)) {
			for (AelCampoLaudoRelacionado campoRelacionado : paramSelected.getCamposLaudoRelacionados()) {
				retorno.add(campoRelacionado.getAelParametroCamposLaudoByAelClvPclFk2());
			}
		}
		return retorno;
	}

	public String cadastrarNovoResultadoPadraoLaudo() {
		return PAGE_NOVO_RESULTADO_PADRAO_LAUDO;
	}

	/**
	 * Grava em memória ou associa uma nova lista de campos vinculados ao campo
	 * laudo selecionado pelo usuário
	 */
	// @Restrict("#{s:hasPermission('desenharMascara','executar')}")
	public void gravarCamposVinculadosRelacionados(String modalWG) {
		if (DominioTipoRelacionamento.C.equals(this.tipoRelacionamento)) {
			this.parametroSelect.setCampoCodifRelacionado(new HashSet<AelCampoCodifRelacionado>());
		} else if (DominioTipoRelacionamento.N.equals(this.tipoRelacionamento)) {
			this.parametroSelect.setCamposLaudoRelacionados(new HashSet<AelCampoLaudoRelacionado>());
		} else {
			this.parametroSelect.setCampoVinculados(new HashSet<AelCampoVinculado>());
		}
		for (CampoLaudoUtilizadoDesenhoMascaraVO vo : this.listaCamposLaudoUtilizadosDesenhoMascara) {
			// Se o item de VO estiver selecionado/marcado
			if (vo.isSelecionado()) {
				if (DominioTipoRelacionamento.C.equals(this.tipoRelacionamento)) {
					// Instancia um objeto do tipo relacionamento codificado
					AelCampoCodifRelacionado campoCodifRel = new AelCampoCodifRelacionado(new AelCampoCodifRelacionadoId(this.parametroSelect.getId().getVelEmaExaSigla(), this.parametroSelect.getId().getVelEmaManSeq(), 
							this.parametroSelect.getId().getVelSeqp(), this.parametroSelect.getId().getCalSeq(), this.parametroSelect.getId().getSeqp(), 
							vo.getParametroCamposLaudo().getId().getVelEmaExaSigla(), vo.getParametroCamposLaudo().getId().getVelEmaManSeq(), vo.getParametroCamposLaudo().getId().getVelSeqp(), 
							vo.getParametroCamposLaudo().getId().getCalSeq(), vo.getParametroCamposLaudo().getId().getSeqp()), 
							this.parametroSelect, vo.getParametroCamposLaudo());
					this.parametroSelect.getCampoCodifRelacionado().add(campoCodifRel);

				} else if (DominioTipoRelacionamento.N.equals(this.tipoRelacionamento)) {
					// Instancia um objeto do tipo relacionamento comum
					AelCampoLaudoRelacionado campoRelacionado = new AelCampoLaudoRelacionado(new AelCampoLaudoRelacionadoId(this.parametroSelect.getId().getVelEmaExaSigla(), this.parametroSelect.getId().getVelEmaManSeq(), 
							this.parametroSelect.getId().getVelSeqp(), this.parametroSelect.getId().getCalSeq(), this.parametroSelect.getId().getSeqp(), 
							vo.getParametroCamposLaudo().getId().getVelEmaExaSigla(), vo.getParametroCamposLaudo().getId().getVelEmaManSeq(), vo.getParametroCamposLaudo().getId().getVelSeqp(), 
							vo.getParametroCamposLaudo().getId().getCalSeq(), vo.getParametroCamposLaudo().getId().getSeqp()), 
							this.parametroSelect, vo.getParametroCamposLaudo());
					this.parametroSelect.getCamposLaudoRelacionados().add(campoRelacionado);

				} else {
					// Intancia um novo campo vinculado
					AelCampoVinculado campoVinculado = new AelCampoVinculado(new AelCampoVinculadoId(this.parametroSelect.getId().getVelEmaExaSigla(), this.parametroSelect.getId().getVelEmaManSeq(), 
							this.parametroSelect.getId().getVelSeqp(), this.parametroSelect.getId().getCalSeq(), this.parametroSelect.getId().getSeqp(), 
							vo.getParametroCamposLaudo().getId().getVelEmaExaSigla(), vo.getParametroCamposLaudo().getId().getVelEmaManSeq(), vo.getParametroCamposLaudo().getId().getVelSeqp(), 
							vo.getParametroCamposLaudo().getId().getCalSeq(), vo.getParametroCamposLaudo().getId().getSeqp()), 
							this.parametroSelect, vo.getParametroCamposLaudo());
					this.parametroSelect.getCampoVinculados().add(campoVinculado);
				}
			}
		}
		pendenciaGravacao = true;
		super.closeDialog(modalWG);
	}

	/**
	 * Lista AelResultadoPadrao
	 * 
	 * @return
	 */
	public void listarResultadosPadrao() {
		if (versaoLaudo != null) {
			emaExaSigla = versaoLaudo.getId().getEmaExaSigla();
			emaManSeq = versaoLaudo.getId().getEmaManSeq();
		}
		listaResultadoPadrao = this.cadastrosApoioExamesFacade.listarResultadoPadraoCampoPorExameMaterial(emaExaSigla,
				emaManSeq);
	}

	public void abrirModalResultadosPadrao() {
		this.listarResultadosPadrao();
		super.openDialog("modalResultPadraoWG");
	}
	
	public boolean isActive(AelResultadosPadrao item) {
		return (item.getSituacao() == DominioSituacao.A);
	}

	/**
	 * Método que realiza a ação do botão atualizar situação
	 */
	// @Restrict("#{s:hasPermission('informarValoresResultadoPadraoLaudo','executar')}")
	public void editarSituacaoResultPadrao(AelResultadosPadrao item) {
		try {

			if (item != null) {
				DominioSituacao situacao = item.getSituacao() == DominioSituacao.A ? DominioSituacao.I : DominioSituacao.A;
				item.setSituacao(situacao);
				// Submete o procedimento para ser atualizado
				this.cadastrosApoioExamesFacade.persistirResultadoPadrao(item);

			}

			// Apresenta as mensagens de acordo
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_RESULT_PADRAO");
			this.listarResultadosPadrao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private boolean isEditorRenderizado() {
		return this.parametroSelect != null && this.versaoLaudo.getSituacao().isConstrucao()
				&& !DominioObjetoVisual.TEXTO_LONGO.equals(this.parametroSelect.getObjetoVisual())
				&& !(this.parametroSelect.getCampoLaudo() != null && DominioTipoCampoCampoLaudo.E.equals(this.parametroSelect.getCampoLaudo().getTipoCampo()));
	}

	// --[GETTERS AND SETTERS]
	public AelParametroCamposLaudo getParametroSelect() {
		return parametroSelect;
	}

	public void setParametroSelect(AelParametroCamposLaudo parametroSelect) {
		this.parametroSelect = parametroSelect;
	}

	public Map<String, AelParametroCamposLaudo> getParametros() {
		return parametros;
	}

	public void setParametros(Map<String, AelParametroCamposLaudo> parametros) {
		this.parametros = parametros;
	}

	public Integer getCounterObjs() {
		return counterObjs;
	}

	public void setCounterObjs(Integer counterObjs) {
		this.counterObjs = counterObjs;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getFrameCache() {
		return frameCache;
	}

	public void setFrameCache(String frameCache) {
		this.frameCache = frameCache;
	}

	public List<AelParametroCamposLaudo> getParametrosExcluidos() {
		return parametrosExcluidos;
	}

	public void setParametrosExcluidos(List<AelParametroCamposLaudo> parametrosExcluidos) {
		this.parametrosExcluidos = parametrosExcluidos;
	}

	public String getComponentPosY() {
		return componentPosY;
	}

	public void setComponentPosY(String componentPosY) {
		this.componentPosY = componentPosY;
	}

	public String getComponentPosX() {
		return componentPosX;
	}

	public void setComponentPosX(String componentPosX) {
		this.componentPosX = componentPosX;
	}

	public AelVersaoLaudo getVersaoLaudo() {
		return versaoLaudo;
	}

	/**
	 * Setado na tela de pesquisaMascarasLaudos.seam
	 * 
	 * @param versaoLaudo
	 */
	public void setVersaoLaudo(AelVersaoLaudo versaoLaudo) {
		this.versaoLaudo = versaoLaudo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public boolean isExibirPdf() {
		return exibirPdf;
	}

	public void setExibirPdf(boolean exibirPdf) {
		this.exibirPdf = exibirPdf;
	}

	public List<CampoLaudoUtilizadoDesenhoMascaraVO> getListaCamposLaudoUtilizadosDesenhoMascara() {
		return listaCamposLaudoUtilizadosDesenhoMascara;
	}

	public void setListaCamposLaudoUtilizadosDesenhoMascara(
			List<CampoLaudoUtilizadoDesenhoMascaraVO> listaCamposLaudoUtilizadosDesenhoMascara) {
		this.listaCamposLaudoUtilizadosDesenhoMascara = listaCamposLaudoUtilizadosDesenhoMascara;
	}

	public DominioTipoRelacionamento getTipoRelacionamento() {
		return tipoRelacionamento;
	}

	public void setTipoRelacionamento(DominioTipoRelacionamento tipoRelacionamento) {
		this.tipoRelacionamento = tipoRelacionamento;
	}

	public String getTipoRelac() {
		return tipoRelac;
	}

	public void setTipoRelac(String tipoRelac) {
		this.tipoRelac = tipoRelac;
	}

	public String getResultadoValidacaoFormula() {
		return resultadoValidacaoFormula;
	}

	public void setResultadoValidacaoFormula(String resultadoValidacaoFormula) {
		this.resultadoValidacaoFormula = resultadoValidacaoFormula;
	}

	public List<VariaveisValorFormulaMascaraExameVO> getVariaveisFormula() {
		return variaveisFormula;
	}

	public void setVariaveisFormula(List<VariaveisValorFormulaMascaraExameVO> variaveisFormula) {
		this.variaveisFormula = variaveisFormula;
	}

	public void setTextoCampo(String textoCampo) {
		this.textoCampo = textoCampo;
	}

	public void setStyleCampo(String styleCampo) {
		this.styleCampo = styleCampo;
	}

	public Short getNovaVersaoUnfSeq() {
		return novaVersaoUnfSeq;
	}

	public void setNovaVersaoUnfSeq(Short novaVersaoUnfSeq) {
		this.novaVersaoUnfSeq = novaVersaoUnfSeq;
	}

	public DominioSituacaoVersaoLaudo getNovaVersaoSituacao() {
		return novaVersaoSituacao;
	}

	public void setNovaVersaoSituacao(DominioSituacaoVersaoLaudo novaVersaoSituacao) {
		this.novaVersaoSituacao = novaVersaoSituacao;
	}

	public String getNovaVersaoNomeDesenho() {
		return novaVersaoNomeDesenho;
	}

	public void setNovaVersaoNomeDesenho(String novaVersaoNomeDesenho) {
		this.novaVersaoNomeDesenho = novaVersaoNomeDesenho;
	}

	public Integer getNovaVersaoPesquisaPaginada() {
		return novaVersaoPesquisaPaginada;
	}

	public void setNovaVersaoPesquisaPaginada(Integer novaVersaoPesquisaPaginada) {
		this.novaVersaoPesquisaPaginada = novaVersaoPesquisaPaginada;
	}

	public boolean isVoltouDesenhoNovaVersaoMascara() {
		return voltouDesenhoNovaVersaoMascara;
	}

	public void setVoltouDesenhoNovaVersaoMascara(boolean voltouDesenhoNovaVersaoMascara) {
		this.voltouDesenhoNovaVersaoMascara = voltouDesenhoNovaVersaoMascara;
	}

	public String getNovaVersaoExaSigla() {
		return novaVersaoExaSigla;
	}

	public void setNovaVersaoExaSigla(String novaVersaoExaSigla) {
		this.novaVersaoExaSigla = novaVersaoExaSigla;
	}

	public Integer getNovaVersaoManSeq() {
		return novaVersaoManSeq;
	}

	public void setNovaVersaoManSeq(Integer novaVersaoManSeq) {
		this.novaVersaoManSeq = novaVersaoManSeq;
	}

	public Boolean getEmCriacao() {
		return emCriacao;
	}

	public void setEmCriacao(Boolean emCriacao) {
		this.emCriacao = emCriacao;
	}

	public OutputPanel getPanelFrameComponent() {
		return panelFrameComponent;
	}

	public void setPanelFrameComponent(OutputPanel panelFrameComponent) {
		this.panelFrameComponent = panelFrameComponent;
	}

	public List<AelResultadosPadrao> getListaResultadoPadrao() {
		return listaResultadoPadrao;
	}

	public void setListaResultadoPadrao(List<AelResultadosPadrao> listaResultadoPadrao) {
		this.listaResultadoPadrao = listaResultadoPadrao;
	}

	public ConsultarResultadosNotaAdicionalController getConsultarResultadosNotaAdicionalController() {
		return consultarResultadosNotaAdicionalController;
	}

	public void setConsultarResultadosNotaAdicionalController(
			ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController) {
		this.consultarResultadosNotaAdicionalController = consultarResultadosNotaAdicionalController;
	}

	public boolean isPendenciaGravacao() {
		return pendenciaGravacao;
	}

	public void setPendenciaGravacao(boolean pendenciaGravacao) {
		this.pendenciaGravacao = pendenciaGravacao;
	}
}