package br.gov.mec.aghu.casca.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.casca.dao.AcessoDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosJnDAO;
import br.gov.mec.aghu.casca.dao.UsuarioDAO;
import br.gov.mec.aghu.casca.dao.UsuarioJnDAO;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.PerfisUsuariosJn;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.casca.model.UsuarioJn;
import br.gov.mec.aghu.casca.vo.FiltroUsuarioJnVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * 
 * @author murillo.marinho
 * 
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class UsuarioON extends BaseBusiness {
	
	@EJB
	private PerfilON perfilON;

	private static final Log LOG = LogFactory.getLog(UsuarioON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;

	@Inject
	private UsuarioJnDAO usuarioJnDAO;
	
	@Inject
	private PerfisUsuariosJnDAO perfisUsuariosJnDAO;
	
	@Inject
	private UsuarioDAO usuarioDAO;
	
	@Inject
	private PerfisUsuariosDAO perfisUsuariosDAO;
	
	@Inject
	private AcessoDAO acessoDAO;
	
	@Inject
	private IGerenciadorUsuarios gerenciadorUsuarios;

	
	private static final long serialVersionUID = -2096230202644314695L;
//	private static final int LDAP_SEARCH_TIMELIMIT_TODOS_USUARIOS = 30000; // 30s
	
	protected enum UsuarioONExceptionCode implements BusinessExceptionCode {
		CASCA_MENSAGEM_USUARIO_NAO_INFORMADO, 
		CASCA_MENSAGEM_LOGIN_EXISTENTE, 
		CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO, 
		CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, 
		CASCA_MENSAGEM_USUARIO_INATIVO,
		CASCA_MENSAGEM_USUARIO_SEM_PERFIL, CASCA_MENSAGEM_HORA_DIFERENTE,
		ERRO_AUTENTICACAO, CASCA_MENSAGEM_PERFIL_EXISTENTE,
		CASCA_USUARIO_NOME_EXISTENTE;
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
	// throws ApplicationBusinessException {
	//
	// getSession().getTransaction().begin();
	// if (usuario == null) {
	// throw new ApplicationBusinessException(
	// UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_INFORMADO);
	// }
	//
	// if (usuario.getId() == null) { // inclusao
	// if (isLoginExistente(null, usuario.getLogin())) {
	// throw new ApplicationBusinessException(
	// UsuarioONExceptionCode.CASCA_MENSAGEM_LOGIN_EXISTENTE);
	// }
	// usuario.setDataCriacao(new Date());
	// obterValorSequencialUsuario(usuario);
	// entityManager.persist(usuario);
	// entityManager.flush();
	// } else { // alteracao
	// if (isLoginExistente(usuario.getId(), usuario.getLogin())) {
	// throw new ApplicationBusinessException(
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
	public void salvarUsuario(Usuario usuario) throws ApplicationBusinessException {

		if (usuario == null) {
			throw new ApplicationBusinessException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_INFORMADO);
		}
		
		//Verifica se o usuário possui um nome que já existe em outro usuário
		verificarUsuarioNomeExistente(usuario);		
		
		if (usuario.getId() == null) { // inclusao
			if (isLoginExistente(null, usuario.getLogin())) {
				throw new ApplicationBusinessException(
						UsuarioONExceptionCode.CASCA_MENSAGEM_LOGIN_EXISTENTE);
			}
			usuario.setDataCriacao(new Date());
			usuarioDAO.persistir(usuario);
			
			// cria a journal
			UsuarioJn usuarioJn = this.criarJournal(usuario, DominioOperacoesJournal.INS);
			
			// persiste a journal			
			usuarioJnDAO.persistir(usuarioJn);

		} else { // alteracao
			if (isLoginExistente(usuario.getId(), usuario.getLogin())) {
				throw new ApplicationBusinessException(
						UsuarioONExceptionCode.CASCA_MENSAGEM_LOGIN_EXISTENTE);
			}
			// cria a journal
			Usuario usuarioOriginal = usuarioDAO.obterOriginal(usuario);
			UsuarioJn usuarioJn = null;
			boolean alterado = this.alterado(usuario, usuarioOriginal);
			if(alterado){
				usuarioJn = this.criarJournal(usuarioOriginal, DominioOperacoesJournal.UPD);
			}
						
			usuarioDAO.merge(usuario);
			
			if(alterado){
				// persiste a journal			
				usuarioJnDAO.persistir(usuarioJn);
			}
		}
		usuarioDAO.flush();
	}
	
	/**
	 * Verifica se já existe um usuário cadastrado com o nome
	 * @param usuario
	 * @throws CascaException 
	 */
	private void verificarUsuarioNomeExistente(Usuario usuario) throws ApplicationBusinessException {
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		List<Usuario> listaUsuarios = usuarioDAO.pesquisarUsuarioMesmoNome(
				usuario.getNome(), usuario.getId());
		if (!listaUsuarios.isEmpty()){
			throw new ApplicationBusinessException(
					UsuarioONExceptionCode.CASCA_USUARIO_NOME_EXISTENTE);
		}
		
	}
	

	/**
	 * @return
	 */
	protected AcessoDAO getAcessoDAO() {
		return acessoDAO;
	}
	
	protected UsuarioDAO getUsuarioDAO() {
		return usuarioDAO;
	}
	
	protected UsuarioJnDAO getUsuarioJnDAO() {
		return usuarioJnDAO;
	}
	
	protected PerfisUsuariosDAO getPerfisUsuariosDAO() {
		return perfisUsuariosDAO;
	}
	
	protected PerfisUsuariosJnDAO getPerfisUsuariosJnDAO() {
		return perfisUsuariosJnDAO;
	}
	
	/**
	 * @return
	 */
	protected PerfilON getPerfilON() {
		return perfilON;
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
			String nomeOuLogin) throws ApplicationBusinessException {
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
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarUsuariosCount(String nomeOuLogin) {
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		return usuarioDAO.pesquisarUsuariosCount(nomeOuLogin);
	}
	
	/**
	 * Busca o histórico do cadastro do usuário
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtroUsuarioJnVO
	 * @return
	 */
	public List<UsuarioJn> pesquisarHistoricoPorUsuario(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroUsuarioJnVO filtroUsuarioJnVO) {
		if (orderProperty == null) {
			orderProperty = UsuarioJn.Fields.SEQ_JN.toString();
			asc = false;
		}
		return getUsuarioJnDAO().pesquisarPorUsuario(firstResult, maxResult,
				orderProperty, asc, filtroUsuarioJnVO);
	}

	/**
	 * Quantidade de registros no histórico do cadastro do usuário
	 * 
	 * @param filtroUsuarioJnVO
	 * @return
	 */
	public Long pesquisarHistoricoPorUsuarioCount(
			FiltroUsuarioJnVO filtroUsuarioJnVO) {
		return getUsuarioJnDAO().pesquisarPorUsuarioCount(filtroUsuarioJnVO);
	}

	
	/**
	 * Retorna a quantidade de usuarios no IdentityStore configurado e que não
	 * estão cadastrados
	 * 
	 * @param login
	 *            O login utilizado como filtro
	 * @return A quantidade de usuários
	 */
	public Long pesquisarUsuariosNaoCadastradosCount(String login) {
		List<String> logins = usuarioDAO.pesquisarLoginsNaoCadastrados(gerenciadorUsuarios.listarLoginsRegistrados(login));
		return (long) logins.size();
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
	public List<Usuario> pesquisarUsuariosNaoCadastrados(Integer firstResult, Integer maxResult, String login) {

		List<Usuario> usuarios = new ArrayList<Usuario>();

		// FIXME Esta sendo executado duas vezes. Melhorar
//		UsuarioDAO usuarioDAO = getUsuarioDAO();
		
		//Recupera o IdentityStore para aumentar o tempo de busca no LDAP/AD, para casos em que existem muitos usuários
		
		
		
		//TODO arquitetura ver identies
//		LdapIdentityStore identityStore = null;
//		Integer timeLimitOriginal = null;
//
//		if (getIdentityManager().getIdentityStore() instanceof LdapIdentityStore) {
//			identityStore = (LdapIdentityStore) getIdentityManager().getIdentityStore();
//		} else if (getIdentityManager().getIdentityStore() instanceof AdIdentityStore) {
//			identityStore = (AdIdentityStore) getIdentityManager().getIdentityStore();
//		}
//
//		if(identityStore != null) {
//			timeLimitOriginal = identityStore.getSearchTimeLimit(); //Armazena o tempo limite original
//			identityStore.setSearchTimeLimit(LDAP_SEARCH_TIMELIMIT_TODOS_USUARIOS); //Aumenta o limite da pesquisa
//		}
//
//		
//		List<String> logins = usuarioDAO.pesquisarLoginsNaoCadastrados(getIdentityManager().listUsers(login));

//		if(identityStore != null) {
//			identityStore.setSearchTimeLimit(timeLimitOriginal); //Retorna o limite para o original
//		}
//		
//		logDebug("O total de logins não cadastrados é ["+logins.size()+"].");
//
//		for (String l : logins) {
//			Usuario usuario = new Usuario();
//			usuario.setLogin(l);
//			// Nome é not null no banco
//			usuario.setNome(l);
//			usuarios.add(usuario);
//		}

		//Caso não informado o máximo, permite retornar todos
		
		List<String> logins = usuarioDAO.pesquisarLoginsNaoCadastrados(gerenciadorUsuarios.listarLoginsRegistrados(login));
		for (String l : logins) {
			Usuario usuario = new Usuario();
			usuario.setLogin(l);
			// Nome é not null no banco
			usuario.setNome(l);
			usuarios.add(usuario);
		}		
		
		if(maxResult == null) {
			maxResult = usuarios.size();
		}
		
		int max = firstResult + maxResult;
		if (max > usuarios.size()) {
			max = usuarios.size();
		}

		return usuarios.subList(firstResult, max);
	}

	public Usuario obterUsuarioAtivo(String login) throws ApplicationBusinessException {
		Usuario usuario = obterUsuario(login);
		Usuario retorno = null;
		
		if (usuario != null && usuario.isAtivo()) {
			retorno = usuario;
		}
		
		return retorno;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Usuario obterUsuario(Integer id) throws ApplicationBusinessException {
		if (id == null) {
			throw new ApplicationBusinessException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}

		UsuarioDAO usuarioDAO = getUsuarioDAO();
		Usuario usuario = usuarioDAO.obterPorChavePrimaria(id);
		usuarioDAO.refresh(usuario);
		if (usuario == null) {
			throw new ApplicationBusinessException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO);
		}
		return usuario;
	}

	/**
	 * Obter usuário pelo login
	 * 
	 * @param login
	 * @return usuário
	 * @throws ApplicationBusinessException
	 */
	public Usuario obterUsuario(String login)  {
		return obterUsuario(login, true);
	}
	
	public Usuario obterUsuario(String login, boolean ignorarCaixa)  {
		if (login == null) {
			return null;
		}		
		UsuarioDAO usuarioDAO = getUsuarioDAO();
		return usuarioDAO.recuperarUsuario(login);		
	}

	public void associarPerfilUsuario(Integer idUsuario, List<PerfisUsuarios> listaPerfisUsuarios) throws ApplicationBusinessException {

		if (idUsuario == null) {
			throw new ApplicationBusinessException(UsuarioONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}

		Usuario usuario = usuarioDAO.obterOriginal(idUsuario);
		
		List<PerfisUsuarios> perfisUsuariosAtual = perfisUsuariosDAO.pequisarPerfisUsuarios(usuario);
		
		for (PerfisUsuarios perfilUsuario : perfisUsuariosAtual){
			if (!listaPerfisUsuarios.contains(perfilUsuario)){
				this.removerPerfisUsuario(perfilUsuario);
			}
		}
		perfisUsuariosAtual = perfisUsuariosDAO.pequisarPerfisUsuarios(usuario);
		
		for (PerfisUsuarios perfilUsuario : listaPerfisUsuarios){
			if (!perfisUsuariosAtual.contains(perfilUsuario)){
				this.associaPerfisUsuarios(usuario, perfilUsuario);
			}
		}
		
		try{
			perfisUsuariosDAO.flush();
		} catch(Exception e){
			if(e instanceof PersistenceException && e.getCause() instanceof ConstraintViolationException){
				throw new ApplicationBusinessException(UsuarioONExceptionCode.CASCA_MENSAGEM_PERFIL_EXISTENTE);
			}
		}
	}
	
	public void associarPerfilUsuarioPatologista(Integer idUsuario,
			List<PerfisUsuarios> listaPerfis, List<Perfil> perfisPatologista) throws ApplicationBusinessException{
 
		if (idUsuario == null) {
			throw new ApplicationBusinessException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}

		UsuarioDAO usuarioDAO = getUsuarioDAO();
		Usuario usuario = usuarioDAO.obterPorChavePrimaria(idUsuario);
		
		Set<PerfisUsuarios> perfisUsuariosAtual = usuario.getPerfisUsuario();
		List<PerfisUsuarios> perfisUsuariosAtualRemover = new ArrayList<PerfisUsuarios>(0);
		
		// LISTA PERFIS DA NOVA FUNÇÃO
		List<Perfil> perfisInserir = new ArrayList<Perfil>(0);
		for (PerfisUsuarios perfisUsuarios : listaPerfis) {
			perfisInserir.add(perfisUsuarios.getPerfil());
		} 
		
		// LISTA PERFIS DA ANTIGA FUNÇÃO
		List<Perfil> perfisAtuais = new ArrayList<Perfil>(0);
		for (PerfisUsuarios perfisUsuarios : perfisUsuariosAtual) {
			perfisAtuais.add(perfisUsuarios.getPerfil());
		}
		
		// FILTRA PARA REMOVER APENAS PERFIS DE PATOLOGISTA	NÃO REFERENTES A FUNÇÃO NOVA
		for (PerfisUsuarios perfisUsuariosA : perfisUsuariosAtual) {
			if(perfisPatologista.contains(perfisUsuariosA.getPerfil()) && !perfisInserir.contains(perfisUsuariosA.getPerfil())){
				perfisUsuariosAtualRemover.add(perfisUsuariosA);
			}
		}
		
		// REMOVE PERFIS DE PATOLOGISTA REFERENTES A ANTIGA FUNÇÃO
		Set<PerfisUsuarios> perfisUsuariosRemover = new HashSet<PerfisUsuarios>();
		for (PerfisUsuarios perfilUsuario : perfisUsuariosAtualRemover){
				perfisUsuariosRemover.add(perfilUsuario);
				this.removerPerfisUsuario(perfilUsuario);
		}
		// INSERE PERFIS REFERENTES A NOVA FUNÇÃO
		for (PerfisUsuarios perfilUsuario : listaPerfis){
			if(!perfisAtuais.contains(perfilUsuario.getPerfil())){
				this.associaPerfisUsuarios(usuario, perfilUsuario);
			}	
		}	
		perfisUsuariosAtual.removeAll(perfisUsuariosRemover);
	}
	
	/**
	 * 
	 * @param login
	 * @param perfilNome
	 * @throws CascaException
	 */
	public void atribuirPerfilUsuario(String login, String perfilNome, String usuarioLogado) throws ApplicationBusinessException {
		
		Usuario usuario = getCascaFacade().obterUsuarioAtivo(login);
		Perfil perfil = getCascaFacade().pesquisarPerfis(perfilNome).get(0);
		
		if (login == null || perfil == null) {
			throw new ApplicationBusinessException(UsuarioONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}		

		PerfisUsuarios perfilUsuarioPossui = getCascaFacade().
				obterPerfilUsuarioLogin(login, perfilNome);
		
		if(perfilUsuarioPossui == null) {
			PerfisUsuarios perfilUsuario = new PerfisUsuarios();
			perfilUsuario.setDataCriacao(new Date());
			perfilUsuario.setUsuario(usuario);
			perfilUsuario.setPerfil(perfil);
			this.associaPerfisUsuarios(usuario, perfilUsuario);
		}		
		
	}
	
	/**
	 * 
	 * @param login
	 * @param perfilNome
	 * @throws CascaException
	 */
	public void removerPerfilUsuario(String login, String perfil, String usuarioLogado) throws ApplicationBusinessException {
		
		if (login.isEmpty() || perfil.isEmpty()) {
			throw new ApplicationBusinessException(UsuarioONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}		
		
		PerfisUsuarios perfilUsuarioPossui = getCascaFacade().
				obterPerfilUsuarioLogin(login, perfil);
		
		if(perfilUsuarioPossui != null) {
			this.removerPerfisUsuario(perfilUsuarioPossui);
		}
		
	}	

	

	public void associaPerfisUsuarios(Usuario usuario, PerfisUsuarios perfilUsuario) throws ApplicationBusinessException {
		perfisUsuariosDAO.persistir(perfilUsuario);
		
		// cria a journal
		PerfisUsuariosJn perfisUsuariosJn = this.criarJournal(perfilUsuario, DominioOperacoesJournal.INS);
						
		// persiste a journal				
		perfisUsuariosJnDAO.persistir(perfisUsuariosJn);
		
		//TODO rever limpeza de cache nesta arquitetura 
		//PerfilDAOCache.limpaCache();
	}

	public void removerPerfisUsuario(PerfisUsuarios perfilUsuario) throws ApplicationBusinessException {
		PerfisUsuariosJn perfisUsuariosJn = this.criarJournal(perfilUsuario, DominioOperacoesJournal.DEL);
		perfisUsuariosDAO.removerPorId(perfilUsuario.getId());
		
		// persiste a journal			
		perfisUsuariosJnDAO.persistir(perfisUsuariosJn);
		
		//TODO rever limpeza de cache nesta arquitetura 
		//PerfilDAOCache.limpaCache();
	}

	/**
	 * Metodo responsavel por excluir um usuario.
	 */
	public void excluirUsuario(Integer idUsuario) throws ApplicationBusinessException {
		if (idUsuario == null) {
			throw new ApplicationBusinessException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO);
		}

		Usuario usuario = obterUsuario(idUsuario);

		if (usuario == null) {
			throw new ApplicationBusinessException(
					UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO);
		}
		
		// cria a journal
		UsuarioJn usuarioJn = this.criarJournal(usuario, DominioOperacoesJournal.DEL);
		
		acessoDAO.removerAcessos(usuario);
		usuarioDAO.remover(usuario);
		usuarioJnDAO.persistir(usuarioJn);
	}
	
	/**
	 * Verifica se algum campo que deve gerar journal foi alterado
	 * 
	 * @param usuario
	 * @param usuarioOriginal
	 * @return
	 */
	private boolean alterado(Usuario usuario, Usuario usuarioOriginal){
		if(usuario != null && usuarioOriginal != null){
			if(CoreUtil.modificados(usuario.getNome(), usuarioOriginal.getNome()) ||
				CoreUtil.modificados(usuario.getEmail(), usuarioOriginal.getEmail()) ||
				CoreUtil.modificados(usuario.getTempoSessaoMinutos(), usuarioOriginal.getTempoSessaoMinutos()) ||
				CoreUtil.modificados(usuario.isDelegarPerfil(), usuarioOriginal.isDelegarPerfil()) ||
				CoreUtil.modificados(usuario.isAtivo(), usuarioOriginal.isAtivo())){
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Cria uma entidade journal da Usuario
	 * 
	 * @param usuario
	 * @param operacao
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private UsuarioJn criarJournal(Usuario usuario, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		 
		UsuarioJn usuarioJn = BaseJournalFactory.getBaseJournal(operacao, UsuarioJn.class, servidorLogado.getUsuario());
		usuarioJn.setId(usuario.getId());
		usuarioJn.setNome(usuario.getNome());
		usuarioJn.setLogin(usuario.getLogin());
		usuarioJn.setEmail(usuario.getEmail());
		usuarioJn.setAtivo(usuario.isAtivo());
		usuarioJn.setDelegarPerfil(usuario.isDelegarPerfil());
		usuarioJn.setTempoSessaoMinutos(usuario.getTempoSessaoMinutos());
		return usuarioJn;
	}
	
	/**
	 * Cria uma entidade joural da PerfisUsuarios
	 * 
	 * @param perfisUsuarios
	 * @param operacao
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private PerfisUsuariosJn criarJournal(PerfisUsuarios perfisUsuarios, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		PerfisUsuariosJn perfisUsuariosJn = BaseJournalFactory.getBaseJournal(operacao, PerfisUsuariosJn.class, servidorLogado.getUsuario());
		perfisUsuariosJn.setId(perfisUsuarios.getId());
		perfisUsuariosJn.setIdUsuario(perfisUsuarios.getUsuario().getId());
		perfisUsuariosJn.setLogin(perfisUsuarios.getUsuario().getLogin());
		perfisUsuariosJn.setIdPerfil(perfisUsuarios.getPerfil().getId());
		perfisUsuariosJn.setNomePerfil(perfisUsuarios.getPerfil().getNome());
		perfisUsuariosJn.setDataExpiracao(perfisUsuarios.getDataExpiracao());
		perfisUsuariosJn.setMotivoDelegacao(perfisUsuarios.getMotivoDelegacao());
		return perfisUsuariosJn;
	}

	public boolean verificarSenha(String login, String senha) throws ApplicationBusinessException {
// TODO arquitetura verificar IdentityManager				
//			return this.getIdentityManager().authenticate(login, senha);
		return true;
	}
	
	public boolean alterarSenha(String login, String senhaAtual, String senhaNova) throws ApplicationBusinessException {
		// TODO arquitetura verificar IdentityManager	
		//return verificarSenha(login, senhaAtual) && getIdentityManager().changePassword(login, senhaNova);
		return true;
	}	

	/**
	 * 
	 * @param idUsuario
	 * @param login
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private boolean isLoginExistente(Integer idUsuario, String login)
			throws ApplicationBusinessException {
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
	 * @throws ApplicationBusinessException
	 */
	public Boolean isLoggedIn(String cascaSessionId, String clientSessionId, String token, String urlLogout) throws ApplicationBusinessException {
		// TODO arquitetura verificar GerenciadorTokeCliente	
		//GerenciadorTokenCliente gerenciador = (GerenciadorTokenCliente) gerenciadorTokenCliente;
		//return gerenciador.isTokenValido(cascaSessionId, clientSessionId, token, urlLogout);
		return true;
	}
	
	
	
	public void validarLogin(String login) throws ApplicationBusinessException{
		this.validarUsuarioAtivo(login);
	}
	
	/**
	 * Valida se o usuário existe e se está ativo.
	 * Logica de negocio duplicado no AghuBaseLoginModule.
	 * 
	 * @param login Login do usuário no sistema AGHU
	 * @throws ApplicationBusinessException Caso não exista ou esteja ativo
	 */
	public void validarUsuarioAtivo(final String login) throws ApplicationBusinessException {		
		Usuario usuario = obterUsuario(login);
		if (usuario == null) {
			logWarn("Este usuário não foi encontrado no sistema: "+login);
			throw new ApplicationBusinessException(UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO);
		}
		
		if (!usuario.isAtivo()) {
			logWarn("Este usuário está inativo no sistema: "+login);
			throw new ApplicationBusinessException(UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_INATIVO);
		}
		
		if (getPerfilON().pesquisarPerfisAtivosDoUsuarioCount(usuario) == 0) {
			logWarn("Usuário ["+login+"] não tem perfil associado ou todos seus perfis estão inativos.");
			throw new ApplicationBusinessException(UsuarioONExceptionCode.CASCA_MENSAGEM_USUARIO_SEM_PERFIL);
		}
	}	

	public Set<String> pesquisarUsuariosPorPerfil(String perfil) {
		Set<String> result = new HashSet<String>();
		if (perfil != null && !perfil.isEmpty()) {
			List<String> usuarios = getUsuarioDAO().pesquisarUsuariosPorPerfil(perfil);
			if(usuarios != null){
				result.addAll(usuarios);
			}
		}		
		return result; 
	}

	public void alterarSenhaSemValidacarSenhaAtual(String login, String senhaNova) throws ApplicationBusinessException {
		gerenciadorUsuarios.changePassword(login, senhaNova);
	}
	

	
	public void validarSenha(String login, String senhaAtual) throws ApplicationBusinessException {
		if (!gerenciadorUsuarios.authenticate(login, senhaAtual)){
			throw new ApplicationBusinessException(UsuarioONExceptionCode.ERRO_AUTENTICACAO);
		}
		
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}
	
	
}