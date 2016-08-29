package br.gov.mec.aghu.internacao.leitos.exception;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("ucd")
public class AGHULetioOptimistLockException extends AGHULeitoConcurrencyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 296243053556251679L;

	public AGHULetioOptimistLockException(BusinessExceptionCode code,
			Object... params) {
		super(code, params);
	}

	public AGHULetioOptimistLockException(BusinessExceptionCode code,
			Severity severidade, Object... params) {
		super(code, severidade, params);
	}

	public AGHULetioOptimistLockException(BusinessExceptionCode code,
			Throwable cause, Object... params) {
		super(code, cause, params);
	}

	public AGHULetioOptimistLockException(BusinessExceptionCode code,
			Throwable cause) {
		super(code, cause);
	}

}
