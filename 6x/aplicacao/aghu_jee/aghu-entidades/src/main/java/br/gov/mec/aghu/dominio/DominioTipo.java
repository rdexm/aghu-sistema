package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioTipo implements Dominio {
	/**
	 * 
	 */
	V,
	
	/**
	 * 
	 */
	O,
	
	/**
	 * 
	 */
	D,
	
	/**
	 * 
	 */
	P;

	@Override
	public int getCodigo() {
		// TODO Auto-generated method stub
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case V:
			return "";
		case O:
			return "";
		case D:
			return "";
		case P:
			return "";

		default:
			return "";
		}
	}

}
