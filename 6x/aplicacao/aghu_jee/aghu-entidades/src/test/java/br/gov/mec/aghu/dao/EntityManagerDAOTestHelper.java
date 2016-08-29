package br.gov.mec.aghu.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EntityManagerDAOTestHelper {
	
	private static final String MAGIC_STRING_SEM_ENTITY_MANAGER = "Sem EntityManager";

	private static final String PERSISTENCE_UNIT_NAME = "aghu-test";
	private static final Log LOG = LogFactory.getLog(EntityManagerDAOTestHelper.class);
	
	private EntityManager em = null;
	private boolean isEmOk = false;
	private long start = 0L;
	
	private final static EntityManagerDAOTestHelper instance = new EntityManagerDAOTestHelper();
	
	private void startEntityManager() {
		EntityManagerFactory emf = null;
		
		this.start = System.currentTimeMillis();
		LOG.info("Creating EntityManager: " + PERSISTENCE_UNIT_NAME);
		
		try {
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
			this.em = emf.createEntityManager();			
			LOG.info("EntityManager created successfully");
			this.isEmOk = true;
		} catch (Exception e) {
			LOG.error("Error creating EntityManager: ", e);
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

	private EntityManagerDAOTestHelper() {
		super();
		this.startEntityManager();
	}
	
	@Override
	protected void finalize()
			throws Throwable {
	
		this.stopEntityManager();
		super.finalize();
	}

	public boolean isEmOk() {
		
		if (!this.isEmOk) {			
			LOG.error(MAGIC_STRING_SEM_ENTITY_MANAGER);					
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
				LOG.error("Error creating Transaction: ", e);
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

	public static EntityManagerDAOTestHelper getInstance() {
		return instance;
	}	
}