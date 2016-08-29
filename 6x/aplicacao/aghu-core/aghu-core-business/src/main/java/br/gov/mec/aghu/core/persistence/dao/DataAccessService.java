package br.gov.mec.aghu.core.persistence.dao;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Cache;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.jdbc.Work;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.jpa.FullTextQuery;

import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException.BaseOptimisticLockExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.persistence.BaseEntity;

@SuppressWarnings({"PMD.AghuTooManyMethods"})
public class DataAccessService implements Serializable {

	private static final long serialVersionUID = 5369896821553929582L;
	private static final Log LOG = LogFactory.getLog(DataAccessService.class);
	
    @PersistenceContext(name="aghu-pu")
    private EntityManager em;	

    /**
     * Retrieves a session instance  
    **/
    private Session getSession() {
    	return (Session) em.unwrap(Session.class);
    }
    
    public SessionImplementor getSessionImplementor() {
    	return (SessionImplementor) em.getDelegate();
    }
    
    public FullTextQuery createFullTextQuery(org.apache.lucene.search.Query query, Class<?> clazzName) {
    	return org.hibernate.search.jpa.Search.getFullTextEntityManager(em).createFullTextQuery(query, clazzName);
    }
    
    public String getIdProperty(Class entityClass) {
    	return getSession().getSessionFactory().getClassMetadata(entityClass).getIdentifierPropertyName();
   	}
    
    public StatelessSession getStatelessSession() {
    	return getSession().getSessionFactory().openStatelessSession();
   	}
    
    /**
	 * Retorna o dialeto definido no hibernate.properties da conf do JBoss
	 * 
	 * @return
	 */
	public String getDialectClassName() {
		SessionImplementor sessionImplementor = (SessionImplementor) this.em.getDelegate();
		String dialeto = sessionImplementor.getFactory().getDialect().getClass().getName();
		
		if (dialeto == null || !(dialeto.contains("Oracle") || dialeto.contains("PostgreSQL") || dialeto.contains("HSQL"))) {
			throw new IllegalStateException("Dialeto fora do padrão: " + dialeto + ". Verifique o arquivo hibernate.properties na conf do JBoss");
		}
		
		return dialeto;
	}
    
    /**
     * Stores an instance of the entity class in the database
     * @param T Object
     * @return 
     */
    public <T extends BaseEntity> void persist(T object) {
        this.em.persist(object);
    }
    
    /**
     * Retrieves an entity instance that was previously persisted to the database 
     * @param T Object
     * @param id
     * @return 
     */
    public <T extends BaseEntity> T find(Object id, Class<T> type) {
        return this.em.find(type, id);
    }

    /**
     * Removes the record that is associated with the entity instance
     * @param type
     * @param id 
     
    public <T extends BaseEntity> void delete(Object id, Class<T> type) {
        Object ref = this.em.getReference(type, id);
        this.em.remove(ref);
    }
    */

    /**
	 * Returna uma instancia persistente de uma dada classe com um dado id, 
	 * assume a instancia existe. Este metodo executa um entityManager.find<br>
	 * 
	 * Voce nao deve usar este metodo para determinar se uma instancia existe (use find para isso).
	 * Use este somente para carregar um instancia que voce assume que existe, quando nao existe serah gerado um erro.
	 * 
	 * Se id ou type nao forem informados retorna erro.
	 *  
	 * @param id Identificador valido de um instancia existente da classe
	 * @param type um tipo de classe persistente
	 * @return
	 * 
	 * @see BaseRuntimeException
	 * @see ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO
	 * @see ApplicationBusinessExceptionCode.REGISTRO_NULO_LOAD
	 */
	public <T extends BaseEntity> T load(Object id, Class<T> type) {
		if (id == null || type == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, "Parametro obrigatorio nao informado!");
		}
		
		T entityLoad = this.em.find(type, id);
		
