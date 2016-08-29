package br.gov.mec.aghu.dao;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.gov.mec.aghu.core.persistence.dao.DataUnit;

public class DatabaseProducer {
	
    @PersistenceContext(name="aghu-pu")
    EntityManager entityManager;
	
    @Produces  
	@DataUnit
    public EntityManager createEntityManager() {  
        return entityManager;  
    }  
    
    //https://issues.jboss.org/browse/WFLY-3137
    /*public void dispose(  
            @Disposes @DataUnit EntityManager entityManager) {  
        entityManager.close();  
    } */ 	
}
