package br.gov.mec.casca.app.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

import br.gov.mec.aghu.business.exception.AGHUNegocioException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.app.menu.vo.AplicacaoVO;
import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.casca.model.Componente;
import br.gov.mec.casca.model.Metodo;
import br.gov.mec.casca.model.Perfil;
import br.gov.mec.casca.model.Permissao;
import br.gov.mec.casca.model.PermissoesComponentes;
import br.gov.mec.casca.model.Usuario;
import br.gov.mec.casca.security.vo.PermissaoVO;

@SuppressWarnings("serial")
@Name("cascaFacade")
@Scope(ScopeType.STATELESS)
public class CascaFacade implements Serializable {
	
	@Logger
	private Log log;
			 
	@SuppressWarnings("unchecked")
	protected <T> T obterDoContexto(Class<T> clazz) {
		
		return (T) Component.getInstance(clazz, true);
	}	

	protected Identity getIdentity() {
		
		Identity result = null;
		
		try {			
			result = this.obterDoContexto(Identity.class);
		} catch (Exception e) {
			this.log.info("Identity nao localizado");
		}
		
		return result;
	}	
	
	protected SessionKeepAliveTokenON getSessionKeepAliveTokenON() {
		
		SessionKeepAliveTokenON result = null; 
		
		result = (SessionKeepAliveTokenON) Component.getInstance(SessionKeepAliveTokenON.class, true);
		if (result == null) {
			this.log.error("Componente " + SessionKeepAliveTokenON.class.getName() + " nao encontrado");									
		}
		
		return result;
	}
	
    private PerfilON getPerfilON() {
		return new PerfilON();
	}

	private UsuarioON getUsuarioON() {
		return new UsuarioON();
	}

	private ComponenteON getComponenteON() {
		return new ComponenteON();
	}

	private AplicacaoON getAplicacaoON() {
		return new AplicacaoON();
	}

	private PermissaoON getPermissaoON() {
		return new PermissaoON();
	}

	private SenhaON getSenhaON() {
		return new SenhaON();
	}
	
	// componente

	/**
	 * 
	 */
	public List<Componente> pesquisarComponentePorNome(Object nome) {
		return getComponenteON().pesquisarComponentePorNome(nome);
	}

