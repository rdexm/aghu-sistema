package br.gov.mec.aghu.service;


/**
 * Erros que nao necessitem forcar verificacao.
 * Normalmente erros de verificacao contrato entre o Servico e Cliente.
 * 
 *
 */
public class ServiceRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 2015431081898439019L;

	public ServiceRuntimeException() {
	}

	public ServiceRuntimeException(String message) {
		super(message);
	}
}
