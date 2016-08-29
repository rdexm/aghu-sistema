package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrigemPrescricao implements Dominio {
    
	/**
	 * Internação
	 */
	I,
	/**
	 * Quimioterapia
	 */
	Q,
	/**
	 * Diálise
	 */
	D;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Internação";
		case Q:
			return "Quimioterapia";
		case D:
			return "Diálise";
		default:
			return "";
		}
	}

}
