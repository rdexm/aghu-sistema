package br.gov.mec.casca.app.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.IdentityManager;

import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.app.dao.UsuarioDAO;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.PerfisUsuarios;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.seam.business.SeamContextsManager;
import br.gov.mec.seam.business.exception.NegocioExceptionCode;

/**
 * 
 * @author murillo.marinho
 * 
 */
class UsuarioON extends SeamContextsManager { 

	private IdentityManager identityManager = getIdentityManager();
	
	protected IdentityManager getIdentityManager() {
		return this.obterDoContexto(IdentityManager.class);
	}
	
	protected enum UsuarioONExceptionCode implements NegocioExceptionCode {
		CASCA_MENSAGEM_USUARIO_NAO_INFORMADO, CASCA_MENSAGEM_LOGIN_EXISTENTE, CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO, CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, CASCA_MENSAGEM_USUARIO_NAO_LOGADO, CASCA_MENSAGEM_SENHA_FRACA;
	}

	/**
	 * Metodo responsavel por incluir ou alterar um usuario.
	 * 
	 * @param usuario
	 * @param rapPessoasFisicas
	 * @param rapServidores
	 * @param cseUsuarios
	 * @param rapQualificacao
	 * @param aghProfEspecialidades
	 * @param aghEspecialidade
	 * @param isMedico
	 * @param pessoas
	 */
	// public void salvarUsuario(Usuario usuario)
	// throws CascaException {
	//
	// getSession().getTransaction().begin();
	// if (usuario == null) {
	// throw new CascaException(
	// UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_INFORMADO);
	// }
	//
	// if (usuario.getId() == null) { // inclusao
	// if (isLoginExistente(null, usuario.getLogin())) {
	// throw new CascaException(
	// UsuarioONExceptionCode.CASCA_MENSAGEM_LOGIN_EXISTENTE);
	// }
	// usuario.setDataCriacao(new Date());
	// obterValorSequencialUsuario(usuario);
	// entityManager.persist(usuario);
	// entityManager.flush();
	// } else { // alteracao
	// if (isLoginExistente(usuario.getId(), usuario.getLogin())) {
	// throw new CascaException(
	// UsuarioONExceptionCode.CASCA_MENSAGEM_LOGIN_EXISTENTE);
	// }
	// entityManager.merge(usuario);
	// entityManager.flush();
	// }
	//
	// entityManager.flush();
	// getSession().getTransaction().commit();
	//
	// }

	/**
	 * Metodo responsavel por incluir ou alterar um usuario.
	 * 
	 * @param usuarios
	 */
	public void salvarUsuario(Usuario usuario) throws CascaException {

		if (usuario == null) {
			throw new CascaException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_INFORMADO);
		}

