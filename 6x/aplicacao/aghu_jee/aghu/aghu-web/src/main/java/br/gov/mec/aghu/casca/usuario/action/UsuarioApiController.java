package br.gov.mec.aghu.casca.usuario.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.UsuarioApi;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class UsuarioApiController extends ActionController {

	private static final long serialVersionUID = 5353572500931197357L;

	private static final String REDIRECIONA_PESQUISAR_USUARIO = "pesquisarUsuariosApi";

	@EJB
	private ICascaFacade cascaFacade;
	
	private UsuarioApi usuarioApi;
	
	private Integer seqUsuario;

	@PostConstruct
	protected void init(){
		begin(conversation);
	}

	public void iniciar() {
		if (seqUsuario != null) {
			usuarioApi = cascaFacade.obterUsuarioApi(seqUsuario);
		} else {
			usuarioApi = new UsuarioApi();
			usuarioApi.setAtivo(true);
		}
	}
	
	public String salvar() {
		try {
			boolean indicaEdicao = usuarioApi.getId() != null;
			cascaFacade.salvarUsuario(usuarioApi);

			if (indicaEdicao) {
				this.apresentarMsgNegocio(Severity.INFO, "CASCA_MENSAGEM_SUCESSO_EDICAO_USUARIO");			
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "CASCA_MENSAGEM_SUCESSO_CRIACAO_USUARIO");			
			}
			usuarioApi = new UsuarioApi();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return REDIRECIONA_PESQUISAR_USUARIO;
	}
	
	public String cancelar() {
		return REDIRECIONA_PESQUISAR_USUARIO;
	}

	public Integer getSeqUsuario() {
		return seqUsuario;
	}

	public void setSeqUsuario(Integer seqUsuario) {
		this.seqUsuario = seqUsuario;
	}

	public UsuarioApi getUsuarioApi() {
		return usuarioApi;
	}

	public void setUsuarioApi(UsuarioApi usuarioApi) {
		this.usuarioApi = usuarioApi;
	}
 
}
