package br.gov.mec.aghu.internacao.leitos.exception;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;



public abstract class AGHULeitoConcurrencyException extends ApplicationBusinessException {
	//talvez deveria herdar de AGHUNegocioExceptionSemRollback {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6403572743535507659L;

	public AGHULeitoConcurrencyException(BusinessExceptionCode code,
			Object... params) {
		super(code, params);
	}

	public AGHULeitoConcurrencyException(BusinessExceptionCode code,
			Severity severidade, Object... params) {
		super(code, severidade, params);
	}

	public AGHULeitoConcurrencyException(BusinessExceptionCode code,
			Throwable cause, Object... params) {
		super(code, cause, params);
	}

	public AGHULeitoConcurrencyException(BusinessExceptionCode code,
			Throwable cause) {
		super(code, cause);
	}

	

}
