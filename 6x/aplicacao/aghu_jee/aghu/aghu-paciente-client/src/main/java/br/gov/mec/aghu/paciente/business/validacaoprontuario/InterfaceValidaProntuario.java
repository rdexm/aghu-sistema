package br.gov.mec.aghu.paciente.business.validacaoprontuario;


/**
 * @author rafael
 *
 */
public interface InterfaceValidaProntuario {
	/**
	 * 
	 * Realiza a validação do código de prontuário informado pelo usuário.
	 * 
	 * @param prontuario
	 *            código do prontuário informado pelo usuário.
	 * @return retorna se o código é valido.
	 * 
	 */
	public boolean validaProntuario(String prontuario);
	public abstract int executaModulo(Integer numeroProntuario);
}
