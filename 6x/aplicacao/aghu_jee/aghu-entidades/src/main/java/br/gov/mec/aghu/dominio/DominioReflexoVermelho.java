package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioReflexoVermelho implements Dominio {
	
	/**
	 * Anormal
	 */
	A,
	
	/**
	 * Normal
	 */
	N,
	
	/**
	 * Duvidoso
	 */
	D;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Anormal";
		case N:
			return "Normal";
		case D:
			return "Duvidoso";
		default:
			return "";
		}
	}
}
