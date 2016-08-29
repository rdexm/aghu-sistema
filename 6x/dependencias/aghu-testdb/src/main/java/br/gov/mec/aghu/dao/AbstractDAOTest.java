package br.gov.mec.aghu.dao;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.sql.JoinType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import br.gov.mec.aghu.core.persistence.dao.BaseDao;


/**
 * Classe a ser extendida pelos testes de DAO.
 * 
 * </br><b>ATENÇAO: A criteria (ou hql) deve ser executada no DAO, o teste
 * apenas chama o método do mesmo.</b>
 * 
 * @author lcmoura
 * 
 * @param Dao
 *            a ser testado
 */
@SuppressWarnings("PMD")
public abstract class AbstractDAOTest<T extends BaseDao<?>> {

	private static EntityManagerFactoryHelper emfHelper = EntityManagerFactoryHelper.getInstance();
	
	protected final Log logger = LogFactory.getLog(AbstractDAOTest.class);
	
	protected T daoUnderTests;
	protected EntityManager entityManager;
	private boolean isEmOk = false;
	
	@BeforeClass
	public final static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public final static void tearDownAfterClass() throws Exception {
	}

	@Before
	public final void setUp() throws Exception {
		try {
			this.initMocks();
			this.entityManager = emfHelper.criarEM();
			this.isEmOk = true;
			this.daoUnderTests = this.doDaoUnderTests();
			this.daoUnderTests.doSetDataAccessService(this.entityManager);
			this.startTransaction();
		} catch (Throwable t) {
			logger.error("Erro no metodo de Before do Test.", t);
			this.isEmOk = true;			
		}
	}

	@After
	public final void tearDown() throws Exception {
		this.stopTransaction();
		this.daoUnderTests = null;
		this.entityManager = null;
		this.finalizeMocks();
	}
	
	
	private void startTransaction() {
		if (isEmOk) {			
			logger.info("EntityManager Transaction begin");
			try {
				this.entityManager.getTransaction().begin();
			} catch (Exception e) {
				logger.error("Error creating Transaction: " + e + "\n" + Arrays.toString(e.getStackTrace()));
				this.isEmOk = false;
			}
		}
	}
	
	private void stopTransaction() {
		if (isEmOk) {
			this.entityManager.getTransaction().rollback();
			logger.info("EntityManager Transaction rollback");
		}		
	}

	/**
	 * Condição a ser chamada no inínio de cada teste, para evitar de rodar o
	 * mesmo no caso de não ter conseguido instanciar o Entity Manager
	 * 
	 * @return
	 */
	protected final boolean isEntityManagerOk() {
		return isEmOk;
	}
	
	protected <T> List<T> runCriteria(DetachedCriteria criteria, int firstResult, int maxResults, String orderProperty, boolean asc) {
		return runCriteria(criteria, firstResult, maxResults, orderProperty, asc, null);
	}

	protected <T> List<T> runCriteria(DetachedCriteria criteria, boolean cacheble) {
		return runCriteria(criteria);
	}
	
	protected <T> List<T> runCriteria(DetachedCriteria criteria, int firstResult, int maxResults, String orderProperty, boolean asc, CacheMode cacheMode) {
		if (StringUtils.isNotEmpty(orderProperty)) {
			addOrder(criteria, orderProperty, asc);
		}
		
		Criteria executableCriteria = createExecutableCriteriaPaginator(criteria, firstResult, maxResults);

		if (cacheMode != null) {
			executableCriteria.setCacheMode(cacheMode);
		}
		
		return executableCriteria.list();
	}

	protected <T> List<T> runCriteria(DetachedCriteria criteria) {
		return executeCriteria(criteria, false, null);
	}
	
	protected Object runCriteriaUniqueResult(DetachedCriteria criteria, boolean cacheable) {
		Criteria executableCriteria = createExecutableCriteria(criteria);
		executableCriteria.setCacheable(cacheable);
		Object object = executableCriteria.uniqueResult();

		return object;
	}
	
