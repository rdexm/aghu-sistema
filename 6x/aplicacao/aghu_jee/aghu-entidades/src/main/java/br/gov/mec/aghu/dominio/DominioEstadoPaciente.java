package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o estado do paciente 
 */
public enum DominioEstadoPaciente implements Dominio {
	
	/**
	 * Curado
	 */
	C,

	/**
	 * Melhorado
	 */
	M,
	
	/**
	 * Inalterado
	 */
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Curado";
		case M:
			return "Melhorado";
		case I:
			return "Inalterado";
		default:
			return "";
		}
	}

}
