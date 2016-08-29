package br.gov.mec.aghu.core.exception;

public class ObjetosOracleException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3615736726577561486L;

	public ObjetosOracleException(BaseException excecaoOriginal) {
		super(excecaoOriginal);
	}

	public ObjetosOracleException(BusinessExceptionCode code, Object... params) {
		super(code, params);
	}

	public ObjetosOracleException(BusinessExceptionCode code, Severity s,
			Object... params) {
		super(code, s, params);
	}

	public ObjetosOracleException(BusinessExceptionCode code, Throwable cause,
			Object... params) {
		super(code, cause, params);
	}

	public ObjetosOracleException(BusinessExceptionCode code, Throwable cause) {
		super(code, cause);
	}

}
