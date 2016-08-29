package br.gov.mec.aghu.casca.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import br.gov.mec.aghu.casca.model.Acesso;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Dashboard;
import br.gov.mec.aghu.casca.model.Favorito;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.casca.model.Metodo;
import br.gov.mec.aghu.casca.model.Modulo;
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
import br.gov.mec.aghu.casca.model.PalavraChaveMenu;
import br.gov.mec.aghu.casca.security.vo.PermissaoVO;
import br.gov.mec.aghu.casca.vo.FiltroPerfilJnVO;
import br.gov.mec.aghu.casca.vo.FiltroPerfisUsuariosJnVO;
import br.gov.mec.aghu.casca.vo.FiltroUsuarioJnVO;
import br.gov.mec.aghu.casca.vo.MenuVO;
import br.gov.mec.aghu.casca.vo.ModuloVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAcesso;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.service.seguranca.BearerToken;

@Local
public interface ICascaFacade extends Serializable {

	/**
	 * 
	 */
	List<Componente> pesquisarComponentePorNome(Object nome);

	/**
	 * @param componente 
	 * 
	 */
	List<Metodo> pesquisarActionPorNome(Object nome, Componente componente);

	/**
	 * 
	 * @param nomeComponente
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	List<Componente> pesquisarComponentes(String nomeComponente,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc);

	/**
	 * 
	 * @param idComponente
	 * @return
	 */
	Long pesquisarMetodosComponenteCount(Integer idComponente);

	/**
	 * 
	 * @param idComponente
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	List<Metodo> pesquisarMetodosComponente(Integer idComponente,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc);

	/**
	 * 
	 * @param nomeComponente
	 * @return
	 */
	Long pesquisarComponentesCount(String nomeComponente);

	/**
	 * 
	 * @param idComponente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	Componente obterComponente(Integer idComponente) throws ApplicationBusinessException;

	/**
	 * 
	 * @param idComponente
	 * @throws ApplicationBusinessException
	 */
	void excluirComponente(Integer idComponente) throws ApplicationBusinessException;

	/**
	 * 
	 * @param idMetodo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	Metodo obterMetodo(Integer idMetodo) throws ApplicationBusinessException;

	/**
	 * 
	 * @param idPermissao
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	List<PermissoesComponentes> pesquisarComponentesPermissao(
			Integer idPermissao, String target, String action, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc);

	Long pesquisarComponentesPermissaoCount(Integer idPermissao, String target, String action);

	void enviarEmailChaveIdentificadora(UsuarioApi usuarioApi);
	
	void salvarUsuario(UsuarioApi usuarioApi) throws ApplicationBusinessException;
	
	List<UsuarioApi> pesquisarUsuariosApi(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String nome, String email, String loginHcpa, DominioSituacao situacao);
	
	Long pesquisarUsuariosApiCount(String nome, String email, String loginHcpa, DominioSituacao situacao);
	
	/**
	 * 
	 */
	
	List<Aplicacao> pesquisarAplicacaoPorNome(Object nome);

	void removerPerfisUsuario(PerfisUsuarios perfilUsuario) throws ApplicationBusinessException;
	
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
	
	List<Aplicacao> pesquisarAplicacoes(String servidor, Integer porta,
			String contexto, String nome, Boolean externo, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc);

	Long pesquisarPerfilApiCountSuggestionBox(String nome);
	
	BearerToken obterBearerToken(String authInfo, String origem);

	Boolean verificarTokenAtivo(String token);
	
	void reiniciarIdentificacaoAcesso(UsuarioApi usuarioApi) throws ApplicationBusinessException;
	
	UsuarioApi obterUsuarioApi(Integer seq);
	
	/**
	 * 
	 * @param aplicacao
	 * @throws ApplicationBusinessException
	 */
	
	void salvar(Aplicacao aplicacao) throws ApplicationBusinessException;

	/**
	 * 
	 * @param idAplicacao
	 * @return
	 * @throws ApplicationBusinessException
	 */

	
	Aplicacao obterAplicacao(Integer idAplicacao);

	void salvarPerfilApi(PerfilApi perfilApi) throws ApplicationBusinessException;
	
