package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoLaminaLaringo implements Dominio {
	
	/**
	 * Curva
	 */
	C,
	/**
	 * Reta
	 */
	R;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Curva";
		case R:
			return "Reta";
		default:
			return "";
		}

	}

}
