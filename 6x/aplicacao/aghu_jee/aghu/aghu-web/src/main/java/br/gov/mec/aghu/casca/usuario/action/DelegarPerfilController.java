package br.gov.mec.aghu.casca.usuario.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class DelegarPerfilController extends ActionController {
	private static final long serialVersionUID = -3499206096291771063L;
	private final String PAGE_MEUS_PERFIS = "meusPerfis";
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")	
	private ICascaFacade cascaFacade;
	
	private Usuario usuario;
	private Perfil perfil;
	private Usuario usuarioPerfil;
	private Date dataExpiracao;
	private String motivoDelegacao;	

	/**
	 * Método executado na entrada da página. Carrega o perfil e o usuário logado.
	 * @throws CascaException
	 */
	@PostConstruct
	public void init()  {
		begin(conversation);
		usuario = cascaFacade.obterUsuario(obterLoginUsuarioLogado());
	}
	
	/**
	 * Método usado pela suggestion de usuário.
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException 
	 * @throws CascaException
	 */
	public List<Usuario> pesquisarUsuarios(String parametro) throws ApplicationBusinessException {
		String nomeOuLogin = parametro;
		return this.returnSGWithCount(cascaFacade.pesquisarUsuarios(0, 100, null, true, nomeOuLogin),pesquisarUsuariosCount(parametro));
	}

	/**
	 * Método usado pela suggestion de usuário.
	 * @param parametro
	 * @return
	 * @throws CascaException
	 */
	public Long pesquisarUsuariosCount(String parametro) throws ApplicationBusinessException {
		return cascaFacade.pesquisarUsuariosCount((String)parametro);
	}
	
	/**
	 * Método que salva a delegação de perfil
	 * @return
	 */
	public void salvar() {		
		try {
			cascaFacade.delegarPerfilUsuario(usuario, perfil, usuarioPerfil, dataExpiracao, motivoDelegacao);
			perfil = null; 
			usuarioPerfil = null; 
			dataExpiracao = null;
			motivoDelegacao = null;
			apresentarMsgNegocio("CASCA_MENSAGEM_SUCESSO_DELEGACAO_PERFIL");				
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método executado pelo botão cancelar.
	 * @return
	 */
	public String cancelar() {
		return PAGE_MEUS_PERFIS;
	} 
	

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public Usuario getUsuarioPerfil() {
		return usuarioPerfil;
	}

	public void setUsuarioPerfil(Usuario usuarioPerfil) {
		this.usuarioPerfil = usuarioPerfil;
	}

	public Date getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	public String getMotivoDelegacao() {
		return motivoDelegacao;
	}

	public void setMotivoDelegacao(String motivoDelegacao) {
		this.motivoDelegacao = motivoDelegacao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}