		if (entityLoad == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_BUSCA);
		}
		
		return entityLoad;
	} 
	
    /**
     * 
     * @param entity
     */
    public <T extends BaseEntity> void remove(T entity) {
        this.em.remove(entity);    	
    }

    /**
     * Removes the number of entries from a table
     * @param <T>
     * @param items
     * @return 
     */
    public <T extends BaseEntity> boolean deleteItems(T[] items) {
        for (T item : items) {
            em.remove(em.merge(item));
        }
        return true;
    }
    
    /**
     * Check if the instance is a managed entity instance belonging to the current persistence context.
     * 
     * @param entity
     */
    public <T extends BaseEntity> boolean contains(T entity) {
        return this.em.contains(entity);    	
    }
    
    public void evictQueryRegion(String region) {
    	 Cache cache = getSession().getSessionFactory().getCache();
    	 cache.evictQueryRegion(region);
    }
    
    public <T extends BaseEntity> void evictEntityRegion(Class<T> type) {
   	 Cache cache = getSession().getSessionFactory().getCache();
   	 cache.evictEntityRegion(type);
   }
    
    /**
     * Remove this instance from the session cache.<br> 
     * Changes to the instance will not be synchronized with the database.<br> 
     * This operation cascades to associated instances 
     * if the association is mapped with cascade="evict".<br>
     * 
     * @param entity
     */
    public <T extends BaseEntity> void evict(T entity) {
    	this.getSession().evict(entity);
    }
    
    public boolean isDirty(){
    	return this.getSession().isDirty();
    }
    
    /**
     * 
     * @param criteria
     * @param firstResult
     * @param maxResults
     * @return
     */
    public Criteria createExecutableCriteriaPaginator(DetachedCriteria criteria, int firstResult, int maxResults) {
		Criteria executableCriteria = criteria.getExecutableCriteria(getSession());

		executableCriteria.setFirstResult(firstResult);
		executableCriteria.setMaxResults(maxResults);

		return executableCriteria;
	}
    
    public <T extends BaseEntity> void refresh(T entity) {
    	this.attach(entity);
    	this.em.refresh(entity);
    }
    
    public <T extends BaseEntity> void attach(T entity) {
        if(!getSession().contains(entity)){ 
        	getSession().buildLockRequest(LockOptions.NONE).lock(entity);
        }	
    }    
    
	public <T extends BaseEntity> void refreshAndLock(T entity) {
		this.getSession().refresh(entity, LockOptions.UPGRADE);
		// caso ocorra problema na forma acima feito, tentar com alinha abaixo.
		//this.em.refresh(entity, LockModeType.PESSIMISTIC_WRITE);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BaseEntity> T getAndLock(Class<T> clazz, Serializable id, LockOptions lockOptions) {
		return (T) this.getSession().get(clazz, id, lockOptions);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BaseEntity> T getAndLockForce(Class<T> clazz,
			Serializable id) {
		T entidade = (T) this.getSession().get(clazz, id);
		this.getSession().buildLockRequest(LockOptions.NONE)
				.setLockMode(LockMode.PESSIMISTIC_WRITE).setTimeOut(1000 * 60)
				.lock(entidade);
		return entidade;

	}


    /**
     * Updates the entity instance. update.
     * @param <T>
     * @param t
     * @return the object that is updated
     */
    public <T extends BaseEntity> void update(T entity) {
    	try {
    		//Utilizar update para garantir que não será persistido novo registro no banco
    		getSession().update(entity);
    	} catch (StaleObjectStateException ex) {
    		throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.STALE_OBJECT_STATE);
    	} catch (OptimisticLockException ex) {
    		throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);    		
    	}
    }
    
    public <T extends BaseEntity> T merge(T entity) {
    	T returnValue;
    	
    	try {
    		returnValue = (T) this.em.merge(entity);
    	} catch (StaleObjectStateException ex) {
    		throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.STALE_OBJECT_STATE);
    	} catch (OptimisticLockException ex) {
    		throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);    		
    	}
    	
        return returnValue; 
    }    

    /**
     * Returns the number of records that meet the criteria
     * @param namedQueryName
     * @return List
     */
    @SuppressWarnings("rawtypes")    
    public List findWithNamedQuery(String namedQueryName) {
        return this.em.createNamedQuery(namedQueryName).getResultList();
    }

    /**
     * Returns the number of records that meet the criteria
     * @param namedQueryName
     * @param parameters
     * @return List
     */
    @SuppressWarnings("rawtypes")    
    public List findWithNamedQuery(String namedQueryName, Map parameters) {
        return findWithNamedQuery(namedQueryName, parameters, 0);
    }

    /**
     * Returns the number of records with result limit
     * @param queryName
     * @param resultLimit
     * @return List
     */
    @SuppressWarnings("rawtypes")    
    public List findWithNamedQuery(String queryName, int resultLimit) {
        return this.em.createNamedQuery(queryName).
                setMaxResults(resultLimit).
                getResultList();
    }

    /**
     * Returns the number of records that meet the criteria
     * @param <T>
     * @param sql
     * @param type
     * @return List
     */
    
    @SuppressWarnings("unchecked")
	public <T extends BaseEntity> List<T> findByNativeQuery(String sql, Class<T> type) {
        return this.em.createNativeQuery(sql, type).getResultList();
    }

    /**
     * Returns the number of total records
     * @param namedQueryName
     * @return int
     */
    public int countTotalRecord(String namedQueryName) {
        Query query = em.createNamedQuery(namedQueryName);
        Number result = (Number) query.getSingleResult();
        return result.intValue();
    }

    /**
     * Returns the number of records that meet the criteria with parameter map and
     * result limit
     * @param namedQueryName
     * @param parameters
     * @param resultLimit
     * @return List
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })    
    public List findWithNamedQuery(String namedQueryName, Map parameters, int resultLimit) {
        Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = this.em.createNamedQuery(namedQueryName);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        for (Map.Entry<String, Object> entry : rawParameters) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }
    
    /**
     * Returns the number of records that will be used with lazy loading / pagination 
     * @param namedQueryName
     * @param start
     * @param end
     * @return List
     */
    @SuppressWarnings("rawtypes")    
    public List findWithNamedQuery(String namedQueryName, int start, int end) {
        Query query = this.em.createNamedQuery(namedQueryName);
        query.setMaxResults(end - start);
        query.setFirstResult(start);
        return query.getResultList();
    }
    
    @SuppressWarnings("rawtypes")    
    public List query(String query, int resultLimit) {
        return this.em.createQuery(query).
                setMaxResults(resultLimit).
                getResultList();
    }
    
    @SuppressWarnings("rawtypes")    
    public List query(String query) {
        return this.em.createQuery(query).
                getResultList();
    }
    
    public Query createQuery(String query, int resultLimit) {
        return this.em.createQuery(query).setMaxResults(resultLimit);
    }
    
    public Query createQuery(String query) {
        return this.em.createQuery(query);
    }
    
    public org.hibernate.Query createQueryStateless(String queryString) {
    	return this.getStatelessSession().createQuery(queryString);
    }
    
	public Query createNativeQuery(String sqlString) {
		return this.em.createNativeQuery(sqlString);
	}   

	
	public <T extends BaseEntity> Query createNativeQuery(String sqlString, Class<T> resultClass) {
		return this.em.createNativeQuery(sqlString, resultClass);
	}
    
    public org.hibernate.Query createHibernateQuery(String query) {
    	return this.getSession().createQuery(query);
    }

    public <T extends BaseEntity> Criteria createCriteria(Class<T> type) {
    	return (Criteria) getSession().createCriteria(type);
    }   

    public <T extends BaseEntity> Criteria createCriteria(Class<T> type, String alias) {
    	return (Criteria) getSession().createCriteria(type, alias);
    } 
    
    /**
	 * Metodo mais geral para criar criterias executaveis. Nao deve ser usado de
	 * forma geral, de preferencia ao metodo.
	 * 
	 * @see executeCriteria(DetachedCriteria criteria)
	 * @param criteria
	 * @return
	 */
    public Criteria createExecutableCriteria(DetachedCriteria criteria) {
		Criteria executableCriteria = criteria.getExecutableCriteria(getSession());
		return executableCriteria;
	}
    
    
    public void flush(){
    	try {
        	this.em.flush();
        } catch (StaleObjectStateException ex) {
    		throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.STALE_OBJECT_STATE);
    	} catch (OptimisticLockException ex) {
    		throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);    		
    	}
    }

    public Object getIdentifier(BaseEntity entity) {
    	return getSession().getIdentifier(entity);
    }
    
    /**
     * Executa o Hibernate.initialize.
     * Inicia o proxy, nao garante de itens de lista persistentes sejam inicializados.
     * 
     * @param proxy
     */
    public void initialize(Object proxy) {
    	Hibernate.initialize(proxy);
    }

	public SQLQuery createSQLQuery(String query) {
		return getSession().createSQLQuery(query);
	}

	public void doWork(Work aghuWork) {
		getSession().doWork(aghuWork);
	}
	
	public org.hibernate.Query createFilter(Object collection, String queryString) {
		return getSession().createFilter(collection, queryString);
	}

	public boolean entityManagerIsOpen() {
		return em.isOpen();
	}

	public void entityManagerClear() {
		this.em.clear();
	}

	public <T extends BaseEntity> void lockEntity(T entity, LockModeType lockMode) {
		this.em.lock(entity, lockMode);
	}

	public void joinTransaction() {
		this.em.joinTransaction();
	}

	public void setReadOnly(BaseEntity entity, boolean readOnly) {
		getSession().setReadOnly(entity, readOnly);
	}
	
	public ScrollableResults createScrollableResults(
			DetachedCriteria criteria, Integer fetchSize,
			ScrollMode scrollMode) {
		return criteria.getExecutableCriteria(getSession()).setFetchSize(fetchSize)
				.scroll(scrollMode);
	}

	public void indexar(Class clazz) throws InterruptedException {
//		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
//		fullTextEntityManager.createIndexer().startAndWait();
		
		FullTextSession fullTextSession = Search.getFullTextSession(this.getSession());		
		fullTextSession.createIndexer(clazz).threadsToLoadObjects(5).threadsForSubsequentFetching(20).startAndWait();
	}
	
	/**
	 * Se o dialeto é do Oracle
	 * 
	 * @return
	 */
	public Boolean isOracle() {
		return getDialectClassName().contains("Oracle");
	}
	
	/**
	 * Se o dialeto é do PostgreSQL
	 * 
	 * @return
	 */
	public Boolean isPostgreSQL() {
		return getDialectClassName().contains("PostgreSQL");
	}
	
	/**
	 * Se o dialeto é do HSQL
	 * 
	 * @return
	 */
	public Boolean isHSQL() {
		return getDialectClassName().contains("HSQL");
	}
	
	public Date obterDataHora() {
		String sqlString = null;
		Date retorno = null;		

		if (isOracle()) {	
			// ao usar o sysdate estava retornando com precisão apenas até os dias. 
			//usar systimestamp gerava um erro relacionado ao dialeto. verificar solução mais elegante posteriormente.
			sqlString = "select to_char(systimestamp,'DD/MM/RRRR HH24:MI:SS:FF3') from dual";
		} else if (isPostgreSQL()) {
			sqlString = "select now()";			
		} else {
			// implementar outras bases, caso necessario.
			sqlString = "select now()";			
		}
		
		Query queryTimestamp = createNativeQuery(sqlString);
		Object obj = queryTimestamp.getSingleResult();
		
		
		if (obj instanceof String){
			String stData = (String) obj;
			
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
			
			try {
				retorno = df.parse(stData);
			} catch (ParseException e) {
				LOG.error("Não foi possível fazer o parse para data da string " + stData, e);
			}			
		}else{
			retorno = (Date) obj;
		}
		return retorno;		
	}
		

}