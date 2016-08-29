package br.gov.mec.aghu.casca.business;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.dao.DashboardDAO;
import br.gov.mec.aghu.casca.dao.FavoritoDAO;
import br.gov.mec.aghu.casca.dao.MenuDAO;
import br.gov.mec.aghu.casca.model.Dashboard;
import br.gov.mec.aghu.casca.model.Favorito;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.casca.vo.MenuVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MenuON extends BaseBusiness {

    @EJB
    private UsuarioON usuarioON;

    private static final Log LOG = LogFactory.getLog(MenuON.class);

    @Override
    @Deprecated
    protected Log getLogger() {
	return LOG;
    }

    @Inject
    private MenuDAO menuDAO;

    @Inject
    private FavoritoDAO favoritoDAO;

    @Inject
    private DashboardDAO dashboardDAO;

    @EJB
    private IPermissionService permissionService;

    private static final long serialVersionUID = -7105967456303402782L;

    protected enum MenuONExceptionCode implements BusinessExceptionCode {
	CASCA_MENSAGEM_LOGIN_NAO_FORNECIDO
    }

    public MenuVO obterMenuPorUrl(final String url) {

	MenuVO result = null;
	List<Menu> menus = getMenuDAO().obterMenuPorURL(url);
	if (!menus.isEmpty()) {
	    try {
		result = this.montarMenuVO(menus.get(0));
	    } catch (UnsupportedEncodingException e) {
		logError("Erro ao buscar menu por URL: " + url, e);
	    }

	}
	return result;
    }

    public List<Favorito> obterFavoritos(String login) throws BaseException {
	if (login == null) {
	    throw new BaseException(MenuONExceptionCode.CASCA_MENSAGEM_LOGIN_NAO_FORNECIDO);
	}
	return getFavoritoDAO().obterFavoritos(login);
    }

    public List<Integer> obterIdMenuFavoritos(String login) {
	return getFavoritoDAO().obterIdMenuFavoritos(login);
    }

    public void atualizarDashdoard(Integer menuId, String modal, String login, Integer ordem) throws BaseException {
    	Dashboard dashboard = getDashboardDAO().obterDashboardPeloMenuOuModal(login, menuId, modal);
    	if(dashboard == null) {
    		dashboard = new Dashboard();
    		dashboard.setOrdem(ordem);
    		dashboard.setDataCriacao(new Date());
    		dashboard.setUsuario(getUsuarioON().obterUsuarioAtivo(login));
    		if(menuId != null) {
    			dashboard.setFavorito(getFavoritoDAO().obterFavoritoIdMenu(login, menuId));
    		} else {
    			dashboard.setModal(modal);
    		}
    		getDashboardDAO().persistir(dashboard);
    	} else {
    		dashboard.setOrdem(ordem);
    		dashboard.setDataAtualizacao(new Date());
    		getDashboardDAO().atualizar(dashboard);
    	}
    }
    
    public void atualizarPosicaoFavorito(Integer menuId, String login, Integer ordem) {
    	Favorito favorito = getFavoritoDAO().obterFavoritoIdMenu(login, menuId);
    	if(favorito != null) {
    		favorito.setOrdem(ordem);
    		favorito.setDataAtualizacao(new Date());
    		getFavoritoDAO().atualizar(favorito);
    	}
    }
    
    public Favorito criarFavorito(Menu menu, String login, Integer ordem) throws BaseException {
	if (login == null) {
	    return null;
	}
	Favorito favorito = new Favorito();
	favorito.setDataCriacao(new Date());
	favorito.setMenu(menu);
	favorito.setOrdem(ordem);
	favorito.setUsuario(getUsuarioON().obterUsuarioAtivo(login));

	getFavoritoDAO().persistir(favorito);
	getFavoritoDAO().flush();

	return favorito;
    }

    public void excluirDashboard(String usuarioLogado, Integer menuId) {
    	if (menuId != null) {
    	    Dashboard dash = getDashboardDAO().obterDashboardPeloMenuOuModal(usuarioLogado, menuId, null);
    	    if (dash != null) {
    	    	getDashboardDAO().remover(dash);
    	    }
    	}
    }
    
    public void excluirFavorito(Integer id) {
		if (id != null) {
		    getFavoritoDAO().removerPorId(id);
		}
    }

    public void excluirFavoritoIdMenu(String usuarioLogado, Integer id) {
	if (id != null) {
	    Favorito fav = getFavoritoDAO().obterFavoritoIdMenu(usuarioLogado, id);
	    getFavoritoDAO().remover(fav);
	}
    }

    /**
     * @return
     */
    protected MenuDAO getMenuDAO() {
	return menuDAO;
    }

    /**
     * @return
     */
    protected FavoritoDAO getFavoritoDAO() {
	return favoritoDAO;
    }

    protected DashboardDAO getDashboardDAO() {
    	return dashboardDAO;
    }
    
    /**
     * @author gandriotti
     * @param identity
     * @param menu
     * @return
     * @throws UnsupportedEncodingException
     */
    private MenuVO montarMenuVO(Menu menu) throws UnsupportedEncodingException {

	MenuVO result = null;

	if (menu.getAtivo()) {
	    // TODO Validar o que fazer quando o menu nao tem URL.
	    if ((menu.getUrl() == null)
		    || permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), this.getTargetString(menu), "render")) {
		result = this.criarMenuVO(menu);
	    }
	}

	return result;
    }

    public List<Integer> recuperarMenusValidos(String loginUser) {
	return getMenuDAO().recuperarMenusValidos(loginUser);
    }

    public List<Menu> recuperarMenusValidos(String loginUser, String nome, Set<String> conjuntoModulosAtivos) {
	// TODO: Consulta não funciona com mais de 2 String derivadas da
	// normalização
	// return getMenuDAO().recuperarMenusValidos(loginUser,
	// semiFonetiza(nome));
	return getMenuDAO().recuperarMenusValidos(loginUser, nome, conjuntoModulosAtivos);
    }

    public List<Integer> recuperarMenusValidos(String loginUser, Set<String> conjuntoModulosAtivos) {
	return getMenuDAO().recuperarMenusValidos(loginUser, conjuntoModulosAtivos);
    }

    @SuppressWarnings({"PMD.NPathComplexity","PMD.AvoidDuplicateLiterals"})
    public String semiFonetiza(String str) {
	StringBuilder builder = new StringBuilder(63);
	String val = str.toLowerCase();
	String separator = ",";
	builder.append(val);
	if (val.contains("cao")) {
	    builder.append(separator).append(val.replace("cao", "ção"));
	}
	if (val.contains("a")) {
	    builder.append(separator).append(val.replace("a", "á"));
	}
	if (val.contains("o")) {
	    builder.append(separator).append(val.replace("o", "ó"));
	}
	if (val.contains("i")) {
	    builder.append(separator).append(val.replace("i", "í"));
	}
	if (val.contains("çao")) {
	    builder.append(separator).append(val.replace("çao", "ção"));
	}
	if (val.contains("cão")) {
	    builder.append(separator).append(val.replace("cão", "ção"));
	}
	if (val.contains("ssao")) {
	    builder.append(separator).append(val.replace("ssao", "ssão"));
	}
	if (val.contains("sa")) {
	    builder.append(separator).append(val.replace("sa", "za"));
	}
	if (val.contains("za")) {
	    builder.append(separator).append(val.replace("za", "sa"));
	}
	if (val.contains("coe")) {
	    builder.append(separator).append(val.replace("coe", "çõe"));
	}
	if (val.contains("çoe")) {
	    builder.append(separator).append(val.replace("çoe", "çõe"));
	}
	if (val.contains("cõe")) {
	    builder.append(separator).append(val.replace("cõe", "çõe"));
	}
	if (val.contains("torio")) {
	    builder.append(separator).append(val.replace("torio", "tório"));
	}
	if (val.contains("odulos")) {
	    builder.append(separator).append(val.replace("odulos", "ódulos"));
	}
	if (val.contains("torio")) {
	    builder.append(separator).append(val.replace("torio", "tório"));
	}
	if (val.contains("ario")) {
	    builder.append(separator).append(val.replace("arios", "ário"));
	}
	if (val.contains("asico")) {
	    builder.append(separator).append(val.replace("asico", "ásico"));
	}
	if (val.contains("ario")) {
	    builder.append(separator).append(val.replace("ario", "ário"));
	}
	if (val.contains("orio")) {
	    builder.append(separator).append(val.replace("orio", "ório"));
	}

	return builder.toString();
    }

    /**
     * Monta a string com a aplicacao e componente para ser utilizada na
     * pesquisa de permissoes
     * 
     * @param menu
     *            O objeto Menu
     * @return uma string no formato [aplicacao:]componente
     */
    private String getTargetString(Menu menu) {
	return menu.getAplicacao() != null ? menu.getAplicacao().getNome() + ":" + menu.getUrl() : menu.getUrl();
    }

    /**
     * Cria a instance de MenuVO correspondente a instancia de Menu passado por
     * parametro
     * 
     * @param menu
     *            O objeto Menu
     * @return O objeto MenuVO correspondente
     * @throws UnsupportedEncodingException
     */
    private MenuVO criarMenuVO(Menu menu) throws UnsupportedEncodingException {
	MenuVO vo = new MenuVO();

	vo.setId(menu.getId());
	vo.setNome(menu.getNome());
	vo.setClasseIcone(menu.getClasseIcone());
	vo.setMenus(new ArrayList<MenuVO>());

	// Menu pai não contém url
	if (menu.getUrl() != null) {
	    StringBuffer urlTemp = new StringBuffer(menu.getUrl());
	    // Monta URL para aplicações, preparando para o redirecionamento com
	    // token. Links simples, como links para documentação ou sites
	    // externos que não são aplicações, não são tratados.
	    // if (menu.getAplicacao() != null) {
	    // TODO arquitetura: Ver Solução para URLConfig e ServletHttp

	    // Aplicacao app = menu.getAplicacao();
	    //
	    // urlTemp = new
	    // StringBuffer(URLConfig.montarURL(urlTemp.toString(), app));
	    // if (menu.getAplicacao().getExterno()) {
	    // urlTemp.append('&');
	    // } else {
	    // urlTemp.append('?');
	    // }
	    // urlTemp.append("cascaSessionId=").append(ServletContexts.instance().getRequest().getSession().getId());
	    // }

	    vo.setUrl(urlTemp.toString());
	}

	return vo;
    }

    protected UsuarioON getUsuarioON() {
	return usuarioON;
    }
}