package br.gov.mec.aghu.core.business.moduleintegration;

import javax.ejb.ApplicationException;

/**
 * Exceção levantada pelo ModuleIntegrationInterceptor caso o módulo invocado
 * esteja inativo.
 * 
 */
@ApplicationException(rollback = false)
public class InactiveModuleException extends RuntimeException {

	private static final long serialVersionUID = -3135586397201065491L;

	public InactiveModuleException(String mensagem) {
		super(mensagem);
	}

	public InactiveModuleException() {
		super();
	}

}