	/**
	 * @param componente 
	 * 
	 */
	public List<Metodo> pesquisarActionPorNome(Object nome, Componente componente) {
		return getComponenteON().pesquisarActionPorNome(nome, componente);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Componente> pesquisarTodosComponentes() {
		return getComponenteON().pesquisarTodosComponentes();
	}

	/**
	 * 
	 * @param nomeComponente
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<Componente> pesquisarComponentes(String nomeComponente,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		return getComponenteON().pesquisarComponentes(nomeComponente,
				firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * 
	 * @param idComponente
	 * @return
	 */
	public Integer pesquisarMetodosComponenteCount(Integer idComponente) {
		return getComponenteON().pesquisarMetodosComponenteCount(idComponente);
	}

	/**
	 * 
	 * @param idComponente
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<Componente> pesquisarMetodosComponente(Integer idComponente,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		return getComponenteON().pesquisarMetodosComponente(idComponente,
				firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * 
	 * @param nomeComponente
	 * @return
	 */
	public Integer pesquisarComponentesCount(String nomeComponente) {
		return getComponenteON().pesquisarComponentesCount(nomeComponente);
	}

	/**
	 * 
	 * @param componente
	 * @param aplicacao
	 * @throws AGHUNegocioException
	 */
	public void salvar(Componente componente, Aplicacao aplicacao)
			throws CascaException {
		getComponenteON().salvar(componente, aplicacao);
	}

	/**
	 * 
	 * @param idComponente
	 * @return
	 * @throws AGHUNegocioException
	 */
	public Componente obterComponente(Integer idComponente)
			throws CascaException {
		return getComponenteON().obterComponente(idComponente);
	}

	/**
	 * 
	 * @param idComponente
	 * @throws AGHUNegocioException
	 */
	public void excluirComponente(Integer idComponente)
			throws CascaException {
		getComponenteON().excluirComponente(idComponente);
	}

	/**
	 * 
	 * @param metodo
	 * @param idComponente
	 * @throws AGHUNegocioException
	 */
	public void salvarMetodo(Metodo metodo, Integer idComponente)
			throws CascaException {
		getComponenteON().salvarMetodo(metodo, idComponente);
	}

	/**
	 * 
	 * @param idMetodo
	 * @return
	 * @throws AGHUNegocioException
	 */
	public Metodo obterMetodo(Integer idMetodo) throws CascaException {
		return getComponenteON().obterMetodo(idMetodo);
	}

	/**
	 * 
	 * @param idMetodo
	 * @throws AGHUNegocioException
	 */
	public void excluirMetodo(Integer idMetodo) throws CascaException {
		getComponenteON().excluirMetodo(idMetodo);
	}

	/**
	 * 
	 * @param idPermissao
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<PermissoesComponentes> pesquisarComponentesPermissao(
			Integer idPermissao, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		return getComponenteON().pesquisarComponentesPermissao(idPermissao,
				firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * 
	 * @param idPermissao
	 * @return
	 */
	public Integer pesquisarComponentesPermissaoCount(Integer idPermissao) {
		return getComponenteON()
				.pesquisarComponentesPermissaoCount(idPermissao);
	}

	// Aplicacao

	/**
	 * 
	 */
	@Restrict("#{s:hasPermission('aplicacao', 'pesquisar')}")
	public List<Aplicacao> pesquisarAplicacaoPorNome(Object nome) {
		return getAplicacaoON().pesquisarAplicacaoPorNome(nome);
	}

	/**
	 * 
	 * @param servidor
	 * @param porta
	 * @param contexto
	 * @param nome
	 * @param externo
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	@Restrict("#{s:hasPermission('aplicacao', 'pesquisar')}")
	public List<Aplicacao> pesquisarAplicacoes(String servidor, Integer porta,
			String contexto, String nome, Boolean externo,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		return getAplicacaoON().pesquisarAplicacoes(servidor, porta, contexto,
				nome, externo, firstResult, maxResults, orderProperty, asc);
	}

	/**
	 * 
	 * @param aplicacao
	 * @throws AGHUNegocioException
	 */
	@Restrict("#{s:hasPermission('aplicacao', 'alterar')}")
	public void salvar(Aplicacao aplicacao) throws CascaException {
		getAplicacaoON().salvar(aplicacao);
	}

	/**
	 * 
	 * @param idAplicacao
	 * @return
	 * @throws AGHUNegocioException
	 */
	@Restrict("#{s:hasPermission('aplicacao', 'pesquisar')}")
	public Aplicacao obterAplicacao(Integer idAplicacao)
			throws CascaException {
		return getAplicacaoON().obterAplicacao(idAplicacao);
	}

	/**
	 * 
	 * @param servidor
	 * @param porta
	 * @param contexto
	 * @param nome
	 * @param externo
	 * @return
	 */
	public Integer pesquisarAplicacoesCount(String servidor, Integer porta,
			String contexto, String nome, DominioSimNao externo) {
		return getAplicacaoON().pesquisarAplicacoesCount(servidor, porta,
				contexto, nome, externo);
	}

	/**
	 * 
	 * @param idAplicacao
	 * @throws AGHUNegocioException
	 */
	@Restrict("#{s:hasPermission('aplicacao', 'excluir')}")
	public void excluirAplicacao(Integer idAplicacao)
			throws CascaException {
		getAplicacaoON().excluirAplicacao(idAplicacao);
	}
	
	/**
	 * @author gandriotti
	 * 
	 * @return
	 */
	@Restrict("#{s:hasPermission('aplicacao', 'pesquisar')}")
	public List<Aplicacao> pesquisarAplicacoes() {
		
		return this.getAplicacaoON().pesquisarAplicacoes();
	}

	// PerfilON
	/**
	 * Metodo responsavel por incluir ou alterar um perfil.
	 * 
	 * @param perfil
	 */
	//@Restrict("#{s:hasPermission('perfil', 'alterar')}")
	public void salvarPerfil(Perfil perfil) throws CascaException {
		getPerfilON().salvarPerfil(perfil);
	}
	
	/**
	 * Metodo responsável por pesquisar pelo nome completo do perfil
	 * ou parte dele.
	 * 
	 * @param nome do perfil
	 * @return Lista de perfis que contenham parte, ou o todo, do nome passado
	 *         por parâmetro
	 * @throws CascaException
	 */
	//@Restrict("#{s:hasPermission('perfil', 'pesquisar')}")
	public List<Perfil> pesquisarPerfis(String nome)
			throws CascaException {
		return getPerfilON().pesquisarPerfis(nome);
	}

	/**
	 * Metodo responsavel por perfis para os suggestion Box
	 * 
	 * @param nomeUsuario
	 * @param login
	 * @return
	 */
	@Restrict("#{s:hasPermission('perfil', 'pesquisar')}")
	public List<Perfil> pesquisarPerfisSuggestionBox(String nome)
			throws CascaException {
		return getPerfilON().pesquisarPerfisSuggestionBox(nome);
	}
	
	/**
	 * Busca perfis pelo nome do perfil
	 * 
	 * @param perfis
	 * @return
	 * @throws AGHUNegocioException
	 */
	@Restrict("#{s:hasPermission('perfil', 'pesquisar')}")
	public List<Perfil> pesquisarPerfis(List<String> perfis)
			throws CascaException {
		return getPerfilON().pesquisarPerfis(perfis);
	}

	/**
	 * Metodo responsavel por pesquisar usuarios.
	 * 
	 * @param nomeUsuario
	 * @param firstResult
	 * @param maxResult
	 * @param asc
	 * @return
	 */
	@Restrict("#{s:hasPermission('perfil', 'pesquisar')}")
	public List<Perfil> pesquisarPerfis(String nome, Integer firstResult,
			Integer maxResult, boolean asc) throws CascaException {
		return getPerfilON().pesquisarPerfis(nome, firstResult, maxResult, asc);
	}

	/**
	 * 
	 * @param nome
	 * @return
	 */
	public Integer pesquisarPerfisCount(String nome) {
		return getPerfilON().pesquisarPerfisCount(nome);
	}

	/**
	 * 
	 * @param nome
	 * @return
	 */
	public Integer pesquisarPerfilCountSuggestionBox(String nome) {
		return getPerfilON().pesquisarPerfilCountSuggestionBox(nome);
	}
	/**
	 * 
	 * @param id
	 * @return
	 */
	@Restrict("#{s:hasPermission('perfil', 'pesquisar')}")
	public Perfil obterPerfil(Integer id) throws CascaException {
		return getPerfilON().obterPerfil(id);
	}

	/**
	 * 
	 * @param idUsuario
	 * @throws AGHUNegocioException
	 */
	@Transactional
	@Restrict("#{s:hasPermission('perfil', 'excluir')}")
	public void excluirPerfil(Integer idPerfil) throws CascaException {
		getPerfilON().excluirPerfil(idPerfil);
	}

	/**
	 * Retorna uma lista dos perfis de um usuario
	 * 
	 * @param username
	 *            login do usuario
	 * @return Uma lista de strings com os perfis do usuario
	 */
	// @Restrict("#{s:hasPermission('perfil', 'pesquisar')}")
	// Metodo utilizado pelo CascaRoleIdentityManager. Nao pode ter restrição de
	// permissão.
	public List<String> obterNomePerfisPorUsuario(String username) {
		return getPerfilON().obterNomePerfisPorUsuario(username);
	}

	/**
	 * 
	 * @param idUsuario
	 * @param listaPerfis
	 * @throws AGHUNegocioException
	 */
	@Transactional
	@Restrict("#{s:hasPermission('manterPerfilPermissao', 'alterar')}")
	public void associarPermissoesPerfil(Integer idPerfil,
			List<Permissao> listaPermissoes) throws CascaException {
		getPerfilON().associarPermissoesPerfil(idPerfil, listaPermissoes);
	}

	// PermissaoON

	/**
	 * 
	 */
	@Restrict("#{s:hasPermission('permissao', 'alterar')}")
	public void salvarPermissao(Permissao permissao)
			throws CascaException {

		getPermissaoON().salvarPermissao(permissao);
	}

	/**
	 * 
	 * @param nome
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	@Restrict("#{s:hasPermission('permissao', 'pesquisar')}")
	public List<Permissao> pesquisarPermissoes(String nome,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		return getPermissaoON().pesquisarPermissoes(nome, firstResult,
				maxResults, orderProperty, asc);
	}

	/**
	 * 
	 * @param busca
	 * @param asc
	 * @return
	 */
	@Restrict("#{s:hasPermission('permissao', 'pesquisar')}")
	public List<Permissao> pesquisarPermissoesSuggestionBox(String stringBusca) {
		return getPermissaoON().pesquisarPermissoesSuggestionBox(stringBusca);
	}
	
	/**
	 * 
	 * @return
	 */
	@Restrict("#{s:hasPermission('permissao', 'pesquisar')}")
	public List<Permissao> pesquisarTodasPermissoes() {
		return getPermissaoON().pesquisarTodasPermissoes();
	}

	/**
	 * 
	 * @param nome
	 * @return
	 */
	public Integer pesquisarPermissoesCount(String nome) {
		return getPermissaoON().pesquisarPermissoesCount(nome);
	}

	/**
	 * 
	 * @param consulta
	 * @return
	 */
	public Integer pesquisarPermissoesCountSuggestionBox(String consulta) {
		return getPermissaoON().pesquisarPermissoesCountSuggestionBox(consulta);
	}
	/**
	 * 
	 * @param idPermissao
	 * @throws AGHUNegocioException
	 */
	@Restrict("#{s:hasPermission('permissao', 'excluir')}")
	public void excluir(Integer idPermissao) throws CascaException {
		getPermissaoON().excluir(idPermissao);
	}

	/**
	 * 
	 * 
	 * @param idPermissaoComponente
	 * @throws AGHUNegocioException
	 */
	@Restrict("#{s:hasPermission('permissao', 'alterar')}")
	public void excluirComponentePermissao(Integer idPermissaoComponente)
			throws CascaException {
		getPermissaoON().excluirComponentePermissao(idPermissaoComponente);
	}

	/**
	 * 
	 * @param idPermissao
	 * @return
	 * @throws AGHUNegocioException
	 */
	@Restrict("#{s:hasPermission('permissao', 'pesquisar')}")
	public Permissao obterPermissao(Integer idPermissao)
			throws CascaException {
		return getPermissaoON().obterPermissao(idPermissao);
	}

	/**
	 * 
	 * @param aplicacao
	 * @param target
	 * @param action
	 * @return
	 */
	public List<PermissaoVO> obterPermissoes(String aplicacao, String target, String action) {
		return getPermissaoON().obterPermissoes(aplicacao, target, action);
	}
	
	public List<PermissaoVO> obterPermissoesPrecarregaveis(String aplicacoes, String targets, String actions) {
		return getPermissaoON().obterPermissoesPrecarregaveis(aplicacoes, targets, actions);
	}

	/**
	 * 
	 * @param idPermissao
	 * @param idComponente
	 * @param contem
	 * @return
	 */
	@Restrict("#{s:hasPermission('permissao', 'pesquisar')}")
	public List<Metodo> recuperaMetodosComponentePermissao(Integer idPermissao,
			Integer idComponente, boolean contem) {
		return getPermissaoON().recuperaMetodosComponentePermissao(idPermissao,
				idComponente, contem);
	}

	/**
	 * 
	 * @param idPermissao
	 * @param idComponente
	 * @param listaDeMetodos
	 * @throws AGHUNegocioException
	 */
	@Restrict("#{s:hasPermission('permissao', 'alterar')}")
	public void associarPermissaoComponenteMetodos(Integer idPermissao,
			Integer idComponente, Metodo metodo)
			throws CascaException {

		getPermissaoON().associarPermissaoComponenteMetodos(idPermissao,
				idComponente, metodo);
	}

	// usuarioON

	/**
	 * 
	 */
	// FIXME Retirar do CASCA de uma vez por todas!	
	/*
	@Restrict("#{s:hasPermission('usuario', 'alterar')}")
	public void salvarUsuario(Usuario usuario,
			RapPessoasFisicas rapPessoasFisicas, RapServidores rapServidores,
			CseUsuarios cseUsuarios, Pessoa pessoa,
			RapQualificacao rapQualificacao,
			AghProfEspecialidades aghProfEspecialidades,
			AghEspecialidades aghEspecialidade, boolean isMedico)
			throws CascaException {
		getUsuarioON().salvarUsuario(usuario);
	}
	/**
	 * 
	 * @param usuario
	 * @throws AGHUNegocioException
	 */
	@Restrict("#{s:hasPermission('usuario', 'alterar')}")
	public void salvarUsuario(Usuario usuario) throws CascaException {
		getUsuarioON().salvarUsuario(usuario);
	}

	/**
	 * 
	 * @param nome
	 * @param login
	 * @param email
	 * @param loginCriador
	 * @param senha
	 * @throws AGHUNegocioException
	 */
	//@Restrict("#{s:hasPermission('usuario', 'alterar')}")
	public void incluirUsuario(String nome, String login, String email,
			String loginCriador, String senha) throws CascaException {
		getUsuarioON().incluirUsuario(nome, login, email, loginCriador, senha);
	}

	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param nomeOuLogin
	 * @return
	 * @throws AGHUNegocioException
	 */
	@Restrict("#{s:hasPermission('usuario', 'pesquisar')}")
	public List<Usuario> pesquisarUsuarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String nomeOuLogin) throws CascaException {
		return getUsuarioON().pesquisarUsuarios(firstResult, maxResult,
				orderProperty, asc, nomeOuLogin);
	}

