package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioCecOxigenador implements Dominio {
	
	/**
	 * 
	 */
	M,
	/**
	 * 
	 */
	B;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Membrana";
		case B:
			return "Borbulha";
		default:
			return "";
		}
	}

}
