package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o sexo de uma pessoa
 * 
 * @author ehgsilva
 * 
 */
public enum DominioSexoDeterminante implements Dominio {
	
	/**
	 * Qualquer
	 */
	Q,

	/**
	 * Masculino
	 */
	M,
	
	/**
	 * Feminino
	 */
	F;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case Q:
			return "Qualquer";
		case M:
			return "Masculino";
		case F:
			return "Feminino";
		default:
			return "";
		}
	}

}
