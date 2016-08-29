package br.gov.mec.aghu.casca.usuario.action;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class UsuarioPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3779716876375195723L;
	
	
	private final String REDIRECIONA_CADASTRAR_USUARIO = "cadastrarUsuario";	
	private final String REDIRECIONA_CADASTRAR_PERFIL_USUARIO = "manterPerfilUsuario";	
	private final String REDIRECIONA_IMPORTAR_USUARIO = "importarUsuario";

	@EJB
	private ICascaFacade cascaFacade;	
	
	@Inject
	private Conversation conversation;
	
	@Inject
	private UsuarioController usuarioController;

	@Inject @Paginator
	private DynamicDataModel<Usuario> dataModel;
	
	private List<PerfisUsuarios> perfisSelecionados;
	private String nomeOuLogin;
	private Usuario usuarioSelecionado;

	
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	@PostConstruct
	private void init(){
		begin(conversation);
		this.perfisSelecionados = new ArrayList<PerfisUsuarios>();
	}

	/**
	 * 
	 * Metodo inicial do caso de uso.
	 * 
	 */
	public String editar() {	
		usuarioController.setUsuario(usuarioSelecionado);
		return REDIRECIONA_CADASTRAR_USUARIO;
	}
	
	
	public String manterPerfilUsuario() {
		usuarioController.setUsuario(usuarioSelecionado);
		usuarioController.carregarPerfisSelecionados();	
		return REDIRECIONA_CADASTRAR_PERFIL_USUARIO;
	}
	
	
	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarUsuariosCount(nomeOuLogin);
	}

	@Override
	public List<Usuario> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<Usuario> retorno = null;
		try {
			retorno = cascaFacade.pesquisarUsuarios(firstResult, maxResult,
					orderProperty, asc, nomeOuLogin);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}
	
	
	/**
	 * Realiza a chamada para a pesquisa de usuario, e sua paginacao.
	 */
	public void pesquisarUsuarios() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		this.nomeOuLogin = null;
		this.dataModel.limparPesquisa();
	}
	
	
	public String importarUsuario(){
		return REDIRECIONA_IMPORTAR_USUARIO;
	}
	
	public void excluir() {
		try {
			cascaFacade.excluirUsuario(usuarioSelecionado.getId());			
			this.apresentarMsgNegocio(Severity.INFO,"CASCA_MENSAGEM_SUCESSO_EXCLUSAO_USUARIO");			
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	

	// get's and set's
	public String getNomeOuLogin() {
		return nomeOuLogin;
	}

	public void setNomeOuLogin(String nomeOuLogin) {
		this.nomeOuLogin = nomeOuLogin;
	}

	public void setDataModel(DynamicDataModel<Usuario> dataModel) {
		this.dataModel = dataModel;
	}

	public DynamicDataModel<Usuario> getDataModel() {
		return dataModel;
	}

	public List<PerfisUsuarios> getPerfisSelecionados() {
		return perfisSelecionados;
	}
	
	public void setPerfisSelecionados(List<PerfisUsuarios> perfisSelecionados) {
		this.perfisSelecionados = perfisSelecionados;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}

}