	public Integer pesquisarUsuariosCount(String nomeOuLogin) {
		return getUsuarioON().pesquisarUsuariosCount(nomeOuLogin);
	}

	@Restrict("#{s:hasPermission('usuario', 'pesquisar')}")
	public List<Usuario> pesquisarUsuariosNaoCadastrados(Integer firstResult,
			Integer maxResult, String login) {
		return getUsuarioON().pesquisarUsuariosNaoCadastrados(firstResult,
				maxResult, login);
	}

	public Integer pesquisarUsuariosNaoCadastradosCount(String login) {
		return getUsuarioON().pesquisarUsuariosNaoCadastradosCount(login);
	}

	//@Restrict("#{s:hasPermission('usuario', 'pesquisar')}")
	public Usuario recuperarUsuario(String login) throws CascaException {
		return getUsuarioON().recuperarUsuario(login);
	}

	@Restrict("#{s:hasPermission('usuario', 'pesquisar')}")
	public Usuario obterUsuario(Integer id) throws CascaException {
		return getUsuarioON().obterUsuario(id);
	}

	@Transactional
	@Restrict("#{s:hasPermission('usuario', 'associarPerfil')}")
	public void associarPerfilUsuario(Integer idUsuario,
			List<Perfil> listaPerfis) throws CascaException {
		getUsuarioON().associarPerfilUsuario(idUsuario, listaPerfis);
	}

