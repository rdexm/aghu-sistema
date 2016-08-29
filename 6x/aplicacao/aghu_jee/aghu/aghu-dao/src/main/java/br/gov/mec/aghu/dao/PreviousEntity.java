package br.gov.mec.aghu.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.core.dao.IPreviousEntity;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.utils.ModelUtil;

/**
 * Classe para a busca do objeto diretamente do banco de dados.
 * 
 * @author cvagheti
 * @deprecated usar PreviousEntitySearcher
 */
@Stateless
public class PreviousEntity implements IPreviousEntity {

	private static final Log LOG = LogFactory.getLog(PreviousEntity.class);

	@PersistenceContext(name = "aghu-pu")
	private EntityManager em;

	/**
	 * Método asincrono para retornar entidade do banco de dados sem
	 * interferência da transação que o chama.<br />
	 * Sendo asincrono a consulta será realizada em outra thread/transação com
	 * outra conexão ao banco de dados, evitando retornar dados que ainda não
	 * foram comitados pela transação que o chamou.
	 * 
	 * @param clazz
	 * @param id
	 * @return
	 */
	@Asynchronous
	public <E extends BaseEntity> Future<E> getPreviousAsync(Class<E> clazz,
			Serializable id) {
		E result = null;

		if (clazz != null) {
			if (id != null) {
				result = (E) this.get(clazz, id);
			}
		}

		return new AsyncResult<E>(result);
	}

	/**
	 * 
	 */
	public <E extends BaseEntity> E getPrevious(Class<E> clazz, Serializable id) {
		Future<E> previousAsync = getPreviousAsync(clazz, id);
		E result = null;
		try {
			result = previousAsync.get();
		} catch (InterruptedException | ExecutionException e) {
			LOG.error("Erro ao obter o id da entidade", e);
		}
		return result;
	}

	/**
	 * Retorna um stateless session.
	 * 
	 * @return
	 */
	private StatelessSession getStatelessSession() {
		return em.unwrap(Session.class).getSessionFactory()
				.openStatelessSession();
	}

	/**
	 * Retorna pelo id com as propriedades inicializadas.
	 * 
	 * @param clazz
	 * @param valueId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <E extends BaseEntity> E get(Class<E> clazz, Serializable id) {
		Criteria criteria = this.getStatelessSession().createCriteria(clazz);

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (ModelUtil.isModelField(field)) {
				criteria.setFetchMode(field.getName(), FetchMode.JOIN);
			}
		}

		criteria.add(Restrictions.idEq(id));

		return (E) criteria.uniqueResult();

	}

}
