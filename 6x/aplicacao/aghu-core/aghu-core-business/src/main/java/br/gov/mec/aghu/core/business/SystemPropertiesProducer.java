package br.gov.mec.aghu.core.business;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.engine.spi.SessionImplementor;

import br.gov.mec.aghu.core.persistence.dao.DataAccessService;

public class SystemPropertiesProducer {
	
	private static final Log LOG = LogFactory.getLog(SystemPropertiesProducer.class);
	
	@Inject
	private DataAccessService dataAcess;
	
	@Produces @Named("systemProperties") @SessionScoped
	@SuppressWarnings({ "unchecked", "PMD.AvoidThrowingRawExceptionTypes" })
	public Map getSystemProperties() {
		Connection connection = null;		
		try {
			Map systemProperties = new HashMap();
			systemProperties.putAll(System.getProperties());
		
			SessionImplementor sessionImplementor = (SessionImplementor) dataAcess.getSessionImplementor();
			
			connection = sessionImplementor.connection();
			DatabaseMetaData metaData = connection.getMetaData();			
			
			systemProperties.put("hibernateDialect", sessionImplementor.getFactory().getDialect().getClass().getName());
			systemProperties.put("databaseName", String.format("%s %s", metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion()));
									
			systemProperties.put("systemProperties", getSystemPropertiesAsString());
			
			systemProperties.put("driverName", metaData.getDriverName());			
			systemProperties.put("driverVersion", metaData.getDriverVersion());
			
			String url = metaData.getURL() != null ? metaData.getURL() : "";
			int ultimoIndice = url.lastIndexOf('/'); // detectaria na URL do JDBC para Postgres
			if (ultimoIndice < 0) { 
				ultimoIndice = url.lastIndexOf(':'); // detectaria na URL do JDBC para Oracle
			}
			String schema = ultimoIndice >= 0 ? url.substring(++ultimoIndice, url.length()) 
					: "Não foi possível identificar o banco.";
		    systemProperties.put("schema", schema);
		    		    
			return systemProperties;
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao recuperar propriedades sobre o sistema. ", e);
		} finally {
			try {
				connection.close();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	private String getSystemPropertiesAsString() {
		StringBuffer buffer = new StringBuffer();
		
		for (Map.Entry property : System.getProperties().entrySet()) {
			buffer.append(String.format("%s:%s; ", property.getKey(), property.getValue()));
		}
		
		return buffer.toString();
	}
}