	void associaPerfisUsuarios(Usuario usuario, PerfisUsuarios perfilUsuario) throws ApplicationBusinessException;
		
	
	/**
	 * Retorna a aplicação conforme o contexto informado
	 * @param nomeContexto Nome do contexto web da aplicação
	 * @param servidor Nome do servidor web da aplicação
	 * @return Aplicação
	 */
	Aplicacao obterAplicacaoPorContexto(String contexto, String servidor );
	
	/**
	 * 
	 * @param servidor
	 * @param servidor
	 * @throws ApplicationBusinessException
	 */
	
	void excluirAplicacao(Integer idAplicacao) throws ApplicationBusinessException;

	/**
	 * 
	 */
	
	void salvarMenu(Menu menu) throws ApplicationBusinessException;

	void associarPerfilUsuarioApi(Integer idUsuario, List<PerfisUsuariosApi> listaPerfisUsuarios) throws ApplicationBusinessException;
	
	List<PerfisUsuariosApi> pequisarPerfisUsuariosApi(UsuarioApi usuarioApi);
	
	List<PerfilApi> pesquisarPerfisApiSuggestionBox(String nome);
	
	BearerToken refreshBearerToken(String authInfo, String origem);

	Long pesquisarPerfilApiCount(String nome, String descricao, DominioSituacao situacao);
	
	List<PerfilApi> pesquisarPerfilApi(Integer firstResult,	Integer maxResult, String orderProperty, boolean asc, String nome, String descricao, DominioSituacao situacao);
			
	Boolean verificarPerfilToken(String token, Set<String> rolesSet);
	/**
	 * 
	 * @param nomeMenu
	 * @param urlMenu
	 * @param idAplicacao
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 * @throws ApplicationBusinessException
	 */
	
