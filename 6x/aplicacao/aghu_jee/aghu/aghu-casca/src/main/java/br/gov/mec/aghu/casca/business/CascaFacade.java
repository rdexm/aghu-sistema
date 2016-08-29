package br.gov.mec.aghu.casca.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.dao.AcessoDAO;
import br.gov.mec.aghu.casca.dao.AplicacaoDAO;
import br.gov.mec.aghu.casca.dao.CseCategoriaProfissionalDAO;
import br.gov.mec.aghu.casca.dao.CseProcessosDAO;
import br.gov.mec.aghu.casca.dao.DashboardDAO;
import br.gov.mec.aghu.casca.dao.FavoritoDAO;
import br.gov.mec.aghu.casca.dao.MenuDAO;
import br.gov.mec.aghu.casca.dao.ModuloDAO;
import br.gov.mec.aghu.casca.dao.PalavraChaveMenuDAO;
import br.gov.mec.aghu.casca.dao.PerfilApiDAO;
import br.gov.mec.aghu.casca.dao.PerfilDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosApiDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosDAO;
import br.gov.mec.aghu.casca.dao.PermissaoDAO;
import br.gov.mec.aghu.casca.dao.TokensApiDAO;
import br.gov.mec.aghu.casca.dao.UsuarioApiDAO;
import br.gov.mec.aghu.casca.dao.UsuarioDAO;
import br.gov.mec.aghu.casca.model.Acesso;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Dashboard;
import br.gov.mec.aghu.casca.model.Favorito;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.casca.model.Metodo;
import br.gov.mec.aghu.casca.model.Modulo;
import br.gov.mec.aghu.casca.model.PalavraChaveMenu;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfilApi;
import br.gov.mec.aghu.casca.model.PerfilJn;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.PerfisUsuariosApi;
import br.gov.mec.aghu.casca.model.PerfisUsuariosJn;
import br.gov.mec.aghu.casca.model.Permissao;
import br.gov.mec.aghu.casca.model.PermissoesComponentes;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.casca.model.UsuarioApi;
import br.gov.mec.aghu.casca.model.UsuarioJn;
import br.gov.mec.aghu.casca.security.vo.PermissaoVO;
import br.gov.mec.aghu.casca.vo.FiltroPerfilJnVO;
import br.gov.mec.aghu.casca.vo.FiltroPerfisUsuariosJnVO;
import br.gov.mec.aghu.casca.vo.FiltroUsuarioJnVO;
import br.gov.mec.aghu.casca.vo.MenuVO;
import br.gov.mec.aghu.casca.vo.ModuloVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAcesso;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.service.seguranca.BearerToken;

@SuppressWarnings({"PMD.ExcessiveClassLength"})
@Stateless
public class CascaFacade extends BaseFacade implements ICascaFacade {

	private static final long serialVersionUID = -4405274028267416457L;

	@EJB
	private RemoverPerfisAcessoSistemaComPendenciasBloqueantesSchedulerRN removerPerfisAcessoSistemaComPendenciasBloqueantesSchedulerRN;
	
	@EJB
	private AcessoON acessoON;

	@EJB
	private PalavraChaveON palavraChaveON;
	
	@Inject
	private CseCategoriaProfissionalDAO cseCategoriaProfissionalDAO;
	
	@Inject
	private AcessoDAO acessoDAO;
	
	@Inject
	private CseProcessosDAO cseProcessosDAO;
	
	@Inject
	private MenuDAO menuDAO;
	
	@Inject
	private ModuloDAO moduloDAO;
	
	@Inject
	private UsuarioDAO usuarioDAO;

	@Inject
	private PerfilDAO perfilDAO;

	@EJB
	private PerfilON perfilON;

	@EJB
	private PerfilApiON perfilApiON;
	
	@EJB
	private PerfilUsuarioON perfilUsuarioON;

	@EJB
	private UsuarioON usuarioON;

	@EJB
	private MenucrudON menucrudON;

	@EJB
	private ComponenteON componenteON;

	@EJB
	private MenuON menuON;

	@EJB
	private AplicacaoON aplicacaoON;

	@Inject
	private AplicacaoDAO aplicacaoDAO;
	
	@EJB
	private PermissaoON permissaoON;

	@Inject
	private PermissaoDAO permissaoDAO;
	
	@EJB
	private SenhaON senhaON;
	
	@Inject
	private PerfilApiDAO perfilApiDAO;
	
	@Inject
	private PerfisUsuariosDAO perfisUsuariosDAO;
	
	@Inject
	private FavoritoDAO favoritoDAO;

	@Inject
	private UsuarioApiDAO usuarioApiDAO;
	
	@Inject
	private DashboardDAO dashboardDAO;

	@Inject
	private PalavraChaveMenuDAO palavraChaveMenuDAO;

	@EJB
	private UsuarioApiON usuarioApiON;
	
	@EJB
	private TokenApiON tokenApiON;
	
	@Inject
	private TokensApiDAO tokenApiDAO;

	@Inject
	private PerfisUsuariosApiDAO perfisUsuariosApiDAO;
	
	@Override
	public List<Componente> pesquisarComponentePorNome(Object nome) {
		return getComponenteON().pesquisarComponentePorNome(nome);
	}

	@Override
	public List<Metodo> pesquisarActionPorNome(Object nome,
			Componente componente) {
		return getComponenteON().pesquisarActionPorNome(nome, componente);
	}

