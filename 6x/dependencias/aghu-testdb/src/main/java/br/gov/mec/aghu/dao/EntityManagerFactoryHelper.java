package br.gov.mec.aghu.dao;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Responsabilidade desta classe apenas de criar o Entity Manager Factory.<br>
 * 
 * Singleton.<br>
 * 
 * Caso este teste esteja falhando favor olhar a documentacao:
 * <a href="https://apus.hcpa.ufrgs.br/projects/aghu/wiki/Teste_unitario_DAO">Teste_unitario_DAO</a>
 * 
 */
public class EntityManagerFactoryHelper {
	
	/**
	 * OBSERVAÇÃO IMPORTANTE: O valor desta variavel deve estar configurada no
	 * arquivo /jar/src/main/resources/META-INF/persistence.xml
	 */
	private static final String PERSISTENCE_UNIT_NAME = "aghu-test";
	private static final Logger log = Logger.getAnonymousLogger();
	
	private EntityManagerFactory entityManagerFactory = null;
	private boolean isEMFOk = false;
	
	private final static EntityManagerFactoryHelper instance = new EntityManagerFactoryHelper();
	
	public static EntityManagerFactoryHelper getInstance() {
		return instance;
	}
	
	private EntityManagerFactoryHelper() {
		super();
		this.startEntityManager();
	}
	
	
	private void startEntityManager() {
		log.info("Creating EntityManager: " + PERSISTENCE_UNIT_NAME);
		try {
			this.entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
			log.info("EntityManagerFactory created successfully");
			this.isEMFOk = true;
		} catch (Exception e) {
			log.severe("Error creating EntityManager: " + e + "\n" + Arrays.toString(e.getStackTrace()));
			this.isEMFOk = false;
			this.entityManagerFactory = null;
		}
	}
	
	public EntityManager criarEM() {	
		return this.entityManagerFactory.createEntityManager();
	}

	public boolean isEMFOk() {
		return this.isEMFOk;
	}
	
	/*
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
		
	public void logStats() {
		long duration = 0L;
		duration = System.currentTimeMillis() - this.start;
		log.info("Test duration: " + (duration / 1000) + " secs (" + duration + " milisecs)");
		this.isEmOk = false;
		this.em = null;
	}
	*/
	
}
