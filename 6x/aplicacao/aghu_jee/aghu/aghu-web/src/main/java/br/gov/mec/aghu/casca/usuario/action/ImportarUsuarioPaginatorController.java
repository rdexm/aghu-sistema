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

public class ImportarUsuarioPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 7095145082290991013L;
	
	private final String REDIRECIONA_PESQUISAR_USUARIO = "pesquisarUsuarios";	
	private final String REDIRECIONA_CADASTRAR_USUARIO = "cadastrarUsuario";
	
	@EJB
	private ICascaFacade cascaFacade;	
	
	@Inject
	private UsuarioController usuarioControler;
	
	@Inject @Paginator
	private DynamicDataModel<Usuario> dataModel;	
	private String loginFiltro;	
	private Usuario usuarioSelecionado;	
	
	
	public void pesquisar(){
		this.dataModel.reiniciarPaginator();
	}
	
	
	public void limparPesquisa(){
		this.loginFiltro = null;
		this.dataModel.limparPesquisa();
	}
	
	
	public String cancelar(){
		return REDIRECIONA_PESQUISAR_USUARIO;
	}

	@Override
	public List<Usuario> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return cascaFacade.pesquisarUsuariosNaoCadastrados(firstResult, maxResult, loginFiltro);
	}

	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarUsuariosNaoCadastradosCount(loginFiltro);
	}
	
	public String adicionarUsuario(){
		usuarioControler.setUsuario(usuarioSelecionado);
		usuarioControler.iniciarInclusaoUsuarioImportado();
		return REDIRECIONA_CADASTRAR_USUARIO;
	}

	public DynamicDataModel<Usuario> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Usuario> dataModel) {
		this.dataModel = dataModel;
	}

	public String getLoginFiltro() {
		return loginFiltro;
	}

	public void setLoginFiltro(String loginFiltro) {
		this.loginFiltro = loginFiltro;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}

}
