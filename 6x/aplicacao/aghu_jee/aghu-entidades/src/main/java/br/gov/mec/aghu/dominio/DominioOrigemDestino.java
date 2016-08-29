package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrigemDestino implements Dominio{
	/**
	 * Origem
	 */
	O,
	/**
	 * Destino
	 */
	D;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case O:
			return "Origem";
		case D:
			return "Destino";
		default:
			return "";
		}
	}
}
