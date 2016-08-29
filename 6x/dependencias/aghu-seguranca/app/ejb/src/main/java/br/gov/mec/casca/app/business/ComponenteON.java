package br.gov.mec.casca.app.business;

import java.util.List;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.casca.CascaException;
import br.gov.mec.casca.app.dao.ComponenteDAO;
import br.gov.mec.casca.app.dao.MetodoDAO;
import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.casca.model.Componente;
import br.gov.mec.casca.model.Metodo;
import br.gov.mec.casca.model.PermissoesComponentes;
import br.gov.mec.seam.business.SeamContextsManager;
import br.gov.mec.seam.business.exception.NegocioExceptionCode;

class ComponenteON extends SeamContextsManager {

	public enum ComponenteONExceptionCode implements NegocioExceptionCode {
		CASCA_MENSAGEM_COMPONENTE_NAO_INFORMADA, CASCA_MENSAGEM_COMPONENTE_JA_CADASTRADA, CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO, CASCA_VIOLACAO_FK_COMPONENTE, CASCA_VIOLACAO_FK_METODO;
	}

	/***
	 * 
	 * @param nome
	 * @return
	 */
	public List<Componente> pesquisarComponentePorNome(Object nome) {
		ComponenteDAO componenteDAO = criaComponenteDAO();
		return componenteDAO.pesquisarComponentePorNome(nome);
	}
	/**
	 * @return
	 */
	protected ComponenteDAO criaComponenteDAO() {
		return new ComponenteDAO();
	}
	/***
	 * 
	 * @param nome
	 * @param componente 
	 * @return
	 */
	public List<Metodo> pesquisarActionPorNome(Object nome, Componente componente) {
		MetodoDAO metodoDAO = criaMetodoDAO();
		return metodoDAO.pesquisarMetodoPorNome(nome, componente);
	}
	/**
	 * @return
	 */
	protected MetodoDAO criaMetodoDAO() {
		return new MetodoDAO();
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
	public List<Componente> pesquisarTodosComponentes() {
		ComponenteDAO componenteDAO = criaComponenteDAO();
		return componenteDAO.pesquisarTodosComponentes();
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

		ComponenteDAO componenteDAO = criaComponenteDAO();
		return componenteDAO.pesquisarComponentes(nomeComponente, firstResult,
				maxResults, orderProperty, asc);
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
	public Integer pesquisarMetodosComponenteCount(Integer idComponente) {
		ComponenteDAO componenteDAO = criaComponenteDAO();
		return componenteDAO.pesquisarMetodosComponenteCount(idComponente);

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
	public List<Componente> pesquisarMetodosComponente(Integer idComponente,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		ComponenteDAO componenteDAO = criaComponenteDAO();
		return componenteDAO.pesquisarMetodosComponente(idComponente,
				firstResult, maxResults, orderProperty, asc);
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
	public Integer pesquisarComponentesCount(String nomeComponente) {
		ComponenteDAO componenteDAO = criaComponenteDAO();
		return componenteDAO.pesquisarComponentesCount(nomeComponente);
	}

	/**
	 * 
	 * @param aplicacao
	 * @throws CascaException
	 */
	public void salvar(Componente componente, Aplicacao aplicacao)
			throws CascaException {
		if (componente == null) {
			throw new CascaException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_COMPONENTE_NAO_INFORMADA);
		}
		if (aplicacao == null) {
			throw new CascaException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		componente.setAplicacao(aplicacao);
		ComponenteDAO componenteDAO = criaComponenteDAO();
		if (componente.getId() == null) {
			componenteDAO.inserir(componente);
		} else {
			componenteDAO.atualizar(componente);
		}
	}

	/**
	 * 
	 * @param idAplicacao
	 * @return
	 * @throws CascaException
	 */
	public Componente obterComponente(Integer idComponente)
			throws CascaException {
		if (idComponente == null) {
			throw new CascaException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		ComponenteDAO componenteDAO = criaComponenteDAO();
		return componenteDAO.obterPorChavePrimaria(idComponente);
	}

	/**
	 * 
	 * @param idAplicacao
	 * @throws CascaException
	 */
	public void excluirComponente(Integer idComponente)
			throws CascaException {
		if (idComponente == null) {
			throw new CascaException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		Componente componente = obterComponente(idComponente);
		try {
			ComponenteDAO componenteDAO = criaComponenteDAO();
			componenteDAO.remover(componente);
		} catch (PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new CascaException(
						ComponenteONExceptionCode.CASCA_VIOLACAO_FK_COMPONENTE,
						cve.getConstraintName());
			}
		}
	}

	/**
	 * 
	 * @param metodo
	 */
	public void salvarMetodo(Metodo metodo, Integer idComponente)
			throws CascaException {
		if (metodo == null||idComponente==null) {
			throw new CascaException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		metodo.setComponente(obterComponente(idComponente));
		ComponenteDAO componenteDAO = criaComponenteDAO();
		if (metodo.getId() == null) {
			componenteDAO.salvarMetodo(metodo);
		} else {
			componenteDAO.atualizarMetodo(metodo);
		}
	}

	/**
	 * 
	 * @param idMetodo
	 * @return
	 */
	public Metodo obterMetodo(Integer idMetodo) throws CascaException {
		if (idMetodo==null) {
			throw new CascaException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		ComponenteDAO componenteDAO = criaComponenteDAO();
		return componenteDAO.obterMetodo(idMetodo);
	}

	/**
	 * 
	 * @param idMetodo
	 * @throws CascaException
	 */
	public void excluirMetodo(Integer idMetodo) throws CascaException {
		if (idMetodo==null) {
			throw new CascaException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		Metodo metodo = obterMetodo(idMetodo);
		try {
			ComponenteDAO componenteDAO = criaComponenteDAO();
			componenteDAO.excluirMetodo(metodo);
		} catch (PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new CascaException(
						ComponenteONExceptionCode.CASCA_VIOLACAO_FK_METODO,
						cve.getConstraintName());
			}
		}
	}

	/**
	 * 
	 * 
	 * @param nome
	 * @return
	 */
	public List<PermissoesComponentes> pesquisarComponentesPermissao(
			Integer idPermissao, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		ComponenteDAO componenteDAO = criaComponenteDAO();
		return componenteDAO.pesquisarComponentesPermissao(idPermissao,
				firstResult, maxResults, orderProperty, asc);
	}

	public Integer pesquisarComponentesPermissaoCount(Integer idPermissao) {
		ComponenteDAO componteDao = criaComponenteDAO();
		return componteDao.pesquisarComponentesPermissaoCount(idPermissao);
	}

}
