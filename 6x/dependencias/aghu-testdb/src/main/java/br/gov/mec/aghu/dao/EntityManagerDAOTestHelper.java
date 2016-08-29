package br.gov.mec.aghu.dao;

import java.util.Arrays;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classe temporaria para que os DAOTest fora do padrao funcionem.
 *
 */
@SuppressWarnings("PMD")
public class EntityManagerDAOTestHelper {
	
	private static final String MAGIC_STRING_SEM_ENTITY_MANAGER = "Sem EntityManager";
	//private final static Logger log = Logger.getAnonymousLogger();
	private static final Log LOG = LogFactory.getLog(EntityManagerDAOTestHelper.class);
	
	private EntityManager em = null;
	private boolean isEmOk = false;
	private long start = 0L;
	private EntityManagerFactoryHelper emfHelper = null;
	
	
	private final static EntityManagerDAOTestHelper instance = new EntityManagerDAOTestHelper();
	
	public static EntityManagerDAOTestHelper getInstance() {	
		return instance;
	}
	

	private EntityManagerDAOTestHelper() {
		super();	
		this.startEntityManager();
	}

	private void startEntityManager() {
		emfHelper = EntityManagerFactoryHelper.getInstance();
		
		this.start = System.currentTimeMillis();
		//log.info("Creating EntityManager: " + PERSISTENCE_UNIT_NAME);
		
		try {
			//emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
			this.em = emfHelper.criarEM();			
			LOG.info("EntityManager created successfully");
			this.isEmOk = true;
		} catch (Exception e) {
			LOG.warn("Error creating EntityManager: " + e + "\n" + Arrays.toString(e.getStackTrace()));
			this.isEmOk = false;
			this.em = null;
		}
	}

	private void stopEntityManager() {
		if (this.isEmOk) {
			this.em.close();
		}
		this.logStats();
	}

	@Override
	protected void finalize() throws Throwable {
		this.stopEntityManager();
		super.finalize();
	}

	public boolean isEmOk() {
		if (!this.isEmOk) {			
			LOG.warn(MAGIC_STRING_SEM_ENTITY_MANAGER);					
		}
		return this.isEmOk;
	}
	
	public EntityManager getEM() {
		return this.em;
	}
	
	public void startTransaction() {
		if (isEmOk()) {			
			LOG.info("EntityManager Transation begin");
			try {
				this.em.getTransaction().begin();
			} catch (Exception e) {
				LOG.warn("Error creating Transaction: " + e + "\n" + Arrays.toString(e.getStackTrace()));
				this.isEmOk = false;
			}
		}
	}
	
	public void stopTransaction() {
		if (isEmOk()) {
			this.em.getTransaction().rollback();
			LOG.info("EntityManager Transation rollback");
		}		
	}
		
	public void logStats() {
		long duration = 0L;
		
		duration = System.currentTimeMillis() - this.start;
		LOG.info("Test duration: " + (duration / 1000) + " secs (" + duration + " milisecs)");
		this.isEmOk = false;
		this.em = null;
	}

}
