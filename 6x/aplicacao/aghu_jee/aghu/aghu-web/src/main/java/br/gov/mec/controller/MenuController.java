package br.gov.mec.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.aplicacao.action.IntegracaoModulosStartUp;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Dashboard;
import br.gov.mec.aghu.casca.model.Favorito;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.casca.model.Protocolo;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.casca.vo.MenuVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SessionAttributes;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * Classe controladora responsavel pelos menus e favoritos da casca.
 * 
 * @author Cristiano Quadros
 * 
 */
@SessionScoped
@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class MenuController extends ActionController {
	
	private static Properties properties = getProps();
	private static Properties getProps() {		
		Properties prop = new Properties();		
		
			try {
				InputStream prorpetiesStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("app-parameters.properties");
				prop.load(prorpetiesStream);
			} catch (IOException e) {
				LOG.error("Não carregou arquivo de propriedades: ");
			}
			
		return prop;
	}
	
	private static final String PANEL = "panel_";

	private static final long serialVersionUID = -1877518006272597470L;
	
	private static final Log LOG = LogFactory.getLog(MenuController.class);
	private final String FUNC_ADD_TAB="tab.addTab";
    public static final int DEFAULT_COLUMN_COUNT = 8;
    
	private final String SUFFIX_ORACLE_WEBFORMS=".fmx";
	private final String CENTRAL_PENDENCIAS = "centraldependencias";
	//private final String DOCUMENTACAO = "documentacao";
	private final String INFORMACOES_USO = "informacoesuso";
	private final String INFORMACOES_USO_MODAL = "informacoesusoWG";
	private final String OK_JA_ENTENDI = "COMO_UTILIZAR_OK_JA_ENTIDI";
	
	
	
	
	/**
	 * Identifica na URL as aplicações web externas que recebem token de autenticação.
	 * A URL deve conter um parâmetro com esse nome e o valor pode ser vazio.
	 */
	private static final String SUFFIX_WEB_APP_TOKEN = "webapptoken";
	private final String FUNC_ABRIR_JANELA_MODAL="abrirOracleWebFormsModal";
	//private final String FUNC_ABRIR_JANELA_NAO_MODAL="abrirNovaJanela";
	private final String FUNC_ABRIR_JANELA_AGHU5_MODAL="abrirAGHUExternoModal";
	
	/**
	 * Identifica o nome da Aplicação AGHU versão 5 para uso concomitante em ambiente
	 * de produção junto com uma versão superior de AGHU (vide tarefa #45444).
	 * OBS1: A constante abaixo representa o atributo Aplicacao.nome
	 * OBS2: Em ambientes há apenas uma versão de AGHU esta solução pode ser desconsiderada
	 * OBS3: Caso se deseje mais flexibilidade em ambientes com mais de uma versão de AGHU 
	 * rodando, sugere-se transformar a constante abaixo em um parâmetro de sistema
	 */
	private static final String NOME_APLICACAO_AGHU_VERSAO_5 = "AGHU5";
	private static final String NOME_APLICACAO_AGHU_VERSAO_6 = "AGHU6";

	private ICascaFacade cascaFacade = ServiceLocator.getBean(ICascaFacade.class, "aghu-casca");
	private IParametroFacade parametroFacade = ServiceLocator.getBean(IParametroFacade.class, "aghu-configuracao");
	private IAghuFacade aghuFacade = ServiceLocator.getBean(IAghuFacade.class, "aghu-configuracao");
	private IRegistroColaboradorFacade registroColaboradorFacade = ServiceLocator.getBean(IRegistroColaboradorFacade.class, "aghu-registrocolaborador");
	
	private String cacheTab;
	private Boolean ativaPOL = false;
	private String hostName;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private RapServidores servidor;
	private Usuario usuario;
	private String prontuarioContextoSelecionado;	
	private Menu menuPesquisa;
	private List<Integer> favoritos = new ArrayList<>();
	private List<MenuVO> menusFavoritos = new ArrayList<>();
	private Map<Integer, MenuVO> mapDashboard;
	private List<MenuVO> listaMenus = new ArrayList<MenuVO>();
	private Map<Integer, String> masterStylesMap = new LinkedHashMap<>();
	private Integer menuFavoritoId;
	private String banco;
	private String linkSobre;
	private Date dataUltimoAcesso;
	private boolean okJaEntendi;
	
	private DashboardModel dashModel;
	
	@Inject
	private IntegracaoModulosStartUp integracaoModulosStartUp;
	
	private Set<String> conjuntoModulosAtivos;
	
	private String autoCompletePref = "";

	private String menuSelectedName= "";
	
	
	@PostConstruct
	public void init() {
		conjuntoModulosAtivos = integracaoModulosStartUp.getModulosAtivos();
		
		sdf=new SimpleDateFormat("dd/MM/yyyy  HH:mm");
		hostName=FacesContext.getCurrentInstance().getExternalContext().getContextName().toLowerCase();
		try {
			//servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			servidor = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e1) {
			LOG.error("Não foi possivel carregar servidor logado!");
		}
		usuario = cascaFacade.obterUsuario(obterLoginUsuarioLogado());
		mapDashboard = new LinkedHashMap<>();
		
		if(aghuFacade.isHCPA()) {
			if(this.parametroFacade.verificarExisteAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB)) {
				AghParametros aghParametrosBanco=null;
				try {
					aghParametrosBanco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
				} catch (ApplicationBusinessException e) {
					LOG.error("Não carregou parâmetro P_BANCO_AGHU_AGHWEB");
				}
				banco = aghParametrosBanco.getVlrTexto();
			}
		}
		obterDataUltimoAcessoUsuario();		
		criarMenus();
		LOG.debug("Menus Criados! ");
		
		buidDashboard();
		
		// Nao apresentar esta modal na versão 6.3.x
		//exibirInformacoesUso();
	}

	
	private void criarMenus()  {
		List<Menu> menuList = cascaFacade.recuperarMenus();
		List<Integer> menusValidos = cascaFacade.recuperarMenusValidos(this.obterLoginUsuarioLogado(), conjuntoModulosAtivos);
		
		List<Menu> submenuList = null;
		try {
			submenuList = cascaFacade.recuperarSubMenusValidos(false);
		} catch (Exception e) {
			// realiza o evict da region de cache que guardam os menus.
			// teoricamente este erro ocorre somente quando rodamos o menu
			// contra uma base com o servidor no ar
			LOG.info("Ocorreu exceção ao buscar submenus, vou pegar do banco novamente! ");
			submenuList = cascaFacade.recuperarSubMenusValidos(true);
		}
		
		favoritos = cascaFacade.obterIdMenuFavoritos(this.obterLoginUsuarioLogado());
		Map<Integer, MenuVO> storeItensMap = new HashMap<Integer, MenuVO>();

		for (Menu menu : menuList) {
			
			if (!menusValidos.contains(menu.getId())) {
				continue;
			}
			
			if (!ativaPOL && menu.getUrl().contains("arvorePOL")) {
				LOG.debug("Renderiza POL-Prontuário On-Line");
				if (favoritos.contains(menu.getId())) {
					favoritos.remove(menu.getId());
				}
				if (menusValidos.contains(menu.getId())) {				
					ativaPOL = true;
				}	
			} else {
				if (menu.getUrl().contains("/sobre")) {
					linkSobre = urlTab(menu);
				}				
				montarArvore(menu, submenuList, menusValidos, storeItensMap, 0);
			}	
		}
		
		Collections.sort(listaMenus, uiComparator);
	}
	
	public String getBaseUrlDocumentacao() {
		return cascaFacade.getBaseUrlDocumentacao();
	}
	
	

	private String createId(Integer id, Integer ordem){
		return "i"+ordem + "_" + id;
	}	
	
	//Ordenador do Menu
	private Comparator<MenuVO> uiComparator = new Comparator<MenuVO>() {
		@Override
		public int compare(MenuVO o1, MenuVO o2) {
			if (o1.getIdStr()==null || o2.getIdStr()==null){
				return 0;
			}
			String [] split1 = o1.getIdStr().split("_");
			String [] split2 = o2.getIdStr().split("_");
			if (split1==null || split1.length < 2 || split2==null || split2.length < 2){
				return 0;
			}
			Integer ordem1 = Integer.valueOf(split1[0].substring(1));
			Integer id1 = Integer.valueOf(split1[1]);
			Integer ordem2 = Integer.valueOf(split2[0].substring(1));
			Integer id2 = Integer.valueOf(split2[1]);
			if (ordem1 < ordem2){
				return -1;
			}else if (ordem1 == ordem2 && id1 < id2){
				return 0;
			}else{
				return 1;
			}
		}
	};	
	
	
	/**
	 * Ajusta o tempo de sessão do usuário de acordo com os valores em banco.
	 * 
	 * @param usuario
	 * @param session
	 */
	private void obterDataUltimoAcessoUsuario() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		dataUltimoAcesso = (Date) session.getAttribute(SessionAttributes.DATA_ULTIMO_ACESSO.toString());
	}		
	
	
	public void openTabMenu() throws ApplicationBusinessException{
		RequestContext rc = RequestContext.getCurrentInstance();
		if(menuPesquisa != null && !menuSelectedName.equals(menuPesquisa.getNome())) {
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
			aghuFacade.gravarPesquisaMenuLog(autoCompletePref, menuPesquisa.getUrl(), servidorLogado);
			menuSelectedName = menuPesquisa.getNome();
			autoCompletePref = "";
		}

	    rc.execute(urlTab(menuPesquisa));
	}
	
	private String doGetExternalContextPath(String contextPath, Menu menu) {
		if (menu != null) {
			Aplicacao aplicacao = menu.getAplicacao();
			if (aplicacao != null && Boolean.TRUE.equals(aplicacao.getExterno())) {
				StringBuffer sb = new StringBuffer();

				Protocolo protocolo = aplicacao.getProtocolo();
				if (protocolo == null) {
					protocolo = Protocolo.HTTP;
				}

				sb.append(protocolo.getDescricao())
				.append("://")
				.append(aplicacao.getServidor());

				Integer porta = aplicacao.getPorta();
				if (porta != null) {
					sb.append(':');
					sb.append(porta);
				}
				sb.append('/').append(aplicacao.getContexto());
				return sb.toString();
			}
		}
		
		return null;
	}

	
	public String urlTab(Menu menu) {
		if (menu == null) {
			return "";
		}
		
		String contextPath = null;

		if (properties != null) {
			contextPath = "/" + properties.getProperty("app_context");
		} else {
			contextPath =  "/" + hostName;
		}	
		
		String url = getUrl(contextPath, menu);
		
		if (url != null && url.contains(SUFFIX_WEB_APP_TOKEN)) {
			// função javascript que será chamada para abrir aba e 
			// carregar página de aplicação externa com token
			String template = "addTabToken('%1$s', '%2$s', '%3$s', '%4$s')";
			return String.format(template, menu.getId(), menu.getNome(), url, menu.getClasseIcone());
		}
		
		// Se o nome de uma aplicação começar com 'AGHU' indica que a URL deve ter um tratamento
		// especial para poder ser chamada em uma janela modal do browser (vide tarefa #45444).
		// Ficam neste caso aplicações da versão 5 do AGHU e mesmo aplicações da versão 6 ou 7 que
		// estejam em outro deploy porém devem ter SEMPRE o nome da aplicação = 'AGHU%' e o flag
		// externo=true indicando que é uma instalação do AGHU externo a onde roda este AGHU
		if (menu.getAplicacao().getNome().trim().toUpperCase().startsWith("AGHU") && menu.getAplicacao().getExterno()) {
			String urlTratadaAGHUExterno = tratarURLAGHUExterno(menu.getAplicacao(), url);

			if (LOG.isDebugEnabled()) {
				LOG.debug("URL de item de menu do AGHU externo: " + urlTratadaAGHUExterno);
			}
				
			return FUNC_ABRIR_JANELA_AGHU5_MODAL+"('"+urlTratadaAGHUExterno+ "',{parameters:{onClose:function(){}}});";
		}
		
		if (url != null && url.contains(SUFFIX_ORACLE_WEBFORMS)) {
			StringBuffer otherParams = new StringBuffer();
			// o token será gerado por uma chamada ajax
			
			// Este teste é para tratar URLs do AGHWeb que chamam relatórios
			if (url.contains("&otherparams=")) {
				String[] params = url.split("&otherparams=");
				if (params.length > 1) {
					url = params[0];
					otherParams.append(params[1]);
				}
			}
			
			if (banco != null) {
				if (otherParams.length() > 0) {
					otherParams.append('+');
				}
				otherParams.append("p_banco=");
				otherParams.append(banco);
			}
			
			return FUNC_ABRIR_JANELA_MODAL+"('"+url+ "',{parameters:{otherparams: '"+otherParams+"'},onClose:function(){}});";
		}		
		
		return FUNC_ADD_TAB+"('"+menu.getId()+"', '"+menu.getNome()+"','"+url+"','"+menu.getClasseIcone()+"', '1');";
		//return FUNC_ADD_TAB+"('"+menu.getId()+"', '"+menu.getNome()+"','"+url+ "?centralPendencia=true','"+menu.getClasseIcone()+"', '1');";
	}
	
	private String tratarURLAGHUExterno(Aplicacao aplicacao, String url) {
		// Se for um item de menu do AGHU versão 5 troca só o sufixo de .xhtml para .seam
		if (aplicacao.getNome().trim().toUpperCase().startsWith(NOME_APLICACAO_AGHU_VERSAO_5)) {
			url = url.replace(".xhtml", ".seam");
		}
		
		return url;
	}
	
	private MenuVO montarArvore(Menu menu, List<Menu> submenu, List<Integer> menusValidos, Map<Integer, MenuVO> store, int nivel) {
		MenuVO item = null;
		int nnivel = nivel + 1;
		
		if (menu.getUrl() == null) {
			item = getSub(menu, nnivel);			
		} else {
			boolean temAcesso=menusValidos.remove(menu.getId());
			item = getItem(menu, temAcesso);
		}
		
		if (menu.getMenuPai() != null) {
			boolean valid=true; 
			if (!store.containsKey(menu.getMenuPai().getId())) {
				int index = submenu.indexOf(menu.getMenuPai());
				if (index >= 0) {
					Menu pai = submenu.get(submenu.indexOf(menu.getMenuPai()));
					MenuVO itemArvoreMenu = montarArvore(pai, submenu, menusValidos, store, nnivel);
					itemArvoreMenu.setIdStr(createId(pai.getId(), pai.getOrdem()));
					store.put(menu.getMenuPai().getId(), itemArvoreMenu);
				} else {
					valid=false;
					LOG.debug("Menu Não Adicionado: " + menu.getMenuPai().getId());
				}
			}
			if (valid) {
				MenuVO pai = store.get(menu.getMenuPai().getId());
				item.setMasterClass(pai.getMasterClass());
				masterStylesMap.put(item.getId(), item.getMasterClass());
				if (item.getId() != null && favoritos.contains(menu.getId())) {
					item.setFavorito(true); 
					mapDashboard.put(item.getId(), item);
				}				
				pai.add(item);
				if (pai.getMenus().size() > 1) {
					List<MenuVO> elements = pai.getMenus();
					Collections.sort(elements, uiComparator);
					pai.setMenus(elements);
				}	
			}	
		} else {
			if (!store.containsKey(menu.getId()) && item != null) {
				item.setMasterClass(menu.getClasseIcone()+"-menu");
				store.put(menu.getId(), (MenuVO) item);
				masterStylesMap.put(item.getId(), item.getMasterClass());
				listaMenus.add(item);
			}
		}
		return item;
	}


	private MenuVO getItem(Menu menu, boolean temAcesso) {
		MenuVO item = new MenuVO(menu.getNome());
		item.setIdStr(createId(menu.getId(), menu.getOrdem()));
		item.setDisabled(!temAcesso);
		item.setIdPai(menu.getMenuPai().getId());
		if (temAcesso) {
			item.setId(menu.getId());
			//item.setStyleClass("casca-menu-itens");
			item.setStyleClass(menu.getClasseIcone());
			item.setUrl(urlTab(menu));
		} else {
			item.setStyleClass("casca-menu-itens-off");
			//item.setStyleClass(menu.getClasseIcone());
			item.setUrl("return false;");
		}
		return item;
	}
	
	
	private MenuVO getSub(Menu menu, int nivel) {
		MenuVO sub = new MenuVO(menu.getNome());
		sub.setIdStr(createId(menu.getId(), menu.getOrdem()));
		//sub.setStyleClass("casca-menu-subs");
		sub.setStyleClass(menu.getClasseIcone());
		//sub.setNivelMenu(nivel);
		return sub;
	}	
	

	private String getUrl(String contextPath, Menu menu) {
		String pathExternal = this.doGetExternalContextPath(contextPath, menu);
		
		StringBuffer urlResult = new StringBuffer();
		if (menu.getUrl() != null && !menu.getUrl().isEmpty()) {
			if (pathExternal == null) {
				urlResult.append(contextPath).append("/pages").append(menu.getUrl());
			} else {
				urlResult.append(pathExternal != null ? pathExternal : "");
				
				// OBS: If abaixo pode ser futuramente removido quando não houver mais necessidade
				// do AGHUse chamar telas do AGHU com JEE6 (em que há deploys com contextos diferentes)
				if (menu.getAplicacao() != null && menu.getAplicacao().getNome().trim().toUpperCase().startsWith(NOME_APLICACAO_AGHU_VERSAO_6)) {
					urlResult.append("/pages");
				}
				
				if (menu.getUrl().startsWith("/")) {
					urlResult.append(menu.getUrl());
				} else {
					urlResult.append('/').append(menu.getUrl());
				}
			}
		}
		
		return urlResult.toString();
	}
	

	public List<Menu> autocompletePesquisaMenu(String suggest) throws ApplicationBusinessException {
		if(suggest != null && !suggest.isEmpty() && !autoCompletePref.equals(suggest)) {
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
			aghuFacade.gravarPesquisaMenuLog(suggest, null, servidorLogado);
			autoCompletePref = suggest;
			menuSelectedName = "";
		}
		return cascaFacade.recuperarMenusValidos(obterLoginUsuarioLogado(), suggest, conjuntoModulosAtivos);
	}

	
	public String getMsgSessao(){
		FacesContext context = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);

		StringBuffer msg = new StringBuffer(sdf.format(new Date()));
		msg.append(" - Tempo Restante(sessão): ").append(session.getMaxInactiveInterval()).append('-').append(sdf.format(new Date(session.getLastAccessedTime())));
		return msg.toString();
	}

	//--[DASHBOARD]
	
	private void buidDashboard(){
        dashModel = new DefaultDashboardModel();
        for( int i = 0, n = DEFAULT_COLUMN_COUNT; i < n; i++ ) {
            DashboardColumn column = new DefaultDashboardColumn();
            dashModel.addColumn(column);
        }
        
        List<Dashboard> listaDashboard = cascaFacade.obterDashBoard(this.obterLoginUsuarioLogado());
        if(listaDashboard != null && !listaDashboard.isEmpty()) {
        	List<String> listaModais = new ArrayList<String>();
	        for(Dashboard dash : listaDashboard) {
	        	if(dash.getFavorito() != null) {
	        		MenuVO menuVO = mapDashboard.get(dash.getFavorito().getMenu().getId());
		        	if(menuVO != null) {
		        		menusFavoritos.add(menuVO);
						DashboardColumn column = dashModel.getColumn(dash.getOrdem() % DEFAULT_COLUMN_COUNT);
						column.addWidget(PANEL + menuVO.getId());
		        	}
	        	} else {
	        		DashboardColumn column = dashModel.getColumn(dash.getOrdem() % DEFAULT_COLUMN_COUNT);
	        		column.addWidget(dash.getModal());
	        		listaModais.add(dash.getModal());
	        	}
	        }
	        
	        copiarFavoritosParaDashBoard(listaDashboard);
	        
	        if(!listaModais.contains(CENTRAL_PENDENCIAS)) {
	        	DashboardColumn column = dashModel.getColumn((favoritos.size())%DEFAULT_COLUMN_COUNT);
	        	column.addWidget(CENTRAL_PENDENCIAS);
	        }
	        if(!listaModais.contains(INFORMACOES_USO)) {
	        	DashboardColumn column = dashModel.getColumn((favoritos.size()+1)%DEFAULT_COLUMN_COUNT);
	        	column.addWidget(INFORMACOES_USO);
	        }
        }
        else {
        	popularDashboardComFavoritos(dashModel);
        }
	}
	
	protected void copiarFavoritosParaDashBoard(List<Dashboard> listaDashboard) {
        List<Favorito> listaFavoritos = new ArrayList<Favorito>();
        for(MenuVO vo : mapDashboard.values()) {
        	Favorito fav = cascaFacade.obterFavoritoIdMenu(this.obterLoginUsuarioLogado(), vo.getId());
        	listaFavoritos.add(fav);
        }
        if(!listaFavoritos.isEmpty()) {
	        for(Favorito fav : listaFavoritos) {
	        	MenuVO menuVO = mapDashboard.get(fav.getMenu().getId());
	        	if(menuVO != null && !isFavoritoNaDashBoard(listaDashboard, fav)) {
		        	menusFavoritos.add(menuVO);
					DashboardColumn column = dashModel.getColumn(fav.getOrdem() % DEFAULT_COLUMN_COUNT);
					column.addWidget(PANEL + menuVO.getId());
	        	}
	        }
        }
	}

	protected Boolean isFavoritoNaDashBoard(List<Dashboard> listaDashboard, Favorito favorito) {
		for(Dashboard dash : listaDashboard) {
			if(dash.getFavorito() != null && dash.getFavorito().getId().equals(favorito.getId())) {
				return true;
			}
		}
		
		return false;
	}
	
	protected void popularDashboardComFavoritos(DashboardModel dashModel) {
        List<Favorito> listaFavoritos = new ArrayList<Favorito>();
        for(MenuVO vo : mapDashboard.values()) {
        	Favorito fav = cascaFacade.obterFavoritoIdMenu(this.obterLoginUsuarioLogado(), vo.getId());
        	listaFavoritos.add(fav);
        }

        if(!listaFavoritos.isEmpty()) {
	        BeanComparator ordemComparator = new BeanComparator(Favorito.Fields.ORDEM.toString());
	        Collections.sort(listaFavoritos, ordemComparator);
	        for(Favorito fav : listaFavoritos) {
	        	MenuVO menuVO = mapDashboard.get(fav.getMenu().getId());
	        	if(menuVO != null) {
		        	menusFavoritos.add(menuVO);
					DashboardColumn column = dashModel.getColumn(fav.getOrdem() % DEFAULT_COLUMN_COUNT);
					column.addWidget(PANEL + menuVO.getId());
	        	}
	        }
        }        

		DashboardColumn column = dashModel.getColumn((favoritos.size())%DEFAULT_COLUMN_COUNT);
		column.addWidget(CENTRAL_PENDENCIAS);
		column = dashModel.getColumn((favoritos.size()+1)%DEFAULT_COLUMN_COUNT);
		column.addWidget(INFORMACOES_USO);
		
		dsHandleReorder(null);
	}
	
	private void addDash(){
		if (menuFavoritoId!=null){
			MenuVO menuVO=null;
			try {
				menuVO = getItem(cascaFacade.obterMenu(menuFavoritoId), true);
				menuVO.setMasterClass(masterStylesMap.get(menuVO.getId()));
		        favoritos.add(menuFavoritoId);
		        mapDashboard.put(menuFavoritoId, menuVO);
				menusFavoritos.add(menuVO);
				DashboardColumn column = dashModel.getColumn((favoritos.size())%DEFAULT_COLUMN_COUNT);
		        column.addWidget(PANEL + menuVO.getId());
			} catch (ApplicationBusinessException e) {
				LOG.error("NÃ£o encontrou menu para adicionar aos favoritos!");
			}
		}  
	}

	
	private void removeDash(){
		if (menuFavoritoId!=null){
			MenuVO menuVO = mapDashboard.get(menuFavoritoId);
			menuVO.setFavorito(false);
			menusFavoritos.remove(menuVO);
			for (DashboardColumn column : dashModel.getColumns()){
				if (column.getWidgets().contains(PANEL + menuVO.getId())){
					column.removeWidget(PANEL + menuVO.getId());
				}	
			}
		}	
	}
	
	
    public void dsHandleReorder(DashboardReorderEvent event) {
    	int coluna = 0, linha = 0;
    	for(DashboardColumn column : dashModel.getColumns()) {
    		linha = 0;
    		for(String widg : column.getWidgets()) {
    			if(StringUtils.startsWithIgnoreCase(widg, PANEL)) {
    				String menuId = StringUtils.remove(widg, PANEL);
    				Integer ordem = (linha * DEFAULT_COLUMN_COUNT) + coluna;
    				try {
						cascaFacade.atualizarDashdoard(Integer.valueOf(menuId), null,  this.obterLoginUsuarioLogado(), ordem);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
    			} else {
    				Integer ordem = (linha * DEFAULT_COLUMN_COUNT) + coluna;
    				try {
						cascaFacade.atualizarDashdoard(null, widg,  this.obterLoginUsuarioLogado(), ordem);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
    			}
    			linha++;
    		}
    		coluna++;
    	}
    }	
	
	public void addFavorito() {		
		if (!favoritos.contains(menuFavoritoId)) {
			try {
				cascaFacade.criarFavorito(cascaFacade.obterMenu(menuFavoritoId), obterLoginUsuarioLogado(), favoritos.size()+1);
				cascaFacade.atualizarDashdoard(menuFavoritoId, null,  this.obterLoginUsuarioLogado(), favoritos.size()+1);
				addDash();
				menuFavoritoId=null;
			} catch (BaseException e) {
				LOG.error(e.getMessage());
			}
		}
	}	

	public void removeFavorito(){
		if (menuFavoritoId!=null){
			cascaFacade.excluirDashboard(obterLoginUsuarioLogado(), menuFavoritoId);
			cascaFacade.excluirFavoritoIdMenu(obterLoginUsuarioLogado(), menuFavoritoId);
			favoritos.remove(menuFavoritoId);
			removeDash();
			menuFavoritoId=null;
		}	
	}	

	protected void exibirInformacoesUso() {
		Cookie cookie = getCookie(OK_JA_ENTENDI);
		okJaEntendi = cookie != null ? Boolean.parseBoolean(cookie.getValue()) : false;
		if(!okJaEntendi) {
			openDialog(INFORMACOES_USO_MODAL);
		}
	}
	
	public void okJaEntendiListner() {
		setCookie(OK_JA_ENTENDI, String.valueOf(Boolean.TRUE.equals(okJaEntendi)), Integer.MAX_VALUE);
	}

	public Cookie getCookie(String name) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		Cookie cookie = null;
		
		Cookie[] userCookies = request.getCookies();
		if (userCookies != null && userCookies.length > 0 ) {
		    for (int i = 0; i < userCookies.length; i++) {
		        if (userCookies[i].getName().equals(name)) {
		            cookie = userCookies[i];
		            return cookie;
		        }
		    }
		}
		return null;
	}
 	
	public void setCookie(String name, String value, int expiry) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		Cookie cookie = getCookie(name);
		
		if (cookie != null) {
		    cookie.setValue(value);
		} else {
		    cookie = new Cookie(name, value);
		    cookie.setPath(request.getContextPath());
		}
		
		cookie.setMaxAge(expiry);
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		response.addCookie(cookie);
	}
	
	public String getCacheTab() {
		return cacheTab;
	}

	public void setCacheTab(String cacheTab) {
		this.cacheTab = cacheTab;
	}

	public String getProntuarioContextoSelecionado() {
		return prontuarioContextoSelecionado;
	}

	public void setProntuarioContextoSelecionado(
			String prontuarioContextoSelecionado) {
		this.prontuarioContextoSelecionado = prontuarioContextoSelecionado;
	}

	public Menu getMenuPesquisa() {
		return menuPesquisa;
	}

	public void setMenuPesquisa(Menu menuPesquisa) {
		this.menuPesquisa = menuPesquisa;
	}


	public List<MenuVO> getListaMenus() {
		return listaMenus;
	}


	public void setListaMenus(List<MenuVO> listaMenus) {
		this.listaMenus = listaMenus;
	}


	public Integer getMenuFavoritoId() {
		return menuFavoritoId;
	}


	public void setMenuFavoritoId(Integer menuFavoritoId) {
		this.menuFavoritoId = menuFavoritoId;
	}


	public DashboardModel getDashModel() {
		return dashModel;
	}


	public void setDashModel(DashboardModel dashModel) {
		this.dashModel = dashModel;
	}


	public List<MenuVO> getMenusFavoritos() {
		return menusFavoritos;
	}


	public void setMenusFavoritos(List<MenuVO> menusFavoritos) {
		this.menusFavoritos = menusFavoritos;
	}


	public Boolean getAtivaPOL() {
		return ativaPOL;
	}


	public void setAtivaPOL(Boolean ativaPOL) {
		this.ativaPOL = ativaPOL;
	}


	public String getLinkSobre() {
		return linkSobre;
	}


	public void setLinkSobre(String linkSobre) {
		this.linkSobre = linkSobre;
	}


	public Date getDataUltimoAcesso() {
		return dataUltimoAcesso;
	}


	public void setDataUltimoAcesso(Date dataUltimoAcesso) {
		this.dataUltimoAcesso = dataUltimoAcesso;
	}


	public RapServidores getServidor() {
		return servidor;
	}


	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}


	public Usuario getUsuario() {
		return usuario;
	}


	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}


	public boolean isOkJaEntendi() {
		return okJaEntendi;
	}
	public void setOkJaEntendi(boolean okJaEntendi) {
		this.okJaEntendi = okJaEntendi;
	}
	public String getAutoCompletePref() {
		return autoCompletePref;
	}
	public void setAutoCompletePref(String autoCompletePref) {
		this.autoCompletePref = autoCompletePref;
	}
	public String getMenuSelectedName() {
		return menuSelectedName;
	}
	public void setMenuSelectedName(String menuSelectedName) {
		this.menuSelectedName = menuSelectedName;
	}
}