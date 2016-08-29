package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * 
 */
public enum DominioUnidTempoComMinuto implements Dominio {
	/**
	 * Hora.
	 */
	H,
	/**
	 * Dia.
	 */
	D,
	/**
	 * Minutos.
	 */
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case H:
			return "Horas";
		case D:
			return "Dia";
		case M:
			return "Minutos";
		default:
			return "";
		}
	}
	

	

}
