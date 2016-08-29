package br.gov.mec.aghu.casca.business;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.casca.dao.PermissaoDAO;
import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Metodo;
import br.gov.mec.aghu.casca.model.Modulo;
import br.gov.mec.aghu.casca.model.Permissao;
import br.gov.mec.aghu.casca.model.PermissoesComponentes;
import br.gov.mec.aghu.casca.security.vo.PermissaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PermissaoON extends BaseBusiness {

	@EJB
	private ComponenteON componenteON;
	
	private static final Log LOG = LogFactory.getLog(PermissaoON.class);

	@Inject
	private PermissaoDAO permissaoDAO;

	private static final long serialVersionUID = 707977917289428636L;

	protected enum PermissaoONExceptionCode implements BusinessExceptionCode {
		CASCA_MENSAGEM_PERMISSAO_NAO_INFORMADO, CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO, CASCA_MENSAGEM_PERMISSAO_EXISTENTE, CASCA_MENSAGEM_PERMISSAO_NAO_EXISTENTE, CASCA_VIOLACAO_FK_PERMISSAO, CASCA_MENSAGEM_ACTION_NAO_INFORMADA, CASCA_MENSAGEM_ACTION_RELACIONADA, CASCA_MENSAGEM_PERMISSAO_EXISTENTE_PERFIL, CASCA_MENSAGEM_COMPONENTE_EXISTENTE_PERMISSAO;
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	/**
	 * Metodo responsavel por salvar um determinada permissao.
	 * 
	 * @param permissao
	 * @throws ApplicationBusinessException
	 */
	public void salvarPermissao(Permissao permissao) throws ApplicationBusinessException {

		if (permissao == null) {
			throw new ApplicationBusinessException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_INFORMADO);
		}

		if (permissao.getId() == null) { // inclusao
			if (isPermissaoExistente(null, permissao.getNome())) {
				throw new ApplicationBusinessException(
						PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_EXISTENTE);
			}
			permissao.setDataCriacao(new Date());
			getPermissaoDAO().persistir(permissao);
		} else { // alteracao
			if (isPermissaoExistente(permissao.getId(), permissao.getNome())) {
				throw new ApplicationBusinessException(PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_EXISTENTE);
			}
			getPermissaoDAO().merge(permissao);
		}
	}

	/**
	 * @return
	 */
	protected PermissaoDAO getPermissaoDAO() {	
		return  permissaoDAO;  
	}

	/**
	 * Metodo responsavel por realizara pesquisar de uma ou mais permissoes
	 * informadas pelo usuario.
	 * 
	 * @param nome
	 * @return
	 */
	public List<Permissao> pesquisarPermissoes(String nome, String descricao, Modulo modulo, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {

		return getPermissaoDAO().pesquisarPermissoes(nome, descricao, modulo, firstResult, maxResults, orderProperty,
				asc);
	}

	/**
	 * Metodo responsavel por posquisar por nome ou descrição informadas pelo
	 * usuario.
	 * 
	 * @param nome
	 * @return
	 */
	public List<Permissao> pesquisarPermissoesSuggestionBox(String stringBusca) {

		return getPermissaoDAO().pesquisarPermissoesSuggestionBox(stringBusca);
	}

	/**
	 * Metodo responsavel por realizara pesquisar de uma ou mais permissoes informadas pelo usuario.
	 */
	public List<Permissao> pesquisarTodasPermissoes() {
		return getPermissaoDAO().pesquisarTodasPermissoes();
	}
	
	/**
	 * Metodo responsavel por retornar a quantidade de registros encontrados, para a paginacao.
	 */
	public Long pesquisarPermissoesCount(String nome, String descricao, Modulo modulo) {
		return getPermissaoDAO().pesquisarPermissoesCount(nome, descricao, modulo);
	}

	/**
	 * Metodo responsavel por realizar a exclusao de uma determinada Permissao selecionada pelo usuario.
	 */
	public void excluir(Integer idPermissao) throws ApplicationBusinessException {
		if (idPermissao == null) {
			throw new ApplicationBusinessException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_INFORMADO);
		}
		
		Permissao permissao = getPermissaoDAO().obterPorChavePrimaria(idPermissao);
		if (permissao == null) {
			throw new ApplicationBusinessException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_EXISTENTE);
		}
		if (permissao.getPerfisPermissoeses() == null
				|| permissao.getPerfisPermissoeses().isEmpty()) {
			throw new ApplicationBusinessException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_EXISTENTE_PERFIL);
		}
		if (permissao.getPermissoesComponenteses() == null
				|| permissao.getPermissoesComponenteses().isEmpty()) {
			throw new ApplicationBusinessException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_COMPONENTE_EXISTENTE_PERMISSAO);
		}

		try {
			// entityManager.remove(permissao);
			// entityManager.flush();
			getPermissaoDAO().remover(permissao);
			getPermissaoDAO().flush();
		} catch (PersistenceException ce) {
			LOG.error("Exceção capturada: ", ce);
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new ApplicationBusinessException(PermissaoONExceptionCode.CASCA_VIOLACAO_FK_PERMISSAO,
						cve.getConstraintName());
			}
		}
	}

	public void excluirComponentePermissao(Integer idPermissaoComponente) throws ApplicationBusinessException {
		PermissaoDAO permDAO = getPermissaoDAO();
		PermissoesComponentes permissoesComponentes = permDAO
				.obterComponentePermissao(idPermissaoComponente);

		// if (permissoesComponentes == null) {
		// throw new ApplicationBusinessException(
		// PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_EXISTENTE);
		// }

		try {
			permDAO.excluirPermissoesComponentes(permissoesComponentes);
		} catch (PersistenceException ce) {
			LOG.error("Exceção capturada: ", ce);
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new ApplicationBusinessException(PermissaoONExceptionCode.CASCA_VIOLACAO_FK_PERMISSAO,
						cve.getConstraintName());
			}
		}
	}

	public Permissao obterPermissao(Integer idPermissao) throws ApplicationBusinessException {
		if (idPermissao == null) {
			throw new ApplicationBusinessException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_INFORMADO);
		}
		Permissao permissao = getPermissaoDAO().obterPorChavePrimaria(idPermissao);

		if (permissao == null) {
			throw new ApplicationBusinessException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_EXISTENTE);
		}
		return permissao;
	}

	// Metodo utilizado pelo CascaPermissionStore. Nao pode ter restrição,
	// senão o PermissionStore vai entrar num loop recursivo infinito, gerando
	// um StackOverflow