	@Override
	public List<Componente> pesquisarComponentes(String nomeComponente,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		return getComponenteON().pesquisarComponentes(nomeComponente,
				firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long pesquisarMetodosComponenteCount(Integer idComponente) {
		return getComponenteON().pesquisarMetodosComponenteCount(idComponente);
	}

	@Override
	public List<Metodo> pesquisarMetodosComponente(Integer idComponente,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		return componenteON.pesquisarMetodosComponente(idComponente,
				firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long pesquisarComponentesCount(String nomeComponente) {
		return getComponenteON().pesquisarComponentesCount(nomeComponente);
	}

	@Override
	public BearerToken obterBearerToken(String authInfo, String origem) {
		return tokenApiON.obterBearerToken(authInfo, origem);
	}
	
	@Override
	public Componente obterComponente(Integer idComponente)
			throws ApplicationBusinessException {
		return getComponenteON().obterComponente(idComponente);
	}

	@Override
	public void excluirComponente(Integer idComponente) throws ApplicationBusinessException {
		getComponenteON().excluirComponente(idComponente);
	}

	@Override
	public Metodo obterMetodo(Integer idMetodo) throws ApplicationBusinessException {
		return getComponenteON().obterMetodo(idMetodo);
	}

	@Override
	public List<PermissoesComponentes> pesquisarComponentesPermissao(
			Integer idPermissao, String target, String action, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		return getComponenteON().pesquisarComponentesPermissao(idPermissao, target, action,
				firstResult, maxResults, orderProperty, asc);
	}

	@Override
	public Long pesquisarComponentesPermissaoCount(Integer idPermissao, String target, String action) {
		return getComponenteON().pesquisarComponentesPermissaoCount(idPermissao, target, action);
	}

	// Aplicacao

	@Override
	@Secure("#{s:hasPermission('aplicacao', 'pesquisar')}")
	public List<Aplicacao> pesquisarAplicacaoPorNome(Object nome) {
		return getAplicacaoON().pesquisarAplicacaoPorNome(nome);
	}

	@Override
	@Secure("#{s:hasPermission('aplicacao', 'pesquisar')}")
	public List<Aplicacao> pesquisarAplicacoes(String servidor, Integer porta,
			String contexto, String nome, Boolean externo, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		return aplicacaoDAO.pesquisarAplicacoes(servidor, porta, contexto, nome, externo, firstResult, maxResults, orderProperty, asc);
	}

	@Override
	@Secure("#{s:hasPermission('aplicacao', 'alterar')}")
	public void salvar(Aplicacao aplicacao) throws ApplicationBusinessException {
		getAplicacaoON().salvar(aplicacao);
	}

	@Override
	@Secure("#{s:hasPermission('aplicacao', 'pesquisar')}")
	public Aplicacao obterAplicacao(Integer idAplicacao) {
		return getAplicacaoON().obterAplicacao(idAplicacao);
	}
	
	@Override
	public Long pesquisarAplicacoesCount(String servidor, Integer porta,
			String contexto, String nome, Boolean externo) {
		return aplicacaoDAO.pesquisarAplicacoesCount(servidor, porta,	contexto, nome, externo);
	}

	@Override
	@Secure("#{s:hasPermission('aplicacao', 'excluir')}")
	public void excluirAplicacao(Integer idAplicacao) throws ApplicationBusinessException {
		getAplicacaoON().excluirAplicacao(idAplicacao);
	}

	@Override
	@Secure("#{s:hasPermission('menu', 'alterar')}")
	public void salvarMenu(Menu menu) throws ApplicationBusinessException {
		getMenucrudON().salvarMenu(menu);
	}

	@Override
	public BearerToken refreshBearerToken(String authInfo, String origem) {
		return tokenApiON.refreshBearerToken(authInfo, origem);
	}
	
	@Override
	@Secure("#{s:hasPermission('menu', 'pesquisar')}")
	public List<Menu> pesquisarMenu(String nomeMenu, String urlMenu,
			Integer idAplicacao, Integer idMenuPai, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc){
		return getMenucrudON().pesquisarMenu(nomeMenu, urlMenu, idAplicacao,
				idMenuPai, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarMenucrudCount(String nomeMenu, String urlMenu,
			Integer idAplicacao, Integer idMenuPai) {
		return getMenucrudON().pesquisarMenucrudCount(nomeMenu, urlMenu, idAplicacao, idMenuPai);
	}

	@Override
	public void salvarPerfilApi(PerfilApi perfilApi) throws ApplicationBusinessException {
		perfilApiON.salvarPerfilApi(perfilApi);
	}
	
	@Override
	@Secure("#{s:hasPermission('menu', 'pesquisar')}")
	public List<Menu> pesquisarMenuPorNomeEId(String objPesquisa) {
		return getMenucrudON().pesquisarMenuPorNomeEId(objPesquisa);
	}

	@Override
	public Long pesquisarPerfilApiCount(String nome, String descricao, DominioSituacao situacao) {
		return perfilApiDAO.pesquisarPerfilApiCount(nome, descricao, situacao);
	}
	
	@Override
	public List<PerfilApi> pesquisarPerfilApi(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, String nome, String descricao, DominioSituacao situacao) {
		return perfilApiDAO.pesquisarPerfilApi(firstResult, maxResult, orderProperty, asc, nome, descricao, situacao);
	}
	
	@Override
	public UsuarioApi obterUsuarioApi(Integer seq) {
		return usuarioApiDAO.obterPorChavePrimaria(seq);
	}
	
	@Override
	public PerfilApi obterPerfilApi(Integer seq) {
		return perfilApiDAO.obterPorChavePrimaria(seq);
	}

	@Override
	public List<PerfilApi> pesquisarPerfisApiSuggestionBox(String nome) {
		return perfilApiDAO.pesquisarPerfisApiSuggestionBox(nome);	
	}
	
	@Override
	public Long pesquisarPerfilApiCountSuggestionBox(String nome) {
		return perfilApiDAO.pesquisarPerfilApiCountSuggestionBox(nome);
	}
	
	@Override
	public void enviarEmailChaveIdentificadora(UsuarioApi usuarioApi) {
		usuarioApiON.enviarEmailChaveIdentificadora(usuarioApi);
	}
	
	@Override
	public void salvarUsuario(UsuarioApi usuarioApi) throws ApplicationBusinessException { 
		usuarioApiON.salvarUsuario(usuarioApi);
	}
	
	@Override
	public List<UsuarioApi> pesquisarUsuariosApi(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String nome, String email, String loginHcpa, DominioSituacao situacao) {
		return usuarioApiON.pesquisarUsuariosApi(firstResult, maxResult, orderProperty, asc, nome, email, loginHcpa, situacao);
	}

	@Override
	public List<PerfisUsuariosApi> pequisarPerfisUsuariosApi(UsuarioApi usuarioApi) {
		return perfisUsuariosApiDAO.pequisarPerfisUsuariosApi(usuarioApi);
	}
	
	@Override
	public Long pesquisarUsuariosApiCount(String nome, String email, String loginHcpa, DominioSituacao situacao) {
		return usuarioApiON.pesquisarUsuariosApiCount(nome, email, loginHcpa, situacao);
	}
	
	@Override
	public Menu obterMenu(Integer id) throws ApplicationBusinessException {
		return getMenucrudON().obterMenu(id);
	}

	@Override
	@Secure("#{s:hasPermission('menu', 'excluir')}")
	public void deletarMenu(Integer id) throws ApplicationBusinessException {
		getMenucrudON().deletarMenu(id);
	}

	@Override
	public Boolean verificarTokenAtivo(String token) {
		return tokenApiDAO.verificarTokenAtivo(token);
	}
	
	@Override
	@Secure("#{s:hasPermission('menu', 'pesquisar')}")
	public MenuVO recuperarMenuPorUrl(String url) {
		return this.getMenuON().obterMenuPorUrl(url);
	}

	@Override
	public List<Integer> recuperarMenusValidos(String loginUser) {
		return getMenuDAO().recuperarMenusValidos(loginUser);
	}
	
	@Override
	public List<Integer> recuperarMenusValidos(String loginUser, Set<String> conjuntoModulosAtivos) {
		return getMenuON().recuperarMenusValidos(loginUser, conjuntoModulosAtivos);
	}

	@Override
	public List<Menu> recuperarMenusValidos(String loginUser, String nome, Set<String> conjuntoModulosAtivos) {
		return getMenuON().recuperarMenusValidos(loginUser, nome, conjuntoModulosAtivos);
	}

	@Override
	public void atualizarDashdoard(Integer menuId, String modal, String login, Integer ordem) throws BaseException {
		getMenuON().atualizarDashdoard(menuId, modal, login, ordem);
	}
	
	@Override
	public void atualizarPosicaoFavorito(Integer menuId, String login, Integer ordem) {
		getMenuON().atualizarPosicaoFavorito(menuId, login, ordem);
	}

	@Override
	public List<Dashboard> obterDashBoard(String login) {
		return dashboardDAO.obterDashBoard(login);
	}
	
	@Override
	public Favorito obterFavoritoIdMenu(String login, Integer menuId)  {
		return favoritoDAO.obterFavoritoIdMenu(login, menuId);
	}

	@Override
	public void reiniciarIdentificacaoAcesso(UsuarioApi usuarioApi) throws ApplicationBusinessException {
		this.usuarioApiON.reiniciarIdentificacaoAcesso(usuarioApi);
	}
	
	@Override
	@Secure("#{s:hasPermission('favoritos', 'pesquisar')}")
	public List<Favorito> obterFavoritos(String login) throws BaseException {
		return getMenuON().obterFavoritos(login);
	}
	
	@Override
	@Secure("#{s:hasPermission('favoritos', 'pesquisar')}")
	public List<Integer> obterIdMenuFavoritos(String login) {
		return getMenuON().obterIdMenuFavoritos(login);
	}	

	@Override
	public Favorito criarFavorito(Menu menu, String login, Integer ordem)
			throws BaseException {
		return getMenuON().criarFavorito(menu, login, ordem);
	}

	@Override
	public void excluirFavorito(Integer id) {
		getMenuON().excluirFavorito(id);
	}
	
	@Override
	public void excluirFavoritoIdMenu(String usuarioLogado, Integer idMenu){
		getMenuON().excluirFavoritoIdMenu(usuarioLogado, idMenu);
	}
	
	@Override
	public void excluirDashboard(String usuarioLogado, Integer menuId) {
		getMenuON().excluirDashboard(usuarioLogado, menuId);
	}
	
	// PerfilON
	@Override
	@Secure("#{s:hasPermission('perfil', 'alterar')}")
	public void salvarPerfil(Perfil perfil) throws ApplicationBusinessException {
		getPerfilON().salvarPerfil(perfil);
	}

	// @Secure("#{s:hasPermission('perfil', 'pesquisar')}")
	@Override
	public List<Perfil> pesquisarPerfis(String nome) {
		return getPerfilON().pesquisarPerfis(nome);
	}
	
	@Override
	public Boolean verificarPerfilToken(String token, Set<String> rolesSet) {
		return perfisUsuariosApiDAO.verificarPerfilToken(token, rolesSet);
	}
	
	/**
	 * Busca o histórico do cadastro do perfil
	 */
	public List<PerfilJn> pesquisarHistoricoPorPerfil(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroPerfilJnVO filtroPerfilJnVO) {
		return getPerfilON().pesquisarHistoricoPorPerfil(firstResult, maxResult,
				orderProperty, asc, filtroPerfilJnVO);
	}

	/**
	 * Quantidade de registros no histórico do cadastro do perfil
	 */
	public Long pesquisarHistoricoPorPerfilCount(FiltroPerfilJnVO filtroPerfilJnVO) {
		return getPerfilON().pesquisarHistoricoPorPerfilCount(filtroPerfilJnVO);
	}

	@Override
	@Secure("#{s:hasPermission('perfil', 'pesquisar')}")
	public List<Perfil> pesquisarPerfisSuggestionBox(String nome)
			throws ApplicationBusinessException {
		return getPerfilON().pesquisarPerfisSuggestionBox(nome);
	}

	@Override
	@Secure("#{s:hasPermission('perfil', 'pesquisar')}")
	public List<Perfil> pesquisarPerfis(String nome, String descricaoResumida,
			Integer firstResult, Integer maxResult, boolean asc)  {
		return getPerfilDAO().pesquisarPerfis(nome, descricaoResumida, firstResult, maxResult, asc);
	}

	@Override
	@Secure("#{s:hasPermission('perfil', 'pesquisar')}")
	public Long pesquisarPerfisCount(String nome) {
		return getPerfilON().pesquisarPerfisCount(nome);
	}
	
	@Override
	@Secure("#{s:hasPermission('perfil', 'pesquisar')}")
	public Long pesquisarPerfisCount(String nome, String descricao) {
		return getPerfilON().pesquisarPerfisCount(nome, descricao);
	}	
	
	@Override
	public Long pesquisarPerfilCountSuggestionBox(String nome) {
		return getPerfilON().pesquisarPerfilCountSuggestionBox(nome);
	}

	@Override
	@Secure("#{s:hasPermission('perfil', 'pesquisar')}")
	public Perfil obterPerfil(Integer id) throws ApplicationBusinessException {
		return getPerfilON().obterPerfil(id);
	}

	@Override
	
	@Secure("#{s:hasPermission('perfil', 'excluir')}")
	public void excluirPerfil(Integer idPerfil) throws ApplicationBusinessException {
		getPerfilON().excluirPerfil(idPerfil);
	}

	// @Secure("#{s:hasPermission('perfil', 'pesquisar')}")
	// Metodo utilizado pelo CascaRoleIdentityManager. Nao pode ter restrição de
	// permissão.
	@Override
	public Set<String> obterNomePerfisPorUsuario(String username) {
		return getPerfilON().obterNomePerfisPorUsuario(username);
	}

	@Override
	@Secure("#{s:hasPermission('manterPerfilPermissao', 'alterar')}")
	public void associarPermissoesPerfil(Integer idPerfil,
			List<Permissao> listaPermissoes) throws ApplicationBusinessException {
		getPerfilON().associarPermissoesPerfil(idPerfil, listaPermissoes);
	}

	// PermissaoON

	@Override
	@Secure("#{s:hasPermission('permissao', 'alterar')}")
	public void salvarPermissao(Permissao permissao) throws ApplicationBusinessException {

		getPermissaoON().salvarPermissao(permissao);
	}

	@Override
	@Secure("#{s:hasPermission('permissao', 'pesquisar')}")
	public List<Permissao> pesquisarPermissoes(String nome, String descricao,
			Modulo modulo, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		return getPermissaoON().pesquisarPermissoes(nome, descricao, modulo,
				firstResult, maxResults, orderProperty, asc);
	}

	@Override
	@Secure("#{s:hasPermission('permissao', 'pesquisar')}")
	public List<Permissao> pesquisarPermissoesSuggestionBox(String stringBusca) {
		return getPermissaoON().pesquisarPermissoesSuggestionBox(stringBusca);
	}

	@Override
	@Secure("#{s:hasPermission('permissao', 'pesquisar')}")
	public List<Permissao> pesquisarTodasPermissoes() {
		return getPermissaoON().pesquisarTodasPermissoes();
	}

	@Override
	public Long pesquisarPermissoesCount(String nome) {
		return this.pesquisarPermissoesCount(nome, null, null);
	}

	@Override
	public Long pesquisarPermissoesCount(String nome, String descricao,
			Modulo modulo) {
		return getPermissaoON().pesquisarPermissoesCount(nome, descricao, modulo);
	}

	@Override
	@Secure("#{s:hasPermission('permissao', 'excluir')}")
	public void excluir(Integer idPermissao) throws ApplicationBusinessException {
		getPermissaoON().excluir(idPermissao);
	}

	@Override
	@Secure("#{s:hasPermission('permissao', 'alterar')}")
	public void excluirComponentePermissao(Integer idPermissaoComponente)
			throws ApplicationBusinessException {
		getPermissaoON().excluirComponentePermissao(idPermissaoComponente);
	}

	@Override
	@Secure("#{s:hasPermission('permissao', 'pesquisar')}")
	public Permissao obterPermissao(Integer idPermissao) throws ApplicationBusinessException {
		return getPermissaoON().obterPermissao(idPermissao);
	}

	@Deprecated
	@Override
	public List<PermissaoVO> obterPermissoesPrecarregaveis(String aplicacoes,
			String targets, String actions, String login) {
		return getPermissaoON().obterPermissoesPrecarregaveis(aplicacoes,
				targets, actions, login);
	}

	
	@Override
	@Secure("#{s:hasPermission('permissao', 'pesquisar')}")
	public List<Metodo> recuperaMetodosComponentePermissao(Integer idPermissao,
			Integer idComponente, boolean contem) {
		return getPermissaoON().recuperaMetodosComponentePermissao(idPermissao,
				idComponente, contem);
	}

	@Override
	@Secure("#{s:hasPermission('permissao', 'alterar')}")
	public void associarPermissaoComponenteMetodos(Integer idPermissao,
			Integer idComponente, Metodo metodo) throws ApplicationBusinessException {

		getPermissaoON().associarPermissaoComponenteMetodos(idPermissao,
				idComponente, metodo);
	}

	// usuarioON

	@Override
	public List<Usuario> pesquisarUsuariosAssociaveis(String nomeOuLogin) {
		return getUsuarioDAO().pesquisarUsuariosAssociaveis(nomeOuLogin);
	}

	@Override
	// FIXME Retirar do CASCA de uma vez por todas!
	/*
	 * @Secure("#{s:hasPermission('usuario', 'alterar')}") public void
	 * salvarUsuario(Usuario usuario, RapPessoasFisicas rapPessoasFisicas,
	 * RapServidores rapServidores, CseUsuarios cseUsuarios, Pessoa pessoa,
	 * RapQualificacao rapQualificacao, AghProfEspecialidades
	 * aghProfEspecialidades, AghEspecialidades aghEspecialidade, boolean
	 * isMedico) throws ApplicationBusinessException { getUsuarioON().salvarUsuario(usuario);
	 * } /**
	 * 
	 * @param usuario
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('usuario', 'alterar')}")
	public void salvarUsuario(Usuario usuario) throws ApplicationBusinessException {
		getUsuarioON().salvarUsuario(usuario);
	}

	@Override
	@Secure("#{s:hasPermission('usuario', 'pesquisar')}")
	public List<Usuario> pesquisarUsuarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String nomeOuLogin) throws ApplicationBusinessException {
		return getUsuarioON().pesquisarUsuarios(firstResult, maxResult,
				orderProperty, asc, nomeOuLogin);
	}

	@Override
	public Long pesquisarUsuariosCount(String nomeOuLogin) {
		return getUsuarioON().pesquisarUsuariosCount(nomeOuLogin);
	}
	
	/**
	 * Busca o histórico do cadastro do usuário
	 */
	public List<UsuarioJn> pesquisarHistoricoPorUsuario(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroUsuarioJnVO filtroUsuarioJnVO) {
		return getUsuarioON().pesquisarHistoricoPorUsuario(firstResult,
				maxResult, orderProperty, asc, filtroUsuarioJnVO);
	}

	/**
	 * Quantidade de registros no histórico do cadastro do usuário
	 */
	public Long pesquisarHistoricoPorUsuarioCount(
			FiltroUsuarioJnVO filtroUsuarioJnVO) {
		return getUsuarioON().pesquisarHistoricoPorUsuarioCount(filtroUsuarioJnVO);
	}
	
	/**
	 * Busca o histórico do cadastro do perfisUsuarios
	 */
	public List<PerfisUsuariosJn> pesquisarHistoricoPorPerfisUsuarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroPerfisUsuariosJnVO filtroPerfisUsuariosJnVO) {
		return getPerfilUsuarioON().pesquisarHistoricoPorPerfisUsuarios(firstResult, maxResult,	orderProperty, asc, filtroPerfisUsuariosJnVO);
	}

	/**
	 * Quantidade de registros no histórico do cadastro do perfisUsuarios
	 */
	public Long pesquisarHistoricoPorPerfisUsuariosCount(FiltroPerfisUsuariosJnVO filtroPerfisUsuariosJnVO) {
		return getPerfilUsuarioON().pesquisarHistoricoPorPerfisUsuariosCount(filtroPerfisUsuariosJnVO);
	}

	@Override
	@Secure("#{s:hasPermission('usuario', 'pesquisar')}")
	public List<Usuario> pesquisarUsuariosNaoCadastrados(Integer firstResult,Integer maxResult, String login) {
		return getUsuarioON().pesquisarUsuariosNaoCadastrados(firstResult,maxResult, login);
	}

	
	@Override
	@Secure("#{s:hasPermission('usuario', 'pesquisar')}")
	public Long pesquisarUsuariosNaoCadastradosCount(String login) {
		return getUsuarioON().pesquisarUsuariosNaoCadastradosCount(login);
	}	
	
	// @Secure("#{s:hasPermission('usuario', 'pesquisar')}")
	@Override
	public Usuario recuperarUsuario(String login) throws ApplicationBusinessException {
		return recuperarUsuario(login, true);
	}
	
	@Override
	public Usuario recuperarUsuario(String login, boolean ignorarMaiusculasMinusculas) throws ApplicationBusinessException {
		return getUsuarioON().obterUsuario(login, ignorarMaiusculasMinusculas);
	}

	@Override
	@Secure("#{s:hasPermission('usuario', 'pesquisar')}")
	public Usuario obterUsuario(Integer id) throws ApplicationBusinessException {
		return getUsuarioON().obterUsuario(id);
	}
	
	@Override
	public Usuario obterUsuarioAtivo(final String login) throws ApplicationBusinessException {
		return getUsuarioON().obterUsuarioAtivo(login);
	}	
	
	@Override
	public void atribuirPerfilUsuario(final String login, final String perfilNome, final String usuarioLogado) throws ApplicationBusinessException {
		getUsuarioON().atribuirPerfilUsuario(login, perfilNome, usuarioLogado);
	}
	
	@Override
	public void removerPerfilUsuario(final String login, final String perfilNome, final String usuarioLogado) throws ApplicationBusinessException {
		getUsuarioON().removerPerfilUsuario(login, perfilNome, usuarioLogado);
	}		
	

	@Override
	public void removerPerfisUsuario(PerfisUsuarios perfilUsuario) throws ApplicationBusinessException {
		getUsuarioON().removerPerfisUsuario(perfilUsuario);
	}
	
	@Override
	@Secure("#{s:hasPermission('usuario', 'associarPerfil')}")
	public void associarPerfilUsuario(Integer idUsuario,
			List<PerfisUsuarios> listaPerfis) throws ApplicationBusinessException {
		getUsuarioON().associarPerfilUsuario(idUsuario, listaPerfis);
	}

	@Override
	public void associarPerfilUsuarioApi(Integer idUsuario, List<PerfisUsuariosApi> listaPerfisUsuarios) throws ApplicationBusinessException {
		usuarioApiON.associarPerfilUsuarioApi(idUsuario, listaPerfisUsuarios);
	}
	
	@Override
	public void associaPerfisUsuarios(Usuario usuario, PerfisUsuarios perfilUsuario) throws ApplicationBusinessException {
		getUsuarioON().associaPerfisUsuarios(usuario, perfilUsuario);
	}
	
	@Override
	@Secure("#{s:hasPermission('usuario', 'excluir')}")
	public void excluirUsuario(Integer idUsuario) throws ApplicationBusinessException {
		getUsuarioON().excluirUsuario(idUsuario);
	}

	@Override
	public boolean verificarSenha(String login, String senha)
			throws ApplicationBusinessException {
		return this.getUsuarioON().verificarSenha(login, senha);
	}

	@Override
	public boolean alterarSenha(String login, String senhaAtual, String senhaNova)
			throws ApplicationBusinessException {
		return getUsuarioON().alterarSenha(login, senhaAtual, senhaNova);
	}

	@Override
	public void validarAutenticacaoNegocial(final String login)
			throws ApplicationBusinessException {
		getUsuarioON().validarLogin(login);
	}

	public PerfisUsuarios obterPerfilUsuarioLogin(String usuario, String perfil) {
		return getPerfilUsuarioON().obterPerfilUsuarioLogin(usuario, perfil);
	}	
	
	// @Secure("#{s:hasPermission('usuario', 'pesquisarPermissao')}")
	@Override
	public boolean usuarioTemPermissao(String login, String permissao) {
		return getPermissaoON().usuarioTemPermissao(login, permissao);
	}

	/*
	 * (non-Javadoc)
	 * @deprecated usar boolean temPermissao(String login, String componente, String metodo)
	 * @see
	 * br.gov.mec.aghu.casca.business.ICascaFacade#usuarioTemPermissao(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@Deprecated
	public boolean usuarioTemPermissao(String login, String componente,
			String metodo) {
		return getPermissaoON().usuarioTemPermissao(login, componente, metodo);
	}

	@Override
	public List<String> pesquisarUsuariosComPermissao(String permissao) {
		return getUsuarioON().pesquisarUsuariosComPermissao(permissao);
	}

	@Override
	public Boolean isLoggedIn(String cascaSessionId, String clientSessionId,
			String token, String urlLogout) throws ApplicationBusinessException {
		return getUsuarioON().isLoggedIn(cascaSessionId, clientSessionId,
				token, urlLogout);
	}

	@Override
	public List<Modulo> listarModulosAtivos() {
		return this.getModuloDAO().listarModulosAtivos();
	}
	
	public List<Modulo> listarModulosPorNome(String nomeModulo){
		return this.getModuloDAO().listarModulosPorNome(nomeModulo);
	}
	
	
	
	@Override
	public boolean verificarSeModuloEstaAtivo(String modulo){
		// Arquitetura antiga:
		// return ModuloDAOCache.verificarSeModuloEstaAtivo(modulo);
		return this.getModuloDAO().verificarSeModuloEstaAtivo(modulo);
	}

	@Override
	public Set<String> pesquisarUsuariosPorPerfil(String perfil) {
		return getUsuarioON().pesquisarUsuariosPorPerfil(perfil);
	}

	@Override
	public Long listarModulosCount(String strPesquisa) {
		return this.getModuloDAO().listarModulosCount(strPesquisa);
	}

	@Override
	public List<Modulo> listarModulos(String strPesquisa) {
		return this.getModuloDAO().listarModulos(strPesquisa);
	}

	@Override
	public boolean usuarioLogadoLiberadoDePendenciasBloqueantes() throws ApplicationBusinessException {
		return this.getPerfilUsuarioON()
				.usuarioLogadoLiberadoDePendenciasBloqueantes();
	}

	@Override
	public boolean usuarioLiberadoDePendenciasBloqueantes(String usuario) {
		return this.getPerfilUsuarioON().usuarioLiberadoDePendenciasBloqueantes(usuario);
	}

	@Override
	public void liberarAcessoParaAcessarSistemaComPendenciasBloqueantes(
			Usuario usuario) throws BaseException {
		this.getPerfilUsuarioON()
				.liberarAcessoParaAcessarSistemaComPendenciasBloqueantes(
						usuario);
	}

	@Override
	public void removerLiberacaoAcessoSistemaComPendenciasBloqueantes(
			Usuario usuario) throws BaseException {
		this.getPerfilUsuarioON()
				.removerLiberacaoAcessoSistemaComPendenciasBloqueantes(usuario);
	}


	@Override
	public void removerPerfisAcessoSistemaComPendenciasBloqueantes(
			Date expiration, String cron) throws ApplicationBusinessException {
		this.getRemoverPerfisAcessoSistemaComPendenciasBloqueantesSchedulerRN()
				.removerPerfisAcessoSistemaComPendenciasBloqueantes(expiration,	cron);
	}

	protected RemoverPerfisAcessoSistemaComPendenciasBloqueantesSchedulerRN getRemoverPerfisAcessoSistemaComPendenciasBloqueantesSchedulerRN() {
		return removerPerfisAcessoSistemaComPendenciasBloqueantesSchedulerRN;
	}

	@Override
	public Long listarModulosCount() {
		return this.getModuloDAO().listarModulosCount();
	}

	@Override
	public List<Menu> recuperarSubMenusValidos(Boolean force) {
		return getMenuDAO().recuperarSubMenusValidos(force);
	}
	
	@Override
	public List<Menu> recuperarMenus() {
		return getMenuDAO().recuperarMenus();
	}
	
	public List<ModuloVO> listarModulos(Integer firstResult, Integer maxResult,	String orderProperty, boolean asc, Set<String> conjuntoModulosAtivos) {

		List<Modulo> listaModulos = this.getModuloDAO().listarModulos(firstResult, maxResult, orderProperty, asc);
		
		List<ModuloVO> modulos = new ArrayList<ModuloVO>();

		for (Modulo modulo : listaModulos) {
			ModuloVO moduloVO = new ModuloVO();

			if ((conjuntoModulosAtivos.contains(modulo.getNome()))) {
				modulo.setAtivo(Boolean.TRUE);
				moduloVO.setAtualizado(Boolean.TRUE);
			} else {
				modulo.setAtivo(Boolean.FALSE);
				moduloVO.setAtualizado(Boolean.FALSE);
			}
			
			moduloVO.setModulo(modulo);
			modulos.add(moduloVO);
		}

		return modulos;
	}
	

	@Override
	public void registrarAcesso(String login, String enderecoOrigem,
			String mensagem, Boolean autorizado, DominioTipoAcesso tipoAcesso)
			throws ApplicationBusinessException {
		this.getAcessoON().registrarAcesso(login, enderecoOrigem, mensagem,
				autorizado, tipoAcesso);
	}

	@Override
	public List<Acesso> pesquisarAcessos(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Usuario usuario, String enderecoOrigem, String mensagem,
			Boolean autorizado, DominioTipoAcesso tipoAcesso, Date dataInicial,
			Date dataFinal) {

		return this.getAcessoDAO().pesquisarAcessos(firstResult, maxResult,
				orderProperty, asc, usuario, enderecoOrigem, mensagem,
				autorizado, tipoAcesso, dataInicial, dataFinal);

	}

	@Override
	public Long pesquisarAcessosCount(Usuario usuario,
			String enderecoOrigem, String mensagem, Boolean autorizado,
			DominioTipoAcesso tipoAcesso, Date dataInicial, Date dataFinal) {
		return this.getAcessoDAO().pesquisarAcessosCount(usuario,
				enderecoOrigem, mensagem, autorizado, tipoAcesso, dataInicial,
				dataFinal);
	}

	private AcessoON getAcessoON() {
		return acessoON;
	}

	private AcessoDAO getAcessoDAO() {
		return acessoDAO;
	}

	@Secure("#{s:hasPermission('modulo', 'pesquisar')}")
	public Modulo obterModulo(Integer idModulo) {
		return getModuloDAO().obterPorChavePrimaria(idModulo);
	}

	@Secure("#{s:hasPermission('modulo', 'alterar')}")
	public void alterarSituacaoModulo(Integer idModulo, Boolean situacaoModulo) {
		this.getModuloDAO().alterarSituacao(idModulo, situacaoModulo);	
	}

	@Override
	public Boolean validarPermissaoPorServidorESeqProcesso(String login,
			Short seqProcesso) {
		return getCseProcessosDAO().validarPermissaoPorServidorESeqProcesso(
				login, seqProcesso);
	}

	@Override
	public Boolean validarPermissaoConsultaPorServidorESeqProcesso(
			String login, Short seqProcesso) {
		return getCseProcessosDAO()
				.validarPermissaoConsultaPorServidorESeqProcesso(login,
						seqProcesso);
	}

	@Override
	public Boolean validarPermissaoExecutaPorServidorESeqProcesso(String login,
			Short seqProcesso) {
		return getCseProcessosDAO()
				.validarPermissaoExecutaPorServidorESeqProcesso(login,
						seqProcesso);
	}

	@Override
	public Boolean validarPermissaoPorServidorEProcessos(String login,
			Short pRocSeq, Short pEspSeq) {
		return getCseProcessosDAO().validarPermissaoPorServidorEProcessos(
				login, pRocSeq, pEspSeq);
	}
	
	@Override
	public Boolean validarPermissaoPorServidorERocSeq(String login, Short cRoqSeq) {
		return getCseProcessosDAO().validarPermissaoPorServidorERocSeq(login, cRoqSeq);
	}
	
	@Override
	public List<CseCategoriaProfissional> pesquisarCategoriaProfissional(
			RapServidores servidor) {
		return getCseCategoriaProfissionalDAO().pesquisarCategoriaProfissional(
				servidor);
	}
	
	@Override
	public CseCategoriaProfissional primeiraCategoriaProfissional(RapServidores servidor) {
		return getCseCategoriaProfissionalDAO().primeiraCategoriaProfissional(servidor);
	}

	@Override
	public List<Menu> pesquisarMenusTabelasSistema(String parametro) {
		return this.getMenuDAO().pesquisarMenusTabelasSistema(
				parametro);
	}

	@Override
	public Long pesquisarCountMenusTabelasSistema(String parametro) {
		return this.getMenuDAO().pesquisarCountMenusTabelasSistema(parametro);
	}
	
	@Override
	public Boolean verificarMaximoTentativasAcessoUltimoMinuto(String enderecoOrigem) throws ApplicationBusinessException {
		return getAcessoON().verificarMaximoTentativasAcessoUltimoMinuto(enderecoOrigem);
	}
	
	@Override
	public Boolean validarPermissaoPorServidorESeqProcessoEPrefixoPerfil(
			String login, Short seqProcesso, String prefixoPerfil) {
		return getCseProcessosDAO().validarPermissaoPorServidorESeqProcessoEPrefixoPerfil(login, seqProcesso, prefixoPerfil);
	}
	
	public void delegarPerfilUsuario(Usuario usuario, Perfil perfil, Usuario usuarioPerfil, Date dataExpiracao, String motivoDelegacao) throws ApplicationBusinessException {
		getPerfilUsuarioON().delegarPerfilUsuario(usuario, perfil, usuarioPerfil, dataExpiracao, motivoDelegacao);
	}
	
	public PerfisUsuarios obterPerfilUsuario(String usuario, String perfil) {
		return getPerfilUsuarioON().obterPerfilUsuario(usuario, perfil);
	}	
	
	@Override
	public boolean temPermissao(String login, String componente, String metodo) {
		return permissaoDAO.temPermissao(login, componente, metodo);
	}
	
	@Override
	public boolean usuarioTemPerfil(String login, String perfil) {
		// TODO essa validação pode ser otimizado mais ainda se a chamada
		// abaixo fizer uma pesquisa em banco pelo usuario e perfil
		Set<String> perfis = this.obterNomePerfisPorUsuario(login);
	
		return !perfis.isEmpty() && perfis.contains(perfil);
	}
	
	
	@Override	
	public List<Permissao> obterPermissoesPorPerfil(Perfil perfil){
		return permissaoDAO.obterPermissoesPorPerfil(perfil);
	}
	
	@Override
	public void associarPermissoesPerfil(Perfil perfil, List<Permissao> listaPermissoes, List<Permissao> listaPermissoesExcluidas) throws ApplicationBusinessException {
		perfilON.associarPermissoesPerfil(perfil, listaPermissoes, listaPermissoesExcluidas);
	}
	
	@Override	
	public Long pesquisarPermissoesCountSuggestionBox(String consulta) {
		return permissaoON.pesquisarPermissoesCountSuggestionBox(consulta);
	}
	
	@Override
	public Usuario obterUsuario(String login)  {
		return usuarioON.obterUsuario(login);
	}	
	
	@Override	
	public void excluirPermissao(Permissao permissao) throws ApplicationBusinessException {
		permissaoON.excluirPermissao(permissao);
	}	
	
	@Override
	public void alterarSenhaSemValidacarSenhaAtual(String login,  String senhaNova) throws ApplicationBusinessException {
		usuarioON.alterarSenhaSemValidacarSenhaAtual(login, senhaNova);
	}
	
	@Override
	public List<PerfisUsuarios> listarPerfisPorUsuario(Usuario usuario){
		return perfisUsuariosDAO.pequisarPerfisUsuariosSemCache(usuario);
	}	
	
	@Override
	public void validarSenha(String login, String senhaAtual) throws ApplicationBusinessException {
		usuarioON.validarSenha(login,  senhaAtual);
		
	}	
	
	@Override
	public void enviarTokenSenhaUsuario(String login, String token, String url)
			throws ApplicationBusinessException {
		senhaON.enviarTokenSenhaUsuario(login, token, url);
	}	
	
	@Override
	public List<PerfisUsuarios> pequisarPerfisUsuarios(Usuario usuario){
		return perfisUsuariosDAO.pequisarPerfisUsuarios(usuario);
	}
	
	@Override
	public List<PerfisUsuarios> pequisarPerfisUsuariosSemCache(Usuario usuario){
		return perfisUsuariosDAO.pequisarPerfisUsuariosSemCache(usuario);
	}
	
	@Override
	@Secure("#{s:hasPermission('aplicacao', 'pesquisar')}")
	public Aplicacao obterAplicacaoPorContexto(String contexto, String servidor){
		return getAplicacaoON().obterAplicacaoPorContexto(contexto, servidor);
	}
	
	@Override
	public void associarPerfilUsuarioPatologista(Integer usuario,
			List<PerfisUsuarios> listaPerfis, List<Perfil> perfisPatologista) throws ApplicationBusinessException{
		getUsuarioON().associarPerfilUsuarioPatologista(usuario, listaPerfis, perfisPatologista);
	}
	
	@Override
	public List<CseCategoriaProfissional> pesquisarListaCseCategoriaProfissional(Object filtro){
		return getCseCategoriaProfissionalDAO().pesquisarListaCseCategoriaProfissional(filtro);
	}
		
	@Override
	public Long pesquisarListaCseCategoriaProfissionalCount(Object filtro) {
		return getCseCategoriaProfissionalDAO().pesquisarListaCseCategoriaProfissionalCount(filtro);
	}

	/**
	 * #39003 - Serviço que busca ultima solicitacao de exames
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Boolean existeServidorCategoriaProfMedico(Integer matricula, Short vinculo) throws ApplicationBusinessException{
		return perfilUsuarioON.existeServidorCategoriaProfMedico(matricula, vinculo);
	}
	
	protected CseCategoriaProfissionalDAO getCseCategoriaProfissionalDAO() {
		return cseCategoriaProfissionalDAO;
	}

	protected CseProcessosDAO getCseProcessosDAO() {
		return cseProcessosDAO;
	}

	protected PerfilDAO getPerfilDAO() {
		return perfilDAO;
	}

	protected UsuarioDAO getUsuarioDAO() {
		return usuarioDAO;
	}

	protected ModuloDAO getModuloDAO() {
		return moduloDAO;
	}

	protected MenuDAO getMenuDAO() {
		return menuDAO;
	}
	
	protected PerfilON getPerfilON() {
		return perfilON;
	}

	protected PerfilUsuarioON getPerfilUsuarioON() {
		return perfilUsuarioON;
	}

	protected UsuarioON getUsuarioON() {
		return usuarioON;
	}

	protected MenucrudON getMenucrudON() {
		return menucrudON;
	}

	protected ComponenteON getComponenteON() {
		return componenteON;
	}

	protected MenuON getMenuON() {
		return menuON;
	}

	protected AplicacaoON getAplicacaoON() {
		return aplicacaoON;
	}

	protected PermissaoON getPermissaoON() {
		return permissaoON;
	}

	protected SenhaON getSenhaON() {
		return senhaON;
	}

	@Override
	public List<PalavraChaveMenu> listarPalavrasChave(Integer menu) {
		return palavraChaveMenuDAO.listarPalavrasChave(menu);
	}

	@Override
	public PalavraChaveMenu obterPorChavePrimaria(PalavraChaveMenu palavra) {
		return palavraChaveMenuDAO.obterPorChavePrimaria(palavra.getId(), PalavraChaveMenu.Fields.MENU);
	}
	
	@Override
	public void persistir(PalavraChaveMenu palavra) {
		palavraChaveON.persistir(palavra);
	}
	
	@Override
	public void delete(PalavraChaveMenu palavra) {
		palavraChaveON.delete(palavra);
	}

	@Override
	public String getBaseUrlDocumentacao() {
		return acessoON.getBaseUrlDocumentacao();
	}
}