		UsuarioDAO usuarioDAO = getUsuarioDAO();
		if (usuario.getId() == null) { // inclusao
			if (isLoginExistente(null, usuario.getLogin())) {
				throw new CascaException(
						UsuarioONExceptionCode.CASCA_MENSAGEM_LOGIN_EXISTENTE);
			}
			usuario.setDataCriacao(new Date());
			usuarioDAO.inserir(usuario, false);

		} else { // alteracao
			if (isLoginExistente(usuario.getId(), usuario.getLogin())) {
				throw new CascaException(
						UsuarioONExceptionCode.CASCA_MENSAGEM_LOGIN_EXISTENTE);
			}
			usuarioDAO.atualizar(usuario);
		}
		usuarioDAO.flush();
	}

	/**
	 * @return
	 */
	protected UsuarioDAO getUsuarioDAO() {
		return new UsuarioDAO();
	}
	
	/**
	 * @return
	 */
	protected PerfilON getPerfilON() {
		return new PerfilON();
	}
	
	/**
	 * Metodo responsavel por incluir um usuario no CASCA e no IdentityStore.
	 * 
	 * 
	 * @param usuarios
	 */
	public void incluirUsuario(String nome, String login, String email,
			String loginCriador, String senha) throws CascaException {
		Usuario usuarioCriador = obterUsuario(loginCriador);
		Usuario usuario = new Usuario(null, usuarioCriador, nome, login,
				new Date(), email, null, null, null, null);
		if (existeLoginCasca(usuario.getLogin())
				|| identityManager.userExists(usuario.getLogin())) {
			throw new CascaException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_LOGIN_EXISTENTE);
		}
		if (StringUtils.isBlank(senha) || senha.length() < 8) {
			throw new CascaException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_SENHA_FRACA);
		}
		this.salvarUsuario(usuario);
		identityManager.createUser(usuario.getLogin(), senha);
	}

	/**
	 * Verifica existencia de login
	 * 
	 * @param login
	 * @return existencia de login
	 * @throws CascaException
	 */
	private boolean existeLoginCasca(String login) throws CascaException {
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		Usuario usuario = usuarioDAO.recuperarUsuario(login);
		return usuario != null;
	}

	/**
	 * Metodo responsavel por pesquisar usuarios.
	 * 
	 * @param nomeUsuario
	 * @param login
	 * @return
	 */
	public List<Usuario> pesquisarUsuarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String nomeOuLogin) throws CascaException {
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		return usuarioDAO.pesquisarUsuarios(firstResult, maxResult,
				orderProperty, asc, nomeOuLogin);
	}

	/**
	 * Recupera a quantidade de registros encontrados.
	 * 
	 * @param nomeUsuario
	 * @param login
	 * @return
	 * @throws CascaException
	 */
	public Integer pesquisarUsuariosCount(String nomeOuLogin) {
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		return usuarioDAO.pesquisarUsuariosCount(nomeOuLogin);
	}

	/**
	 * Pesquisa o repositorio de usuarios utilizando o IdentityStore configurado
	 * e retorna os usuarios que não estão cadastrados no sistema
	 * 
	 * @param firstResult
	 *            O indice do primeiro objeto
	 * @param maxResult
	 *            A quantidade maxima a ser retornada
	 * @param login
	 *            O login utilizado como filtro
	 * @return Uma coleção de Usuario não cadastrados
	 */
	public List<Usuario> pesquisarUsuariosNaoCadastrados(Integer firstResult,
			Integer maxResult, String login) {

		List<Usuario> usuarios = new ArrayList<Usuario>();

		// FIXME Esta sendo executado duas vezes. Melhorar
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		List<String> logins = usuarioDAO.pesquisarLoginsNaoCadastrados(identityManager.listUsers(login));
//		String log = "O total de logins não cadastrados é ["+logins.size()+"].";
//		logDebug(log);
		for (String l : logins) {
			Usuario usuario = new Usuario();
			usuario.setLogin(l);
			// Nome é not null no banco
			usuario.setNome(l);
			usuarios.add(usuario);
		}

		int max = firstResult + maxResult;
		if (max > usuarios.size()) {
			max = usuarios.size();
		}

		return usuarios.subList(firstResult, max);
	}

	/**
	 * Retorna a quantidade de usuarios no IdentityStore configurado e que não
	 * estão cadastrados
	 * 
	 * @param login
	 *            O login utilizado como filtro
	 * @return A quantidade de usuários
	 */
	public Integer pesquisarUsuariosNaoCadastradosCount(String login) {
		// FIXME Esta sendo executado duas vezes. Melhorar
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		List<String> logins = usuarioDAO.pesquisarLoginsNaoCadastrados(identityManager.listUsers(login));
		return logins.size();
	}

	public Usuario recuperarUsuario(String login) throws CascaException {
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		return usuarioDAO.recuperarUsuario(login);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Usuario obterUsuario(Integer id) throws CascaException {
		if (id == null) {
			throw new CascaException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}

		UsuarioDAO usuarioDAO = getUsuarioDAO();
		Usuario usuario = usuarioDAO.obterPorChavePrimaria(id);
		if (usuario == null) {
			throw new CascaException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO);
		}
		return usuario;
	}

	/**
	 * Obter usuário pelo login
	 * 
	 * @param login
	 * @return usuário
	 * @throws CascaException
	 */
	private Usuario obterUsuario(String login) throws CascaException {
		if (login == null) {
			throw new CascaException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		return usuarioDAO.recuperarUsuario(login);
	}

	/**
	 * 
	 * @param idUsuario
	 * @param listaPerfis
	 * @throws CascaException
	 */
	public void associarPerfilUsuario(Integer idUsuario,
			List<Perfil> listaPerfis) throws CascaException {

		if (idUsuario == null) {
			throw new CascaException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}

		UsuarioDAO usuarioDAO = getUsuarioDAO();
		Usuario usuario = usuarioDAO.obterPorChavePrimaria(idUsuario);
		removerPerfilUsuario(usuario);
		associaPerfilUsuario(usuario, listaPerfis);
		usuarioDAO.flush();
	}

	/**
	 * Metodo responsavel por excluir um usuario.
	 * 
	 * @param id
	 */

	public void excluirUsuario(Integer idUsuario) throws CascaException {
		if (idUsuario == null) {
			throw new CascaException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}

		Usuario usuario = obterUsuario(idUsuario);

		if (usuario == null) {
			throw new CascaException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO);
		}
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		usuarioDAO.remover(usuario);
	}

	/**
	 * Excluir usuário
	 * 
	 * @param login
	 * @return
	 * @throws CascaException
	 */
	public void excluirUsuario(String login) throws CascaException {
		Usuario usuario = obterUsuario(login);
		if (usuario == null) {
			throw new CascaException(
					UsuarioON.UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO);
		}
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		usuarioDAO.remover(usuario);
		identityManager.deleteUser(usuario.getNome());
	}
		
	public boolean verificarSenha(String login, String senha) throws CascaException {
				
		return this.identityManager.authenticate(login, senha);
	}

	public boolean alterarSenha(String login, String senhaNova)
			throws CascaException {
		//Comentado por Marcelo Filho a pedido de Tiago Miari		
		/*
		boolean isSenhaAtigaValida = identityManager.authenticate(login, senhaAtual);
		if (isSenhaAtigaValida) {
			identityManager.changePassword(login, senhaNova);
		}
		*/
		return identityManager.changePassword(login, senhaNova);
	}

	public void definirPerfisUsuario(String login, List<String> nomesPerfis)
			throws CascaException {
		Usuario usuario = obterUsuario(login);
		if (usuario == null) {
			throw new CascaException(
					UsuarioON.UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO);

		}
		List<Perfil> perfis = getPerfilON().pesquisarPerfis(nomesPerfis);
		associarPerfilUsuario(usuario.getId(), perfis);
	}

	/**
	 * Metodo responsavel por incluir um registro de Usuario para o usuario
	 * logado no momento. Utilizado preferencialmente para a inclusao do usuario
	 * no momento do seu login, caso seja a sua primeira vez, atraves do evento
	 * org.jboss.seam.security.loginSuccessful
	 * 
	 * @throws CascaException
	 */
	public void incluirUsuarioLogado() throws CascaException {
		Identity identity = (Identity) Component.getInstance(Identity.class);

		if (identity == null || !identity.isLoggedIn()) {
			throw new CascaException(
					UsuarioON.UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_LOGADO);
		}

		String login = identity.getPrincipal().getName();

		Usuario usuario = recuperarUsuario(login);

		// Se o usuario já existir, não importar.
		if (usuario == null) {
			usuario = new Usuario();
			usuario.setLogin(login);
			// FIXME nome é not null no banco =(
			usuario.setNome(login);
			salvarUsuario(usuario);
		}
	}

	/**
	 * 
	 * @param usuario
	 */
	private void removerPerfilUsuario(Usuario usuario) {
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		List<PerfisUsuarios> listaPerfisUsuariosEncontrados = usuarioDAO
				.pequisarPerfisUsuarios(usuario);
		usuario.getPerfisUsuario().removeAll(listaPerfisUsuariosEncontrados);
		for (PerfisUsuarios perfilUsuario : listaPerfisUsuariosEncontrados) {
			usuarioDAO.removerPerfilUsuario(perfilUsuario);
		}
	}

	/**
	 * 
	 * @param usuario
	 * @param listaPerfis
	 */
	private void associaPerfilUsuario(Usuario usuario, List<Perfil> listaPerfis) {
		if (listaPerfis != null && !listaPerfis.isEmpty()) {
			UsuarioDAO usuarioDAO = getUsuarioDAO();
			for (Iterator<Perfil> iterator = listaPerfis.iterator(); iterator
					.hasNext();) {
				Perfil perfil = (Perfil) iterator.next();
				PerfisUsuarios perfisUsuarios = new PerfisUsuarios(
						null, usuario, perfil, new Date());
				usuarioDAO.salvarPerfilUsuario(perfisUsuarios);
			}
		}

	}

	/**
	 * 
	 * @param idUsuario
	 * @param login
	 * @return
	 * @throws CascaException
	 */
	private boolean isLoginExistente(Integer idUsuario, String login)
			throws CascaException {
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		Usuario usuario = usuarioDAO.recuperarUsuario(login);
		// validacao para edicao
		if (usuario != null && idUsuario != null) {
			if (usuario.getId().equals(idUsuario)) {
				return false;
			}
			return true;
		}
		return usuario == null ? false : true;
	}

	private void ativarOuDesativaUsuario(String login, boolean isAtivo)
			throws CascaException {
		Usuario usuario = obterUsuario(login);
		if (usuario == null) {
			throw new CascaException(
					UsuarioON.UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO);
		}
		usuario.setAtivo(isAtivo);
		salvarUsuario(usuario);
	}

	/**
	 * Bloquear usuário
	 * 
	 * @param login
	 * @throws CascaException
	 */
	public void bloquearUsuario(String login) throws CascaException {
		ativarOuDesativaUsuario(login, false);
	}

	/**
	 * Desbloquear usuário
	 * 
	 * @param login
	 * @throws CascaException
	 */
	public void desBloquearUsuario(String login) throws CascaException {
		ativarOuDesativaUsuario(login, true);
	}
	
	/**
	 * Pesquisa, e retorna, todos os usuários com uma determinada permissão
	 * 
	 * @param permissao Nome da permissão
	 * @return Lista de usuários, ordenada por login, que possuem a permissão.
	 *         Caso a permissão não seja informada, retorna uma lista vazia.
	 */
	public List<String> pesquisarUsuariosComPermissao(String permissao) {
		if (permissao == null || permissao.isEmpty()) {
			return new ArrayList<String>();
		}		
		return getUsuarioDAO().pesquisarUsuariosComPermissao(permissao);
	}

	/**
	 * Valida se é um token válido para determinar se o usuário está logado na aplicação.
	 * 
	 * @param cascaSessionId
	 * @param clientSessionId
	 * @param token
	 * @throws CascaException
	 */
	public Boolean isLoggedIn(String cascaSessionId, String clientSessionId, String token, String urlLogout) throws CascaException {
		GerenciadorTokenCliente gerenciador = (GerenciadorTokenCliente) Component.getInstance(GerenciadorTokenCliente.class);
		return gerenciador.isTokenValido(cascaSessionId, clientSessionId, token, urlLogout);
	}

	public List<String> pesquisarUsuariosPorPerfil(String perfil) {
		if (perfil == null || perfil.isEmpty()) {
			return new ArrayList<String>();
		}		
		return getUsuarioDAO().pesquisarUsuariosPorPerfil(perfil);
	}
}