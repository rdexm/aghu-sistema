package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioReacaoMantoux implements Dominio {
	/**
	 * Não reator.
	 */
	N,
	/**
	 * Reator fraco.
	 */
	C,
	
	/**
	 * Reator forte.
	 */
	T,
	
	/**
	 * Não realizado.
	 */
	F;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Não reator";
		case C:
			return "Reator Fraco";
		case T:
			return "Reator forte";
		case F:
			return "Não realizado";
		default:
			return "";
		}
	}
}