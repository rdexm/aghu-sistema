package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSitAntibiograma implements Dominio {
	/**
	 * Em execução.
	 */
	E,

	/**
	 * Realizado.
	 */
	R,

	/**
	 * Não realizado.
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E:
			return "Em execução";
		case R:
			return "Realizado";
		default:
			return "Não realizado";
		}
	}
}
