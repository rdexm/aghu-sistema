package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioModoRelatorio implements Dominio {
	/**
	 * Caracter
	 */
	C,
	/**
	 * Default
	 */
	D,
	/**
	 * Bitmap
	 */
	B;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {	
		switch (this) {
		case C:
			return "Caracter";
		case D:
			return "Default";		
		case B:
			return "Bitmap";
		default:
			return "";
		}
	}

}