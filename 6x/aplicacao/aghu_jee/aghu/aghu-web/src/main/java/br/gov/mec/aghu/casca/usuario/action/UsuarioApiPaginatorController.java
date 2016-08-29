package br.gov.mec.aghu.casca.usuario.action;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.UsuarioApi;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class UsuarioApiPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3779716876375195723L;
	
	private final String REDIRECIONA_CADASTRAR_USUARIO = "cadastrarUsuarioApi";	
	private final String REDIRECIONA_CADASTRAR_PERFIL_USUARIO = "manterPerfilUsuarioApi";

	@EJB
	private ICascaFacade cascaFacade;	
	
	@Inject
	private UsuarioApiController usuarioApiController;
	
	@Inject
	private PerfilUsuarioApiController perfilUsuarioApiController;
	
	@Inject @Paginator
	private DynamicDataModel<UsuarioApi> dataModel;
	
	private String nome;
	private String email;
	private String loginHcpa;
	private DominioSituacao situacao;
	

	@PostConstruct
	protected void init(){
		begin(conversation);
	}

	public String editar(Integer seq) {	
		usuarioApiController.setSeqUsuario(seq);
		return REDIRECIONA_CADASTRAR_USUARIO;
	}

	public String manterPerfilUsuarioApi(Integer seq) {
		perfilUsuarioApiController.setSeqUsuario(seq);
		return REDIRECIONA_CADASTRAR_PERFIL_USUARIO;
	}
	
	public void gerarNovaChave(Integer seq) {
		try {
			UsuarioApi usuarioApi = cascaFacade.obterUsuarioApi(seq);
			if (usuarioApi != null) {
				cascaFacade.reiniciarIdentificacaoAcesso(usuarioApi);
			}
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CHAVE_REINICIADA");
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String incluirUsuarioApi() {
		usuarioApiController.setSeqUsuario(null);
		return REDIRECIONA_CADASTRAR_USUARIO;
	}
	
	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarUsuariosApiCount(nome, email, loginHcpa, situacao);
	}

	@Override
	public List<UsuarioApi> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarUsuariosApi(firstResult, maxResult, orderProperty, asc, nome, email, loginHcpa, situacao);
	}
	
	public void pesquisarUsuarios() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		this.nome = null;
		this.loginHcpa = null;
		this.situacao = null;
		this.email = null;
		this.dataModel.limparPesquisa();
	}
	
	public void setDataModel(DynamicDataModel<UsuarioApi> dataModel) {
		this.dataModel = dataModel;
	}

	public DynamicDataModel<UsuarioApi> getDataModel() {
		return dataModel;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLoginHcpa() {
		return loginHcpa;
	}

	public void setLoginHcpa(String loginHcpa) {
		this.loginHcpa = loginHcpa;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
}