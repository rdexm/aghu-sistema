package br.gov.mec.aghu.service;

/**
 * Erros genericos de Servico.
 * Erros que necessitam checagem e não são negociais.
 * 
 *
 */
public class ServiceException extends Exception {

	private static final long serialVersionUID = 2015431081898439019L;

	public ServiceException() {
	}

	public ServiceException(String message) {
		super(message);
	}
}
