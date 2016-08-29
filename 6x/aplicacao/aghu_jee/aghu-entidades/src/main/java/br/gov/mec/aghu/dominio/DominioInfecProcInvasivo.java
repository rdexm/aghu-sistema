package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioInfecProcInvasivo implements Dominio {
	/**
	 * Sim.
	 */
	S,
	/**
	 * Não.
	 */
	N,
	
	/**
	 * Indeterminado.
	 */
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Sim";
		case N:
			return "Não";
		case I:
			return "Indeterminado";
		default:
			return "";
		}
	}
}