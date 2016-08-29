package br.gov.mec.aghu.core.seguranca;

public class TokenIdentityException extends Exception {

	private static final long serialVersionUID = 4661701623433265122L;

	public TokenIdentityException() {
		super();
	}

	public TokenIdentityException(String message, Throwable cause) {
		super(message, cause);
	}

	public TokenIdentityException(String message) {
		super(message);
	}

	public TokenIdentityException(Throwable cause) {
		super(cause);
	}

}