	List<Menu> pesquisarMenu(String nomeMenu, String urlMenu,
			Integer idAplicacao, Integer idMenuPai, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	/**
	 * 
	 * @param nomeMenu
	 * @param urlMenu
	 * @param idAplicacao
	 * @return
	 */
	Long pesquisarMenucrudCount(String nomeMenu, String urlMenu,
			Integer idAplicacao, Integer idMenuPai);

	/**
	 * 
	 * @param objPesquisa
	 * @return
	 */
	
	List<Menu> pesquisarMenuPorNomeEId(String objPesquisa);

	/*
	 * 
	 */
	Menu obterMenu(Integer id) throws ApplicationBusinessException;

	/**
	 * 
	 * @param id
	 * @throws ApplicationBusinessException
	 */
	
	void deletarMenu(Integer id) throws ApplicationBusinessException;

	/**
	 * @author gandriotti
	 * @return
	 */

	PerfilApi obterPerfilApi(Integer seq);
	
	MenuVO recuperarMenuPorUrl(String url);

	List<Integer> recuperarMenusValidos(String loginUser);

	List<Menu> recuperarMenusValidos(String loginUser, String nome, Set<String> conjuntoModulosAtivos);
	
	List<Integer> recuperarMenusValidos(String loginUser, Set<String> conjuntoModulosAtivos);
	
	List<Favorito> obterFavoritos(String login) throws BaseException;

	Favorito criarFavorito(Menu menu, String login, Integer ordem)
			throws BaseException;

	void excluirFavorito( Integer id);
	
	void excluirFavoritoIdMenu(String usuarioLogado, Integer idMenu);

	// PerfilON
	/**
	 * Metodo responsavel por incluir ou alterar um perfil.
	 * 
	 * @param perfil
	 */
	void salvarPerfil(Perfil perfil) throws ApplicationBusinessException;

	/**
	 * Metodo responsavel por pesquisar usuarios.
	 * 
	 * @param nomeUsuario
	 * @param login
	 * @return
	 */
	List<Perfil> pesquisarPerfis(String nome);
	
	/**
	 * Busca o histórico do cadastro do perfil
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtroPerfilJnVO
	 * @return
	 */
	List<PerfilJn> pesquisarHistoricoPorPerfil(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroPerfilJnVO filtroPerfilJnVO);

	/**
	 * Quantidade de registros no histórico do cadastro do perfil
	 * 
	 * @param filtroPerfilJnVO
	 * @return
	 */	
	Long pesquisarHistoricoPorPerfilCount(FiltroPerfilJnVO filtroPerfilJnVO);

	/**
	 * Metodo responsavel por perfis para os suggestion Box
	 * 
	 * @param nomeUsuario
	 * @param login
	 * @return
	 */
	
	List<Perfil> pesquisarPerfisSuggestionBox(String nome)
			throws ApplicationBusinessException;

	/**
	 * Metodo responsavel por pesquisar usuarios.
	 * 
	 * @param nomeUsuario
	 * @param firstResult
	 * @param maxResult
	 * @param asc
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	
	List<Perfil> pesquisarPerfis(String nome, String descricaoResumida,
			Integer firstResult, Integer maxResult, boolean asc);

	/**
	 * 
	 * @param nome
	 * @return
	 */
	Long pesquisarPerfisCount(String nome);

	/**
	 * 
	 * @param nome
	 * @return
	 */
	Long pesquisarPerfilCountSuggestionBox(String nome);

	/**
	 * 
	 * @param id
	 * @return
	 */
	
	Perfil obterPerfil(Integer id) throws ApplicationBusinessException;

	/**
	 * 
	 * @param idUsuario
	 * @throws ApplicationBusinessException
	 */
	
	
	void excluirPerfil(Integer idPerfil) throws ApplicationBusinessException;

	/**
	 * Retorna uma lista dos perfis de um usuario
	 * 
	 * @param username
	 *            login do usuario
	 * @return Uma lista de strings com os perfis do usuario
	 */
	Set<String> obterNomePerfisPorUsuario(String username);

	/**
	 * 
	 * @param idUsuario
	 * @param listaPerfis
	 * @throws ApplicationBusinessException
	 */
	
	
	void associarPermissoesPerfil(Integer idPerfil,
			List<Permissao> listaPermissoes) throws ApplicationBusinessException;

	/**
	 * 
	 */
	
	void salvarPermissao(Permissao permissao) throws ApplicationBusinessException;

	/**
	 * 
	 * @param nome
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	
	List<Permissao> pesquisarPermissoes(String nome, String descricao,
			Modulo modulo, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc);

	/**
	 * 
	 * @param busca
	 * @param asc
	 * @return
	 */
	
	List<Permissao> pesquisarPermissoesSuggestionBox(String stringBusca);

	/**
	 * 
	 * @return
	 */
	
	List<Permissao> pesquisarTodasPermissoes();

	/**
	 * 
	 * @param nome
	 * @return
	 */
	Long pesquisarPermissoesCount(String nome);

	/**
	 * 
	 * @param nome
	 * @return
	 */
	Long pesquisarPermissoesCount(String nome, String descricao,
			Modulo modulo);

	/**
	 * 
	 * @param idPermissao
	 * @throws ApplicationBusinessException
	 */
	
	void excluir(Integer idPermissao) throws ApplicationBusinessException;

	/**
	 * 
	 * 
	 * @param idPermissaoComponente
	 * @throws ApplicationBusinessException
	 */
	
	void excluirComponentePermissao(Integer idPermissaoComponente)
			throws ApplicationBusinessException;

	/**
	 * 
	 * @param idPermissao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	
	Permissao obterPermissao(Integer idPermissao) throws ApplicationBusinessException;

	/**
	 * @deprecated o cache de permissoes pre carregaveis não será mais utilizado,
	 *  para o cache de permissões utilizar o CachePermissionStore
	 * @param aplicacoes
	 * @param targets
	 * @param actions
	 * @param login
	 * @return
	 */
	List<PermissaoVO> obterPermissoesPrecarregaveis(String aplicacoes,
			String targets, String actions, String login);

	/**
	 * 
	 * @param idPermissao
	 * @param idComponente
	 * @param contem
	 * @return
	 */
	
	List<Metodo> recuperaMetodosComponentePermissao(Integer idPermissao,
			Integer idComponente, boolean contem);

	/**
	 * 
	 * @param idPermissao
	 * @param idComponente
	 * @param listaDeMetodos
	 * @throws ApplicationBusinessException
	 */
	
	void associarPermissaoComponenteMetodos(Integer idPermissao,
			Integer idComponente, Metodo metodo) throws ApplicationBusinessException;

	List<Usuario> pesquisarUsuariosAssociaveis(String nomeOuLogin);

	/**
	 * 
	 */
	// FIXME Retirar do CASCA de uma vez por todas!	
	/*
	
	public void salvarUsuario(Usuario usuario,
			RapPessoasFisicas rapPessoasFisicas, RapServidores rapServidores,
			CseUsuarios cseUsuarios, Pessoa pessoa,
			RapQualificacao rapQualificacao,
			AghProfEspecialidades aghProfEspecialidades,
			AghEspecialidades aghEspecialidade, boolean isMedico)
			throws ApplicationBusinessException {
		getUsuarioON().salvarUsuario(usuario);
	}
	/**
	 * 
	 * @param usuario
	 * @throws ApplicationBusinessException
	 */
	
	void salvarUsuario(Usuario usuario) throws ApplicationBusinessException;

	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param nomeOuLogin
	 * @return
	 * @throws ApplicationBusinessException
	 */
	
	List<Usuario> pesquisarUsuarios(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, String nomeOuLogin)
			throws ApplicationBusinessException;

	Long pesquisarUsuariosCount(String nomeOuLogin);
	
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
	List<UsuarioJn> pesquisarHistoricoPorUsuario(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroUsuarioJnVO filtroUsuarioJnVO);

	/**
	 * Quantidade de registros no histórico do cadastro do usuário
	 * 
	 * @param filtroUsuarioJnVO
	 * @return
	 */
	Long pesquisarHistoricoPorUsuarioCount(FiltroUsuarioJnVO filtroUsuarioJnVO);
	
	/**
	 * Busca o histórico do cadastro do perfisUsuarios
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtroPerfisUsuariosJnVO
	 * @return
	 */
	List<PerfisUsuariosJn> pesquisarHistoricoPorPerfisUsuarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroPerfisUsuariosJnVO filtroPerfisUsuariosJnVO);

