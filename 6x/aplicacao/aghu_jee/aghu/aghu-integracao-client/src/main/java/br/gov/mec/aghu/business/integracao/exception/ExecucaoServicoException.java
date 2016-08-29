package br.gov.mec.aghu.business.integracao.exception;

/**
 * Representa um erro na execução do serviço requisitado.
 * 
 * @author aghu
 * 
 */
public class ExecucaoServicoException extends AGHUIntegracaoException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6632030257223390586L;

	

	public ExecucaoServicoException(Throwable e) {
		super(e);
	}

	

}