	protected <T> List<T> executeCriteria(DetachedCriteria criteria, boolean cacheble, CacheMode cacheMode) {
		Criteria executableCriteria = createExecutableCriteria(criteria);

		if (cacheble) {
			executableCriteria.setCacheable(cacheble);
		}
		
		if (cacheMode != null) {
			executableCriteria.setCacheMode(cacheMode);
		}

		return executableCriteria.list();
	}
	
	protected boolean isOracle() {
		return getDialectClassName().contains("Oracle");
	}
	
	protected boolean isPostgreSQL() {
		return getDialectClassName().contains("PostgreSQL");
	}
	
	protected Query createQuery(String query){
		return this.entityManager.createQuery(query); 
	}
		 
	protected ScrollableResults createScrollableResults(
			DetachedCriteria criteria, Integer fetchSize, ScrollMode scrollMode) {
		return criteria.getExecutableCriteria(getSession()).setFetchSize(fetchSize)
				.scroll(scrollMode);
	}
	
	private String getDialectClassName() {
		SessionImplementor sessionImplementor = (SessionImplementor) this.entityManager.getDelegate();
		String dialeto = sessionImplementor.getFactory().getDialect().getClass().getName();
		
		if (dialeto == null || !(dialeto.contains("Oracle") || dialeto.contains("PostgreSQL") || dialeto.contains("HSQL"))) {
			throw new IllegalStateException("Dialeto fora do padrão: " + dialeto + ". Verifique o arquivo hibernate.properties na conf do JBoss");
		}
		
		return dialeto;
	}
	
	private void addOrder(final DetachedCriteria criteria, String orderProperty, boolean asc) {
		if (orderProperty != null && StringUtils.isNotBlank(orderProperty)) {
			StringTokenizer tokenizer = new StringTokenizer(orderProperty, ".");
			
			String property = tokenizer.nextToken();
			while (tokenizer.hasMoreTokens()) {
				// Left join para nao excluir registros com relacionamentos nulos/nao existentes.
				criteria.createAlias(property, property, JoinType.LEFT_OUTER_JOIN);
				property = tokenizer.nextToken();
			}
			
			criteria.addOrder(asc ? Order.asc(orderProperty) : Order.desc(orderProperty));
		}
	}
	protected Long runCriteriaCount(DetachedCriteria criteria) {
		criteria.setProjection(Projections.rowCount());

		return (Long) runCriteriaUniqueResult(criteria);
	}
	
	protected Object runCriteriaUniqueResult(DetachedCriteria criteria) {
		Criteria executableCriteria = createExecutableCriteria(criteria);
		Object object = executableCriteria.uniqueResult();

		return object;
	}
	
	protected boolean runCriteriaExists(DetachedCriteria criteria) {
		Criteria executableCriteria = createExecutableCriteria(criteria);
		executableCriteria.setMaxResults(1);
		Object object = executableCriteria.uniqueResult();		
		return object != null;		
	}
	
	 protected org.hibernate.SQLQuery createSQLQuery(String query) {
		 return getSession().createSQLQuery(query);
	 }
	 
	 protected Query createNativeQuery(String query){
		 return this.entityManager.createNativeQuery(query);
	 }

	 protected org.hibernate.Query createHibernateQuery(String query) {
		 return this.getSession().createQuery(query);
	 }
	 
    private Session getSession() {
    	return (Session) this.entityManager.unwrap(Session.class);
    }

    private Criteria createExecutableCriteriaPaginator(DetachedCriteria criteria, int firstResult, int maxResults) {
		Criteria executableCriteria = criteria.getExecutableCriteria(getSession());

		executableCriteria.setFirstResult(firstResult);
		executableCriteria.setMaxResults(maxResults);

		return executableCriteria;
	}

    private Criteria createExecutableCriteria(DetachedCriteria criteria) {
		Criteria executableCriteria = criteria.getExecutableCriteria(getSession());
		return executableCriteria;
	}
    
	protected T getDaoUnderTests() {
		return this.daoUnderTests;
	}
 
	protected abstract T doDaoUnderTests();

	/**
	 * Aqui deve ser iniciado qualquer mock criado no teste. Exemplo: </br>
	 */
	protected abstract void initMocks();
	
	/**
	 * Aqui deve ser iniciado qualquer mock criado no teste. Exemplo: </br>
	 */
	protected abstract void finalizeMocks();
}