	/**
	 * Quantidade de registros no histórico do cadastro do perfisUsuarios
	 * 
	 * @param filtroPerfisUsuariosJnVO
	 * @return
	 */	
	Long pesquisarHistoricoPorPerfisUsuariosCount(FiltroPerfisUsuariosJnVO filtroPerfisUsuariosJnVO);
	
	
	List<Usuario> pesquisarUsuariosNaoCadastrados(Integer firstResult,
			Integer maxResult, String login);

	//
	Usuario recuperarUsuario(String login) throws ApplicationBusinessException;
	
	Usuario recuperarUsuario(String login, boolean ignorarMaiusculasMinusculas) throws ApplicationBusinessException;
	
	Usuario obterUsuarioAtivo(final String login) throws ApplicationBusinessException;

	
	Usuario obterUsuario(Integer id) throws ApplicationBusinessException;

	
	
	void associarPerfilUsuario(Integer idUsuario, List<PerfisUsuarios> listaPerfis)
			throws ApplicationBusinessException;

	
	void excluirUsuario(Integer idUsuario) throws ApplicationBusinessException;

	boolean verificarSenha(String login, String senha) throws ApplicationBusinessException;

	boolean alterarSenha(String login, String senhaAtual, String senhaNova) throws ApplicationBusinessException;

	/**
	 * Realiza as validações negociais para ver se o usuário informado por parâmetro
	 * pode logar no sistema AGHU.
	 * As verificações consistem em ver se o usuário existe e se está ativo.
	 * ATENÇÃO: NÃO colocar restrição de segurança neste método pois ele é chamado 
	 * no momento de login, quando o contexto de segurança ainda não está pronto.
	 * 
	 * @param login Login do usuário no sistema AGHU
	 * @throws ApplicationBusinessException Caso não exista ou esteja ativo
	 */
	void validarAutenticacaoNegocial(final String login) throws ApplicationBusinessException;

	/**
	 * Recupera todas as aplicações que o usuário tem acesso.
	 * 
	 * @return Um List de objetos AplicacaoVO
	 */
	
