package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioChild implements Dominio {
	
	/**
	 * 
	 */
	B,
	/**
	 * 
	 */
	C,
	/**
	 * 
	 */
	A;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case B:
			return "";
		case C:
			return "";
		case A:
			return "";
		default:
			return "";
		}
	}

}
