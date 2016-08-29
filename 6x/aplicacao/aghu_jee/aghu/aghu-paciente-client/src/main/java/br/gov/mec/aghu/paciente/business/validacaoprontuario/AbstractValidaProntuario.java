package br.gov.mec.aghu.paciente.business.validacaoprontuario;


public abstract class AbstractValidaProntuario implements InterfaceValidaProntuario {

	public boolean validaProntuario(String prontuario) {
		return !(Integer.parseInt(String.valueOf(prontuario.charAt(prontuario
						.length() - 1))) != executaModulo(Integer.parseInt(prontuario
						.substring(0, prontuario.length() - 1))));
	}


}