	/**
	 * Dado o login do usuario, verificar se ele tem uma determinada permissão
	 * 
	 * @param login
	 *            o login do usuário
	 * @param permissao
	 *            o nome da permissão a ser testada
	 * @return true se o usuario tem a permissão, false caso contrário
	 */
	boolean usuarioTemPermissao(String login, String permissao);

	/**
	 * Dado o login do usuario, verificar se ele tem permissão de acesso
	 * para o componente e metódo
	 * 
	 * @deprecated utilizar {@link ICascaFacade#temPermissao(String, String, String)}	  
	 * @param login O login do usuário
	 * @param componente Target
	 * @param metodo Action
	 * @return true se o usuário tem uma permissão associada ao componente e 
	 * método, false caso contrário
	 */
	boolean usuarioTemPermissao(String login, String componente, String metodo);

	/**
	 * Pesquisa, e retorna, todos os usuários com uma determinada permissão
	 * 
	 * @param permissao Nome da permissão
	 * @return Lista de usuários, ordenada por login, que possuem a permissão.
	 *         Caso a permissão não seja informada, retorna uma lista vazia.
	 */
	List<String> pesquisarUsuariosComPermissao(String permissao);

	/**
	 * Valida se é um token válido para determinar se o usuário está logado na aplicação.
	 * 
	 * @param cascaSessionId
	 * @param clientSessionId
	 * @param token
	 * @throws ApplicationBusinessException
	 */
	Boolean isLoggedIn(String cascaSessionId, String clientSessionId,
			String token, String urlLogout) throws ApplicationBusinessException;

	List<Modulo> listarModulosAtivos();
	
	List<Modulo> listarModulosPorNome(String nomeModulo);
	
	boolean verificarSeModuloEstaAtivo(String nomeModulo);


	Set<String> pesquisarUsuariosPorPerfil(String perfil);

	Long listarModulosCount(String strPesquisa);

	List<Modulo> listarModulos(String strPesquisa);

	boolean usuarioLogadoLiberadoDePendenciasBloqueantes() throws ApplicationBusinessException;

	boolean usuarioLiberadoDePendenciasBloqueantes(String usuario);

	void liberarAcessoParaAcessarSistemaComPendenciasBloqueantes(Usuario usuario)
			throws BaseException;

	void removerLiberacaoAcessoSistemaComPendenciasBloqueantes(Usuario usuario)
			throws BaseException;

	void removerPerfisAcessoSistemaComPendenciasBloqueantes(
			Date expiration, String cron) throws ApplicationBusinessException;

	Long listarModulosCount();

