package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioUnidadeTempoLiberacao implements Dominio {

	/**
	 * Hora.
	 */
	H,
	/**
	 * Dia.
	 */
	D;
	
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
		default:
			return "";
		}
	}
}
