package br.gov.mec.aghu.service;

/**
 * Erros de negocio encontrados no servico.
 * Erros deste tipo devem ter uma mensagem negocial.
 *
 */
public class ServiceBusinessException extends ServiceException {
	
	private static final long serialVersionUID = 2128151337408869189L;
	

	public ServiceBusinessException() {
	}

	public ServiceBusinessException(String message) {
		super(message);
	}

}