	@Restrict("#{s:hasPermission('usuario', 'excluir')}")
	public void excluirUsuario(Integer idUsuario) throws CascaException {
		getUsuarioON().excluirUsuario(idUsuario);
	}

	@Restrict("#{s:hasPermission('usuario', 'excluir')}")
	public void excluirUsuario(String login) throws CascaException {
		getUsuarioON().excluirUsuario(login);
	}
		
	public boolean verificarSenha(String login, String senha) throws CascaException {		
		return this.getUsuarioON().verificarSenha(login, senha);
	}

	public boolean alterarSenha(String login, String senhaNova)
			throws CascaException {
		return getUsuarioON().alterarSenha(login, senhaNova);
	}

	public void definirPerfisUsuario(String login, List<String> nomesPerfis)
			throws CascaException {
		getUsuarioON().definirPerfisUsuario(login, nomesPerfis);
	}

	@Restrict("#{s:hasPermission('usuario', 'incluirLogado')}")
	public void incluirUsuarioLogado() throws CascaException {
		getUsuarioON().incluirUsuarioLogado();
	}

	//@Restrict("#{s:hasPermission('usuario', 'alterar')}")
	public void bloquearUsuario(String login) throws CascaException {
		getUsuarioON().bloquearUsuario(login);
	}

	//@Restrict("#{s:hasPermission('usuario', 'alterar')}")
	public void desBloquearUsuario(String login) throws CascaException {
		getUsuarioON().desBloquearUsuario(login);
	}

