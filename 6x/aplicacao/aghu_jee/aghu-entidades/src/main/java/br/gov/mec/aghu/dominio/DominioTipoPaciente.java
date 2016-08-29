package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de um paciente.
 * 
 */
public enum DominioTipoPaciente implements Dominio {

	
	/**
	 * Adulto
	 */
	A,

	/**
	 * Ambos
	 */
	B,

	/**
	 * Pediátrico
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Adulto";
		case B:
			return "Ambos";
		case P:
			return "Pediátrico";
		default:
			return "";
		}
	}

}
