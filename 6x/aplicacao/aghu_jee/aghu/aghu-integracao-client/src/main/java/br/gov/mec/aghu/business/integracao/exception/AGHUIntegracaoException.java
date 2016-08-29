package br.gov.mec.aghu.business.integracao.exception;


/**
 * Excessao pai das excessoes de integração do AGHU.
 * @author aghu
 *
 */
@SuppressWarnings("ucd")
public class AGHUIntegracaoException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3111623346568432017L;
	
	public AGHUIntegracaoException() {
	}

	public AGHUIntegracaoException(Throwable e) {
		super(e);
	}

	public AGHUIntegracaoException(String chaveErro) {
		super(chaveErro);
	}

}
