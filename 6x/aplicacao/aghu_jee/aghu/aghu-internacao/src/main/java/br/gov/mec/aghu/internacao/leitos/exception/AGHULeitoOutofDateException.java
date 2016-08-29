package br.gov.mec.aghu.internacao.leitos.exception;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("ucd")
public class AGHULeitoOutofDateException extends AGHULeitoConcurrencyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5195602614691168285L;

	public AGHULeitoOutofDateException(BusinessExceptionCode code,
			Object... params) {
		super(code, params);
	}

	public AGHULeitoOutofDateException(BusinessExceptionCode code,
			Severity severidade, Object... params) {
		super(code, severidade, params);
	}

	public AGHULeitoOutofDateException(BusinessExceptionCode code,
			Throwable cause, Object... params) {
		super(code, cause, params);
		
	}

	public AGHULeitoOutofDateException(BusinessExceptionCode code,
			Throwable cause) {
		super(code, cause);
	}

}
