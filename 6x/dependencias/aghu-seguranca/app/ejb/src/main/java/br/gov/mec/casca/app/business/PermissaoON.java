package br.gov.mec.casca.app.business;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.app.dao.PermissaoDAO;
import br.gov.mec.casca.model.Componente;
import br.gov.mec.casca.model.Metodo;
import br.gov.mec.casca.model.Permissao;
import br.gov.mec.casca.model.PermissoesComponentes;
import br.gov.mec.casca.security.vo.PermissaoVO;
import br.gov.mec.seam.business.SeamContextsManager;
import br.gov.mec.seam.business.exception.NegocioExceptionCode;

class PermissaoON extends SeamContextsManager {

	protected enum PermissaoONExceptionCode implements NegocioExceptionCode {
		CASCA_MENSAGEM_PERMISSAO_NAO_INFORMADO, CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO, CASCA_MENSAGEM_SUCESSO_INCLUSAO_PERMISSAO, CASCA_MENSAGEM_PERMISSAO_EXISTENTE, CASCA_MENSAGEM_PERMISSAO_NAO_EXISTENTE, CASCA_VIOLACAO_FK_PERMISSAO, CASCA_MENSAGEM_ACTION_NAO_INFORMADA, CASCA_MENSAGEM_ACTION_RELACIONADA, CASCA_MENSAGEM_PERMISSAO_EXISTENTE_PERFIL, CASCA_MENSAGEM_COMPONENTE_EXISTENTE_PERMISSAO;
	}

	/**
	 * Metodo responsavel por salvar um determinada permissao.
	 * 
	 * @param permissao
	 * @throws CascaException
	 */
	public void salvarPermissao(Permissao permissao) throws CascaException {

		if (permissao == null) {
			throw new CascaException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_INFORMADO);
		}

