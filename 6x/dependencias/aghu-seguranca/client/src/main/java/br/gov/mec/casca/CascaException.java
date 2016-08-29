package br.gov.mec.casca;

import org.jboss.seam.international.StatusMessage.Severity;

import br.gov.mec.seam.business.exception.MECBaseException;
import br.gov.mec.seam.business.exception.NegocioExceptionCode;

public class CascaException extends MECBaseException {

	public CascaException(NegocioExceptionCode code, Object... params) {
		super(code, params);
	}

	public CascaException(NegocioExceptionCode code, Severity severidade,
			Object... params) {
		super(code, severidade, params);
	}

	public CascaException(NegocioExceptionCode code, Throwable cause,
			Object... params) {
		super(code, cause, params);
	}

	public CascaException(NegocioExceptionCode code, Throwable cause) {
		super(code, cause);
	}

	
}
