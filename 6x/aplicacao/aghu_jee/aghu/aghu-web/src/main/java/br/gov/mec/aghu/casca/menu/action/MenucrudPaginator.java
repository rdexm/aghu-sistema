package br.gov.mec.aghu.casca.menu.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * Classe responsavel por realizar a paginacao dos menus encontrados.
 * 
 * @author kamila.nogueira
 * 
 */
public class MenucrudPaginator extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 9068719268102765729L;
	
	private final String REDIRECIONA_CADASTRAR_MENU = "cadastrarMenu";

	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<Menu> dataModel;

	private Boolean exibirBotaoIncluirMenu;
	
	private Menu menuFiltro;
	
	private Menu menuSelecionado;


	@PostConstruct
	public void init(){
		this.begin(conversation);
		menuFiltro = new Menu();
		exibirBotaoIncluirMenu = false;
	}
	
	public String cadastrarNovo() {
		return REDIRECIONA_CADASTRAR_MENU;
	}
	
	
	public void limparPesquisa() {
		init();
		this.dataModel.limparPesquisa();
	}
	
	
	public List<Menu> pesquisarMenuPorNome(String param) {
		return this.cascaFacade.pesquisarMenuPorNomeEId((String) param);
	}
	
	public List<Aplicacao> pesquisarAplicacaoPorNome(String param) {
		return cascaFacade.pesquisarAplicacaoPorNome((String) param);
	}
	
	@Override
	public Long recuperarCount() {
		Integer idAplicacao = null;
		if (menuFiltro.getAplicacao() != null){
		 idAplicacao = menuFiltro.getAplicacao().getId();
		}
		
		Integer idMenuPai = null;
		if (menuFiltro.getMenuPai() != null){
			idMenuPai = menuFiltro.getMenuPai().getId();
		}
		return cascaFacade.pesquisarMenucrudCount(menuFiltro.getNome(), menuFiltro.getUrl(),idAplicacao	, idMenuPai);
	}


	@Override	
	public List<Menu> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

	    Integer idAplicacao = null;
		if (menuFiltro.getAplicacao() != null){
			idAplicacao = menuFiltro.getAplicacao().getId();
		}
		
		Integer idMenuPai = null;
		if (menuFiltro.getMenuPai() != null){
			idMenuPai = menuFiltro.getMenuPai().getId();
		}
		
		return cascaFacade.pesquisarMenu(menuFiltro.getNome(), menuFiltro.getUrl(),
					idAplicacao, idMenuPai, firstResult, maxResult, orderProperty, asc);
	}
	
	public void pesquisarMenu() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluirMenu = true;
		
	}
	
	public void deletarMenu() {
		try {
			cascaFacade.deletarMenu(menuSelecionado.getId());
			dataModel.reiniciarPaginator();			
			this.apresentarMsgNegocio(Severity.INFO, "CASCA_MENSAGEM_SUCESSO_EXCLUSAO_MENU");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}
	
	public String editar() {
		return REDIRECIONA_CADASTRAR_MENU;		
	}
	
	public void setDataModel(DynamicDataModel<Menu> dataModel) {
		this.dataModel = dataModel;
	}


	public Boolean getExibirBotaoIncluirMenu() {
		return exibirBotaoIncluirMenu;
	}

	public void setExibirBotaoIncluirMenu(boolean exibirBotaoIncluirMenu) {
		this.exibirBotaoIncluirMenu = exibirBotaoIncluirMenu;
	}
	
	
	public Menu getMenuPai() {
		return menuFiltro.getMenuPai();
	}

	public void setMenuPai(Menu menuPai) {
		menuFiltro.setMenuPai(menuPai);
	}
	
	public Aplicacao getAplicacao() {
		return menuFiltro.getAplicacao();
	}

	public void setAplicacao(Aplicacao aplicacao) {
		menuFiltro.setAplicacao(aplicacao);
	}

	public Menu getMenuSelecionado() {
		return menuSelecionado;
	}

	public void setMenuSelecionado(Menu menuSelecionado) {
		this.menuSelecionado = menuSelecionado;
	}

	public Menu getMenuFiltro() {
		return menuFiltro;
	}

	public void setMenuFiltro(Menu menuFiltro) {
		this.menuFiltro = menuFiltro;
	}

	public DynamicDataModel<Menu> getDataModel() {
		return dataModel;
	}

}
