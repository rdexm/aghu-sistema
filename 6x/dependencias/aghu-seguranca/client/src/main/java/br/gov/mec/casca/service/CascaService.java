package br.gov.mec.casca.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remote;

import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.casca.security.vo.PermissaoVO;
import br.gov.mec.casca.security.vo.SegurancaVO;

@Remote
public interface CascaService extends Serializable {

	List<PermissaoVO> listPermissions(String aplicacao,
			String target, String action);

	void bloquearUsuario(String login) throws CascaException;

	void desbloquearUsuario(String login) throws CascaException;

	Usuario recuperarUsuario(String login) throws CascaException;

	void incluirUsuario(String nome, String login, String email,
			String loginCriador, String senha) throws CascaException;
	
	List<String> obterNomePerfisPorUsuario(String username);
	
	List<String> pesquisarUsuariosPorPerfil(String perfil);	

	void definirPerfisUsuario(String login, List<String> nomesPerfis) throws CascaException;

	List<Perfil> pesquisarPerfis(List<String> perfis) throws CascaException;

	List<Perfil> pesquisarPerfis(String nome) throws CascaException;

	void salvarPerfil(Perfil perfil) throws CascaException;
	
	boolean usuarioTemPermissao(String login, String permissao);
	
	boolean usuarioTemPermissao(String login, String componente, String metodo);
	
	boolean usuarioTemPerfil(String login, String perfil);

	/**
	 * Valida se é um token válido para determinar se o usuário está logado na aplicação.
	 * 
	 * @param segurancaVO
	 * @throws CascaException
	 */
	Boolean isLoggedIn(SegurancaVO segurancaVO) throws CascaException;
	
	public String obterUsuarioLogado();
	
	List<String> pesquisarUsuariosComPermissao(String permissao);
	
	public void renovarSessao(String token);
}