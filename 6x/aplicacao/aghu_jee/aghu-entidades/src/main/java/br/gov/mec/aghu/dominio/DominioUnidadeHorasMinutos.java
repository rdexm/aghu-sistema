package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioUnidadeHorasMinutos implements Dominio {
	/**
	 * Horas
	 */
	H,
	/**
	 * Minutos
	 */
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Minutos";
		case H:
			return "Horas";
		default:
			return "";
		}
	}
}
