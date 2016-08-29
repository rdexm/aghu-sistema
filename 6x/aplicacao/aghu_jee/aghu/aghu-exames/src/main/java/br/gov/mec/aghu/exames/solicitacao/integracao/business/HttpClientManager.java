package br.gov.mec.aghu.exames.solicitacao.integracao.business;

import java.io.IOException;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

@ApplicationScoped
public class HttpClientManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2457943471874868051L;
	private PoolingHttpClientConnectionManager connectionManager;
	private CloseableHttpClient httpClient;
	
	private static final Log LOG = LogFactory.getLog(HttpClientManager.class);
	
	public HttpClientManager() {
		LOG.info("Iniciando HttpClientManager");
		connectionManager = new PoolingHttpClientConnectionManager();

		connectionManager.setMaxTotal(20);
		connectionManager.closeIdleConnections(5, TimeUnit.SECONDS);
		connectionManager.setValidateAfterInactivity(4000);
		
		SocketConfig defaultSocketConfig = SocketConfig.custom()
				.setSoTimeout(4000)
				.build();
		connectionManager.setDefaultSocketConfig(defaultSocketConfig);

		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(4000)
				.setConnectTimeout(4000)
				.setSocketTimeout(4000)
				.build();
		httpClient = HttpClients.custom()
				.setConnectionManager(connectionManager)
				.disableAutomaticRetries()
				.setDefaultRequestConfig(requestConfig)
				.build();
		
		int timeout = 30; // seconds
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				LOG.info("Limpando conexoes do PoolingHttpClientConnectionManager");
				connectionManager.closeExpiredConnections();
				connectionManager.closeIdleConnections(15, TimeUnit.SECONDS);
			}
		};

		new Timer(true).schedule(task, timeout * 1000, timeout * 1000);
		LOG.info("HttpClientManager iniciado");
	}
	
	@Produces
	public CloseableHttpClient getHttpClient(){
		connectionManager.closeExpiredConnections();
		connectionManager.closeIdleConnections(15, TimeUnit.SECONDS);
		return httpClient;
	}
	@PreDestroy
	protected void destroys(){
		LOG.info("Finalizando HttpClientManager");
		try {
			httpClient.close();
		} catch (IOException e) {
			LOG.warn("Erro ao finalizar HttpClient",e);
		}
		connectionManager.shutdown();
	}
	
}
