package br.gov.mec.aghu.business.integracao.exception;


/**
 * Representa que o servidor ESB não está acessível.
 * @author aghu
 *
 */
public class ESBInacessivelException extends AGHUIntegracaoException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3219916901372073251L;
	
	
	
	public ESBInacessivelException(Throwable e) {
		super(e);
	}



	public ESBInacessivelException() {
		// TODO Auto-generated constructor stub
	}

}
