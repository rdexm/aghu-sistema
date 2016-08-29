package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioCecCanulAtrial implements Dominio {
	
	/**
	 * 
	 */
	U,
	/**
	 * 
	 */
	D;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case U:
			return "Única";
		case D:
			return "Dupla";
		default:
			return "";
		}
	}

}
