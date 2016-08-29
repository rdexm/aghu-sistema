package br.gov.mec.aghu.services;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WebServiceTester {

	private static final String BASE_URL = "http://localhost:8080/aghu/rs/assinaturaDigital";
	
	private static final Log LOG = LogFactory
			.getLog(WebServiceTester.class);

	public static void main(String[] args) {

		
		chamarObterArquivoEDataAtual();
		chamarAtualizarEnvelope();
		chamarValidarUsuarioLogado();
		

	}



	private static void chamarObterArquivoEDataAtual() {

		Client client = ClientBuilder.newBuilder().register(SerializableBodyReader.class).build();

		WebTarget myResource = client.target(BASE_URL
				+ "/obterArquivoDataAtual/12205373");

		try {

			Serializable resposta = myResource.request(
					MediaType.APPLICATION_XML).get(Serializable.class);

			LOG.info(resposta);

		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
		} finally {
			client.close();
		}

	}

	private static void chamarAtualizarEnvelope() {

		Client client = ClientBuilder.newClient();

		WebTarget myResource = client
				.target(BASE_URL + "/atualizarEvelope/123");

		try {

			byte[] arquivo = new byte[100];
			arquivo[0] = 66;

			myResource.request(MediaType.APPLICATION_XML).post(
					Entity.entity(arquivo, MediaType.APPLICATION_XML));

		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
		} finally {
			client.close();
		}
	}
	
	
	private static void chamarValidarUsuarioLogado() {
		Client client = ClientBuilder.newBuilder().register(SerializableBodyWriter.class).build();

		WebTarget myResource = client
				.target(BASE_URL + "/validarUsuarioCertificado/76837440268");
		
		 InputStream inStream = null;

		try {
			inStream = new FileInputStream("/home/aghu/Downloads/resources/trust-anchor/ac_raiz_brasileira.cer");
		     CertificateFactory cf = CertificateFactory.getInstance("X.509");
		     X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);	
		     
		    Certificate[] certs = new Certificate[5];
		    certs[0] = cert;
			
			

			myResource.request(MediaType.APPLICATION_XML).post(
					Entity.entity(certs, MediaType.APPLICATION_XML));

		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
		} finally {
			client.close();
		}
		
	}
	
	

}
