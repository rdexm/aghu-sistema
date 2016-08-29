package br.gov.mec.aghu.casca.usuario.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class UsuarioIdentityManagerPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 568919570049669783L;

	@EJB
	private ICascaFacade cascaFacade;

	private String login;
	
	@Inject @Paginator
	private DynamicDataModel<Usuario> dataModel;
	
	//Armazenam sem uma nova pesquisa os usuarios existentes quando pesquisado por login
	private List<Usuario> usuariosIdentityStoreCache;
	private String loginPesquisaCache;

	public UsuarioIdentityManagerPaginatorController() {
		dataModel.setDefaultMaxRow(5);
	}

	@Override
	public Long recuperarCount() {
		List<Usuario> usuarios = recuperarListaDeUsuarios(login);
		
		return (long) usuarios.size();
	}

	@Override
	public List<Usuario> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<Usuario> usuarios = recuperarListaDeUsuarios(login);
		
		int max = firstResult + maxResult;
		if (max > usuarios.size()) {
			max = usuarios.size();
		}

		return usuarios.subList(firstResult, max);
	}
	
	private List<Usuario> recuperarListaDeUsuarios(String login) {
		if(loginPesquisaCache == null || !loginPesquisaCache.equals(login)) {
			usuariosIdentityStoreCache = cascaFacade.pesquisarUsuariosNaoCadastrados(0, null, login);
			loginPesquisaCache = login;
		}
		return usuariosIdentityStoreCache;
	}
	
	
	/**
	 * Realiza a chamada para a pesquisa de usuario, e sua paginacao.
	 */
	public void pesquisarUsuariosNaoCadastrados() {
		// getStatusMessages().clear();
		this.dataModel.reiniciarPaginator();

	}
	
	public void iniciarPesquisaNaoCadastrado() {
		this.login = null;
		this.dataModel.limparPesquisa();

	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	public void setCascaFacade(ICascaFacade c) {
		this.cascaFacade = c;
	}
	
	public void setDataModel(DynamicDataModel<Usuario> dataModel) {
		this.dataModel = dataModel;
	}

	public DynamicDataModel<Usuario> getDataModel() {
		return dataModel;
	}

	public void reiniciarPaginator() {
		this.dataModel.reiniciarPaginator();
		
	}

}