	public void enviarTokenSenhaUsuario(String login, String url) throws CascaException {
		getSenhaON().enviarTokenSenhaUsuario(login, url);
	}
	
	/**
	 * Recupera todas as aplicações que o usuário tem acesso.
	 * 
	 * @return Um List de objetos AplicacaoVO
	 */
	@Restrict("#{s:hasPermission('aplicacao', 'pesquisar')}")
	public List<AplicacaoVO> recuperarAplicacoes(String login) {
		return getAplicacaoON().recuperarAplicacoes(login);
	}
	
	/**
	 * Dado o login do usuario, verificar se ele tem uma determinada permissão
	 * 
	 * @param login
	 *            o login do usuário
	 * @param permissao
	 *            o nome da permissão a ser testada
	 * @return true se o usuario tem a permissão, false caso contrário
	 */
//	@Restrict("#{s:hasPermission('usuario', 'pesquisarPermissao')}")
	public boolean usuarioTemPermissao(String login, String permissao) {
		return getPermissaoON().usuarioTemPermissao(login, permissao);
	}
	
	/**
	 * Dado o login do usuario, verificar se ele tem permissão de acesso
	 * para o componente e metódo
	 * @param login O login do usuário
	 * @param componente Target
	 * @param metodo Action
	 * @return true se o usuário tem uma permissão associada ao componente e 
	 * método, false caso contrário
	 */
	public boolean usuarioTemPermissao(String login, String componente, String metodo) {
		return getPermissaoON().usuarioTemPermissao(login, componente, metodo);
	}
	
