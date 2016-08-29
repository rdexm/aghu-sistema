package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndProcessadoRecProcCid implements Dominio {
	/**
	 * Desprezado.
	 */
	D,
	/**
	 * Sim.
	 */
	S,
	
	/**
	 * Não.
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case D:
			return "Desprezado";
		case S:
			return "Sim";
		case N:
			return "Não";
		default:
			return "";
		}
	}
}