		PermissaoDAO permissaoDAO = getPermissaoDAO();
		if (permissao.getId() == null) { // inclusao
			if (isPermissaoExistente(null, permissao.getNome())) {
				throw new CascaException(
						PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_EXISTENTE);
			}
			permissao.setDataCriacao(new Date());
			permissaoDAO.inserir(permissao);
		} else { // alteracao
			if (isPermissaoExistente(permissao.getId(), permissao.getNome())) {
				throw new CascaException(
						PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_EXISTENTE);
			}
			permissaoDAO.atualizar(permissao);
		}
	}

	/**
	 * @return
	 */
	protected PermissaoDAO getPermissaoDAO() {
		return new PermissaoDAO();
	}

	/**
	 * Metodo responsavel por realizara pesquisar de uma ou mais permissoes
	 * informadas pelo usuario.
	 * 
	 * @param nome
	 * @return
	 */
	public List<Permissao> pesquisarPermissoes(String nome, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {

		return getPermissaoDAO().pesquisarPermissoes(nome, firstResult, maxResults, orderProperty,
				asc);
	}

	/**
	 * Metodo responsavel por posquisar por nome ou descrição informadas pelo
	 * usuario.
	 * 
	 * @param nome
	 * @return
	 */
	List<Permissao> pesquisarPermissoesSuggestionBox(String stringBusca) {

		return getPermissaoDAO().pesquisarPermissoesSuggestionBox(stringBusca);
	}

	/**
	 * Metodo responsavel por realizara pesquisar de uma ou mais permissoes
	 * informadas pelo usuario.
	 * 
	 * @param nome
	 * @return
	 */
	List<Permissao> pesquisarTodasPermissoes() {
		return getPermissaoDAO().pesquisarTodasPermissoes();
	}

	/**
	 * Metodo responsavel por retornar a quantidade de registros encontrados,
	 * para a paginacao.
	 * 
	 * @param nome
	 * @return
	 */
	Integer pesquisarPermissoesCount(String nome) {
		return getPermissaoDAO().pesquisarPermissoesCount(nome);
	}

	/**
	 * Metodo responsavel por retornar a quantidade de registros encontrados,
	 * para a paginacao.
	 * 
	 * @param consulta
	 * @return
	 */
	Integer pesquisarPermissoesCountSuggestionBox(String consulta) {
		return getPermissaoDAO().pesquisarPermissoesCountSuggestionBox(consulta);
	}

	/**
	 * Metodo responsavel por realizar a exclusao de uma determinada Permissao
	 * selecionada pelo usuario.
	 * 
	 * @param idPermissao
	 */
	void excluir(Integer idPermissao) throws CascaException {
		if (idPermissao == null) {
			throw new CascaException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_INFORMADO);
		}
		PermissaoDAO permissaoDAO = getPermissaoDAO();
		// Permissao permissao = entityManager.find(Permissao.class,
		// idPermissao);
		Permissao permissao = permissaoDAO.obterPorChavePrimaria(idPermissao);
		if (permissao == null) {
			throw new CascaException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_EXISTENTE);
		}
		if (permissao.getPerfisPermissoeses() == null
				|| permissao.getPerfisPermissoeses().isEmpty()) {
			throw new CascaException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_EXISTENTE_PERFIL);
		}
		if (permissao.getPermissoesComponenteses() == null
				|| permissao.getPermissoesComponenteses().isEmpty()) {
			throw new CascaException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_COMPONENTE_EXISTENTE_PERMISSAO);
		}

		try {
			// entityManager.remove(permissao);
			// entityManager.flush();
			permissaoDAO.remover(permissao);
		} catch (PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new CascaException(PermissaoONExceptionCode.CASCA_VIOLACAO_FK_PERMISSAO,
						cve.getConstraintName());
			}
		}
	}

	void excluirComponentePermissao(Integer idPermissaoComponente) throws CascaException {
		PermissaoDAO permissaoDAO = getPermissaoDAO();
		PermissoesComponentes permissoesComponentes = permissaoDAO
				.obterComponentePermissao(idPermissaoComponente);

		// if (permissoesComponentes == null) {
		// throw new CascaException(
		// PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_EXISTENTE);
		// }

		try {
			permissaoDAO.excluirPermissoesComponentes(permissoesComponentes);
		} catch (PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new CascaException(PermissaoONExceptionCode.CASCA_VIOLACAO_FK_PERMISSAO,
						cve.getConstraintName());
			}
		}
	}

	/**
	 * 
	 * @param idPermissao
	 * @return
	 * @throws CascaException
	 */
	Permissao obterPermissao(Integer idPermissao) throws CascaException {
		if (idPermissao == null) {
			throw new CascaException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_INFORMADO);
		}
		PermissaoDAO permissaoDAO = getPermissaoDAO();
		Permissao permissao = permissaoDAO.obterPorChavePrimaria(idPermissao);

		if (permissao == null) {
			throw new CascaException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_EXISTENTE);
		}
		return permissao;
	}

	// Metodo utilizado pelo CascaPermissionStore. Nao pode ter restrição,
	// senão o PermissionStore vai entrar num loop recursivo infinito, gerando
	// um StackOverflow
	List<PermissaoVO> obterPermissoes(String aplicacao, String target, String action) {
		
		List<PermissaoVO> preloadedPermissionList = getPreloadedPermissions().getPermissions(aplicacao, target, action);
		
		if (preloadedPermissionList != null) {
			return preloadedPermissionList;
		}
		
		PermissaoDAO permissaoDAO = getPermissaoDAO();
		return permissaoDAO.obterPermissoes(aplicacao, target, action);
	}
	
	protected PreloadedPermissions getPreloadedPermissions() {
		return obterDoContexto(PreloadedPermissions.class);
	}
	
	// Metodo utilizado pelo CascaPermissionStore. Nao pode ter restrição,
	// senão o PermissionStore vai entrar num loop recursivo infinito, gerando
	// um StackOverflow
	List<PermissaoVO> obterPermissoesPrecarregaveis(String aplicacoes, String targets, String actions) {
		PermissaoDAO permissaoDAO = getPermissaoDAO();
		return permissaoDAO.obterPermissoesPrecarregaveis(aplicacoes, targets, actions);
	}

	/**
	 * Retorna uma lista de metodos de um componente que existem ou nao em uma
	 * permissao.
	 * 
	 * @param idPermissao
	 *            id da permissao
	 * @param idComponente
	 *            id do componente
	 * @param contem
	 *            true para retornar os metodos que existem na permissao e false
	 *            para retornar os metodos que nao existem
	 * @return
	 */
	List<Metodo> recuperaMetodosComponentePermissao(Integer idPermissao,
			Integer idComponente, boolean contem) {

		return getPermissaoDAO().recuperaMetodosComponentePermissao(idPermissao, idComponente,
				contem);
	}

	/**
	 * 
	 * @param idPermissao
	 * @param idComponente
	 * @param listaDeMetodos
	 */
	void associarPermissaoComponenteMetodos(Integer idPermissao, Integer idComponente,
			Metodo metodo) throws CascaException {

		if (idPermissao == null) {
			throw new CascaException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		if (idComponente == null) {
			throw new CascaException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		if (metodo == null) {
			throw new CascaException(PermissaoONExceptionCode.CASCA_MENSAGEM_ACTION_NAO_INFORMADA);
		}

		ComponenteON componenteON = new ComponenteON();
		Componente componente = componenteON.obterComponente(idComponente);
		Permissao permissao = obterPermissao(idPermissao);
		validaUnicidadeAction(permissao, metodo);
		PermissaoDAO permissaoDAO = getPermissaoDAO();
		permissaoDAO.associarPermissaoComponenteMetodos(permissao, componente, metodo);
		permissaoDAO.flush();
	}

	/**
	 * @param permissao
	 * @param metodo
	 * @throws CascaException 
	 */
	private void validaUnicidadeAction(Permissao permissao, Metodo metodo) throws CascaException {
		Set<PermissoesComponentes> permissoesComponenteses = permissao.getPermissoesComponenteses();
		for (Iterator iterator = permissoesComponenteses.iterator(); iterator.hasNext();) {
			PermissoesComponentes permissoesComponentes = (PermissoesComponentes) iterator.next();
			if (permissoesComponentes.getMetodo().getId().equals(metodo.getId())) {
				throw new CascaException(PermissaoONExceptionCode.CASCA_MENSAGEM_ACTION_RELACIONADA);
			}

		}
	}

	/**
	 * 
	 * @param idPermissao
	 * @param nome
	 * @return
	 */
	private boolean isPermissaoExistente(Integer idPermissao, String nome) {

		Permissao permissao = getPermissaoDAO().obterPermissao(nome);

		// validacao para edicao
		if (permissao != null && idPermissao != null) {
			if (permissao.getId().equals(idPermissao)) {
				return false;
			}
			return true;
		}
		return permissao == null ? false : true;
	}

	boolean usuarioTemPermissao(String login, String permissao) {
		Permissao perm = getPermissaoDAO().obterPermissao(permissao, login);
		return perm != null;
	}

	boolean usuarioTemPermissao(String login, String componente, String metodo) {
		List<PermissoesComponentes> perms = getPermissaoDAO().obterPermissao(login, componente,
				metodo);
		return perms != null && !perms.isEmpty();
	}

}