	/**
	 * Pesquisa, e retorna, todos os usuários com uma determinada permissão
	 * 
	 * @param permissao Nome da permissão
	 * @return Lista de usuários, ordenada por login, que possuem a permissão.
	 *         Caso a permissão não seja informada, retorna uma lista vazia.
	 */
	public List<String> pesquisarUsuariosComPermissao(String permissao) {
		return getUsuarioON().pesquisarUsuariosComPermissao(permissao);
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
		return getUsuarioON().isLoggedIn(cascaSessionId, clientSessionId, token, urlLogout);
	}
	
	/**
	 * @author gandriotti
	 * @return
	 */
	public String obterUsuarioLogado() {
		
		String result = null;
		Identity id = null;
		
		id = this.getIdentity();
		if (id != null) {			
			if (id.getPrincipal() != null) {
				result = id.getPrincipal().getName();							
			} else {
				this.log.info("Identity nao contem principal: " + id.toString());
			}
		} else {
			this.log.info("Identity nao encontrado");
		}
		
		return result;
	}

	/**
	 * @author gandriotti
	 */
	public boolean consumirRenovacaoSessao(String token) {
		
		Boolean result = null;
		SessionKeepAliveTokenON keepAlive = null;
		
		keepAlive = this.getSessionKeepAliveTokenON();
		result = Boolean.FALSE;
		if (keepAlive != null) {
			result = keepAlive.consumeKeepAlive(token);
			if (result == null) {
				result = Boolean.FALSE;
			}
			this.log.debug("Token de renovacao de sessao: CONSUMIDO [" + token + "] = " + result);			
		}
		
		return result.booleanValue();
	}		

	/**
	 * @author gandriotti
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void renovarSessao(String token) {
		
		SessionKeepAliveTokenON keepAlive = null;
		
		keepAlive = this.getSessionKeepAliveTokenON();
		if (keepAlive != null) {
			keepAlive.setKeepAlive(token);			
		}		
		this.log.debug("Token de renovacao de sessao: ENVIADO [" + token + "]");
	}

	public List<String> pesquisarUsuariosPorPerfil(String perfil) {
		return getUsuarioON().pesquisarUsuariosPorPerfil(perfil);
	}		
}