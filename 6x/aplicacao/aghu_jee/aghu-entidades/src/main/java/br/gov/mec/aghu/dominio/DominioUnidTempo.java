package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * 
 */
public enum DominioUnidTempo implements Dominio {
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
	

	public static DominioUnidTempo getInstance(String valor) {
		if ("H".equalsIgnoreCase(valor)) {
			return DominioUnidTempo.H;
		} else if ("D".equalsIgnoreCase(valor)) {
			return DominioUnidTempo.D;
		} else {
			return null;
		}
	}

}