	List<ModuloVO> listarModulos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Set<String> conjuntoModulosAtivos);
	
	Modulo obterModulo(Integer idModulo);

	void alterarSituacaoModulo(Integer idModulo, Boolean situacaoModulo);
	
	List<Acesso> pesquisarAcessos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Usuario usuario, String enderecoOrigem,
			String mensagem, Boolean autorizado, DominioTipoAcesso tipoAcesso, Date dataInicial, Date dataFinal);

	Long pesquisarAcessosCount(Usuario usuario, String enderecoOrigem,
			String mensagem, Boolean autorizado, DominioTipoAcesso tipoAcesso, Date dataInicial, Date dataFinal);



	void registrarAcesso(String login, String enderecoOrigem, String mensagem,
			Boolean autorizado, DominioTipoAcesso tipoAcesso)
			throws ApplicationBusinessException;

	Boolean validarPermissaoPorServidorESeqProcesso(String login,
			Short seqProcesso);

	Boolean validarPermissaoConsultaPorServidorESeqProcesso(String login,
			Short seqProcesso);

	Boolean validarPermissaoExecutaPorServidorESeqProcesso(String login,
			Short seqProcesso);

	Boolean validarPermissaoPorServidorEProcessos(String login, Short pRocSeq,
			Short pEspSeq);

	Boolean validarPermissaoPorServidorERocSeq(String login, Short cRoqSeq);

	List<CseCategoriaProfissional> pesquisarCategoriaProfissional(
			RapServidores servidor);

	CseCategoriaProfissional primeiraCategoriaProfissional(
			RapServidores servidor);
	
	List<Menu> pesquisarMenusTabelasSistema(String parametro);

	Long pesquisarCountMenusTabelasSistema(String parametro);
	
	Boolean verificarMaximoTentativasAcessoUltimoMinuto(String enderecoOrigem) throws ApplicationBusinessException;
	
	void delegarPerfilUsuario(Usuario usuario, Perfil perfil, Usuario usuarioPerfil, Date dataExpiracao, String motivoDelegacao) throws ApplicationBusinessException;

	Boolean validarPermissaoPorServidorESeqProcessoEPrefixoPerfil(String login,
			Short seqProcesso, String prefixoPerfil);
	
	boolean usuarioTemPerfil(String login, String perfil);

	Long pesquisarAplicacoesCount(String servidor, Integer porta,
			String contexto, String nome, Boolean externo);

	Long pesquisarPerfisCount(String nome, String descricao);

	List<Permissao> obterPermissoesPorPerfil(Perfil perfil);

	void associarPermissoesPerfil(Perfil perfil,
			List<Permissao> listaPermissoes,
			List<Permissao> listaPermissoesExcluidas)
			throws ApplicationBusinessException;

	Long pesquisarPermissoesCountSuggestionBox(String consulta);

	Usuario obterUsuario(String login);
	
	public PerfisUsuarios obterPerfilUsuarioLogin(String usuario, String perfil);
	
	public void atribuirPerfilUsuario(final String login, final String perfilNome, final String usuarioLogado) throws ApplicationBusinessException;
	
	public void removerPerfilUsuario(final String login, final String perfilNome, final String usuarioLogado) throws ApplicationBusinessException;

	void excluirPermissao(Permissao permissao)
			throws ApplicationBusinessException;

	void alterarSenhaSemValidacarSenhaAtual(String login, String senhaNova)
			throws ApplicationBusinessException;

	List<PerfisUsuarios> listarPerfisPorUsuario(Usuario usuario);

	void validarSenha(String login, String senhaAtual)
			throws ApplicationBusinessException;

	void enviarTokenSenhaUsuario(String login, String token, String url)
			throws ApplicationBusinessException;

	Long pesquisarUsuariosNaoCadastradosCount(String login);

	List<PerfisUsuarios> pequisarPerfisUsuarios(Usuario usuario);
	
	List<PerfisUsuarios> pequisarPerfisUsuariosSemCache(Usuario usuario);

	void associarPerfilUsuarioPatologista(Integer usuario,
			List<PerfisUsuarios> listaPerfis, List<Perfil> perfisPatologista) throws ApplicationBusinessException;
	
	public PerfisUsuarios obterPerfilUsuario(String usuario, String perfil);

	boolean temPermissao(String login, String componente, String metodo);
	
	List<CseCategoriaProfissional> pesquisarListaCseCategoriaProfissional(Object filtro);

	Long pesquisarListaCseCategoriaProfissionalCount(Object filtro);

	/**
	 * #39003 - Serviço que busca ultima solicitacao de exames
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	Boolean existeServidorCategoriaProfMedico(Integer matricula, Short vinculo) throws ApplicationBusinessException;

	List<Menu> recuperarSubMenusValidos(Boolean force);

	List<Menu> recuperarMenus();

	List<Integer> obterIdMenuFavoritos(String login);

	void atualizarPosicaoFavorito(Integer menuId, String login, Integer ordem);

	Favorito obterFavoritoIdMenu(String login, Integer menuId);

	void atualizarDashdoard(Integer menuId, String modal, String login,
			Integer ordem) throws BaseException;

	List<Dashboard> obterDashBoard(String login);

	void excluirDashboard(String usuarioLogado, Integer menuId);
	
	List<PalavraChaveMenu> listarPalavrasChave(Integer menu);
	
	void persistir(PalavraChaveMenu palavra);
	
	void delete(PalavraChaveMenu palavra);
	
	public PalavraChaveMenu obterPorChavePrimaria(PalavraChaveMenu palavra);

	String getBaseUrlDocumentacao();
}