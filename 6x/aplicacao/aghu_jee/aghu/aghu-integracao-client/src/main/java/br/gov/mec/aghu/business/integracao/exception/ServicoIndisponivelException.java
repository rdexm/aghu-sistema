package br.gov.mec.aghu.business.integracao.exception;


/**
 * Representa que o servico requisitado não está disponível.
 * @author aghu
 *
 */
public class ServicoIndisponivelException extends AGHUIntegracaoException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6632030257223390586L;
	
	
	public ServicoIndisponivelException(Throwable e) {
		super(e);
	}

}
