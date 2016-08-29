package br.gov.mec.casca.service.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.app.business.CascaFacade;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.casca.security.vo.PermissaoVO;
import br.gov.mec.casca.security.vo.SegurancaVO;
import br.gov.mec.casca.service.CascaService;

@Stateless
@Name("cascaService")
public class CascaServiceBean implements CascaService {

	private static final long serialVersionUID = -1538355556839365632L;
	
	@Logger
	private Log log;
	
	private CascaFacade getCascaFacade() {
		return (CascaFacade)Component.getInstance(CascaFacade.class);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<PermissaoVO> listPermissions(String aplicacao, String target, String action) {
		return getCascaFacade().obterPermissoes(aplicacao, target, action);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void bloquearUsuario(String login) throws CascaException {
		getCascaFacade().bloquearUsuario(login); 
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void desbloquearUsuario(String login) throws CascaException {
		getCascaFacade().desBloquearUsuario(login);
		
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Usuario recuperarUsuario(String login) throws CascaException {
		return getCascaFacade().recuperarUsuario(login);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void incluirUsuario(String nome, String login, String email,
			String loginCriador, String senha) throws CascaException {
		getCascaFacade().incluirUsuario(nome, login, email, loginCriador, senha);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<String> obterNomePerfisPorUsuario(String username) {
		return getCascaFacade().obterNomePerfisPorUsuario(username);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void definirPerfisUsuario(String login, List<String> nomesPerfis) throws CascaException {
		getCascaFacade().definirPerfisUsuario(login, nomesPerfis);
		
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<Perfil> pesquisarPerfis(List<String> perfis)
			throws CascaException {
		return getCascaFacade().pesquisarPerfis(perfis);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<Perfil> pesquisarPerfis(String nome) throws CascaException {
		return getCascaFacade().pesquisarPerfis(nome);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void salvarPerfil(Perfil perfil) throws CascaException {
		getCascaFacade().salvarPerfil(perfil);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean usuarioTemPermissao(String login, String permissao) {
		return getCascaFacade().usuarioTemPermissao(login, permissao);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean usuarioTemPermissao(String login, String componente, String metodo) {
		return getCascaFacade().usuarioTemPermissao(login, componente, metodo);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<String> pesquisarUsuariosComPermissao(String permissao) {
		return getCascaFacade().pesquisarUsuariosComPermissao(permissao);
	}	

	/**
	 * @see br.gov.mec.casca.service.CascaService#isLoggedIn(br.gov.mec.casca.security.vo.SegurancaVO)
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Boolean isLoggedIn(SegurancaVO _segurancaVO) throws CascaException {
		String cascaSessionId = _segurancaVO.getCascaSessionId();
		String clientSessionId = _segurancaVO.getClientSessionId();
		String token = _segurancaVO.getToken();
		String urlLogout = _segurancaVO.getUrlLogout();
		return getCascaFacade().isLoggedIn(cascaSessionId, clientSessionId, token, urlLogout);
	}
	
	@Override
	public String obterUsuarioLogado() {
	
		return this.getCascaFacade().obterUsuarioLogado();
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void renovarSessao(String token) {

		this.log.info("Gerando token de renovacao de sessao");
		this.getCascaFacade().renovarSessao(token);
	}

	@Override
	public List<String> pesquisarUsuariosPorPerfil(String perfil) {
		return getCascaFacade().pesquisarUsuariosPorPerfil(perfil);
	}

	@Override
	public boolean usuarioTemPerfil(String login, String perfil) {
		List<String> perfis = this.getCascaFacade().obterNomePerfisPorUsuario(login);
		
		return (perfis.size() > 0 && perfis.contains(login));		
	}	
}