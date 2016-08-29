package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIntencaoTratamento implements Dominio {
	/**
	 * Curativa
	 */
	C,
	/**
	 * Paliativa
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		switch (this) {
		case C:
			return "Curativa";
		case P:
			return "Paliativa";
		default:
			return "";
		}
	}
}
