package br.gov.mec.aghu.paciente.business.validacaoprontuario;

public class ValidaProntuarioException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5062337913958983545L;

	public ValidaProntuarioException(String mensagem) {
		super(mensagem);
	}

	public ValidaProntuarioException(String mensagem, Exception e) {
		super(mensagem, e);
	}

}
