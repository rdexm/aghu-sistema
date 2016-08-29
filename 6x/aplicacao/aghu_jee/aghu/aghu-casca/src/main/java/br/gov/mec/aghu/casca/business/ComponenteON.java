package br.gov.mec.aghu.casca.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.casca.dao.ComponenteDAO;
import br.gov.mec.aghu.casca.dao.MetodoDAO;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Componente;
import br.gov.mec.aghu.casca.model.Metodo;
import br.gov.mec.aghu.casca.model.PermissoesComponentes;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ComponenteON extends BaseBusiness {


	private static final Log LOG = LogFactory.getLog(ComponenteON.class);
	private static final long serialVersionUID = 4080470880083092632L;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private ComponenteDAO componenteDAO;
	
	@Inject
	private MetodoDAO metodoDAO;

	
	public enum ComponenteONExceptionCode implements BusinessExceptionCode {
		CASCA_MENSAGEM_COMPONENTE_NAO_INFORMADA, CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO, CASCA_VIOLACAO_FK_COMPONENTE, CASCA_VIOLACAO_FK_METODO;
	}

	/***
	 * 
	 * @param nome
	 * @return
	 */
	public List<Componente> pesquisarComponentePorNome(Object nome) {
		return componenteDAO.pesquisarComponentePorNome(nome);
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
		return metodoDAO;
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
	public Long pesquisarMetodosComponenteCount(Integer idComponente) {
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
	public List<Metodo> pesquisarMetodosComponente(Integer idComponente,
			Integer firstResult, Integer maxResults, String orderProperty,	boolean asc) {
		return componenteDAO.pesquisarMetodosComponente(idComponente,firstResult, maxResults, orderProperty, asc);
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
	public Long pesquisarComponentesCount(String nomeComponente) {
		return componenteDAO.pesquisarComponentesCount(nomeComponente);
	}

	/**
	 * 
	 * @param aplicacao
	 * @throws ApplicationBusinessException
	 */
	public void salvar(Componente componente, Aplicacao aplicacao)
			throws ApplicationBusinessException {
		if (componente == null) {
			throw new ApplicationBusinessException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_COMPONENTE_NAO_INFORMADA);
		}
		if (aplicacao == null) {
			throw new ApplicationBusinessException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		componente.setAplicacao(aplicacao);
		if (componente.getId() == null) {
			componenteDAO.persistir(componente);
			componenteDAO.flush();
		} else {
			componenteDAO.atualizar(componente);
			componenteDAO.flush();
		}
	}

	/**
	 * 
	 * @param idAplicacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Componente obterComponente(Integer idComponente)
			throws ApplicationBusinessException {
		if (idComponente == null) {
			throw new ApplicationBusinessException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		return componenteDAO.obterPorChavePrimaria(idComponente);
	}

	/**
	 * 
	 * @param idAplicacao
	 * @throws ApplicationBusinessException
	 */
	public void excluirComponente(Integer idComponente)
			throws ApplicationBusinessException {
		if (idComponente == null) {
			throw new ApplicationBusinessException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		Componente componente = obterComponente(idComponente);
		try {
			componenteDAO.remover(componente);
			componenteDAO.flush();
		} catch (PersistenceException ce) {
			logError("Exceção capturada: ", ce);
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new ApplicationBusinessException(
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
			throws ApplicationBusinessException {
		if (metodo == null||idComponente==null) {
			throw new ApplicationBusinessException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		metodo.setComponente(obterComponente(idComponente));
		if (metodo.getId() == null) {
			metodoDAO.salvarMetodo(metodo);
		} else {
			metodoDAO.atualizarMetodo(metodo);
		}
	}

	/**
	 * 
	 * @param idMetodo
	 * @return
	 */
	public Metodo obterMetodo(Integer idMetodo) throws ApplicationBusinessException {
		if (idMetodo==null) {
			throw new ApplicationBusinessException(	ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		return metodoDAO.obterPorChavePrimaria(idMetodo);
	}

	/**
	 * 
	 * @param idMetodo
	 * @throws ApplicationBusinessException
	 */
	public void excluirMetodo(Integer idMetodo) throws ApplicationBusinessException {
		if (idMetodo==null) {
			throw new ApplicationBusinessException(
					ComponenteONExceptionCode.CASCA_MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		Metodo metodo = obterMetodo(idMetodo);
		try {
			metodoDAO.excluirMetodo(metodo);
		} catch (PersistenceException ce) {
			logError("Exceção capturada: ", ce);
			if (ce.getCause() instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) ce
						.getCause();
				throw new ApplicationBusinessException(
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
			Integer idPermissao, String target, String action,
			Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		return componenteDAO.pesquisarComponentesPermissao(idPermissao, target, action,
				firstResult, maxResults, orderProperty, asc);
	}

	public Long pesquisarComponentesPermissaoCount(Integer idPermissao, String target, String action) {
		return componenteDAO.pesquisarComponentesPermissaoCount(idPermissao, target, action);
	}

}