//	public List<PermissaoVO> obterPermissoes(String aplicacao, String target, String action) {
//		
//		List<PermissaoVO> preloadedPermissionList = getPreloadedPermissions().getPermissions(aplicacao, target, action);
//		
//		if (preloadedPermissionList != null) {
//			return preloadedPermissionList;
//		}
//		
//		return getPermissaoDAO().obterPermissoes(aplicacao, target, action);
//	}
	
	
	// Metodo utilizado pelo CascaPermissionStore. Nao pode ter restrição,
	// senão o PermissionStore vai entrar num loop recursivo infinito, gerando
	// um StackOverflow
	public List<PermissaoVO> obterPermissoesPrecarregaveis(String aplicacoes, String targets, String actions, String login) {
		return getPermissaoDAO().obterPermissoesPrecarregaveis(aplicacoes, targets, actions, login);
	}

	/**
	 * Retorna uma lista de metodos de um componente que existem ou nao em uma permissao.
	 * 
	 * @param idPermissao id da permissao
	 * @param idComponente id do componente
	 * @param contem
	 *            true para retornar os metodos que existem na permissao e false
	 *            para retornar os metodos que nao existem
	 * @return
	 */
	public List<Metodo> recuperaMetodosComponentePermissao(Integer idPermissao,
			Integer idComponente, boolean contem) {

		return getPermissaoDAO().recuperaMetodosComponentePermissao(idPermissao, idComponente,
				contem);
	}

	public void associarPermissaoComponenteMetodos(Integer idPermissao, Integer idComponente,
			Metodo metodo) throws ApplicationBusinessException {

		if (idPermissao == null) {
			throw new ApplicationBusinessException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		if (idComponente == null) {
			throw new ApplicationBusinessException(
					PermissaoONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		if (metodo == null) {
			throw new ApplicationBusinessException(PermissaoONExceptionCode.CASCA_MENSAGEM_ACTION_NAO_INFORMADA);
		}

		Componente componente = componenteON.obterComponente(idComponente);
		Permissao permissao = obterPermissao(idPermissao);
		validaUnicidadeAction(permissao, metodo);
		PermissaoDAO permDAO = getPermissaoDAO();
		permDAO.associarPermissaoComponenteMetodos(permissao, componente, metodo);
		permDAO.flush();
	}

	/**
	 * @param permissao
	 * @param metodo
	 * @throws ApplicationBusinessException 
	 */
	public void validaUnicidadeAction(Permissao permissao, Metodo metodo) throws ApplicationBusinessException {
		Set<PermissoesComponentes> permissoesComponenteses = permissao.getPermissoesComponenteses();
		for (Iterator iterator = permissoesComponenteses.iterator(); iterator.hasNext();) {
			PermissoesComponentes permissoesComponentes = (PermissoesComponentes) iterator.next();
			if (permissoesComponentes.getMetodo().getId().equals(metodo.getId())) {
				throw new ApplicationBusinessException(PermissaoONExceptionCode.CASCA_MENSAGEM_ACTION_RELACIONADA);
			}

		}
	}

	/**
	 * 
	 * @param idPermissao
	 * @param nome
	 * @return
	 */
	public boolean isPermissaoExistente(Integer idPermissao, String nome) {

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
	
	/**
	 * Metodo responsavel por retornar a quantidade de registros encontrados,
	 * para a paginacao.
	 * 
	 * @param consulta
	 * @return
	 */
	public Long pesquisarPermissoesCountSuggestionBox(String consulta) {
		return permissaoDAO.pesquisarPermissoesCountSuggestionBox(consulta);
	}
	
	/**
	 * Metodo responsavel por realizar a exclusao de uma determinada Permissao
	 * selecionada pelo usuario.
	 * 
	 * @param idPermissao
	 */
	public void excluirPermissao(Permissao permissao) throws ApplicationBusinessException {
		if (permissao == null) {
			throw new ApplicationBusinessException(PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_NAO_EXISTENTE);
		}
		
		permissao=permissaoDAO.obterPorChavePrimaria(permissao.getId());
		
		if (permissao == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		if (permissao.getPerfisPermissoeses() != null && !permissao.getPerfisPermissoeses().isEmpty()) {
			throw new ApplicationBusinessException(PermissaoONExceptionCode.CASCA_MENSAGEM_PERMISSAO_EXISTENTE_PERFIL);
		}
		if (permissao.getPermissoesComponenteses() != null
				&& !permissao.getPermissoesComponenteses().isEmpty()) {
			throw new ApplicationBusinessException(PermissaoONExceptionCode.CASCA_MENSAGEM_COMPONENTE_EXISTENTE_PERMISSAO);
		}

		try {
			permissaoDAO.remover(permissao);
			permissaoDAO.flush();
		} catch (PersistenceException ce) {
			LOG.error("Exceção capturada: ", ce);
			if (ce.getCause() instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(PermissaoONExceptionCode.CASCA_VIOLACAO_FK_PERMISSAO);
			}
		}
	}

	/**
	 * @deprecated usar {@link PermissaoDAO#temPermissao(String, String, String)
	 * @param login
	 * @param componente
	 * @param metodo
	 * @return
	 */
	public boolean usuarioTemPermissao(String login, String permissao) {
		Permissao perm = getPermissaoDAO().obterPermissao(permissao, login);
		return perm != null;
	}

	public boolean usuarioTemPermissao(String login, String componente, String metodo) {
		List<PermissoesComponentes> perms = getPermissaoDAO().obterPermissao(login, componente,
				metodo);
		return perms != null && perms.size() > 0;
	}

}