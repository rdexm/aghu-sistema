package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioTipoPrevAtende implements Dominio {
	/**
	 * 
	 */
	C,
	
	/**
	 * 
	 */
	N,
	
	/**
	 * 
	 */
	I;

	@Override
	public int getCodigo() {
		// TODO Auto-generated method stub
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "";
		case N:
			return "";
		case I:
			return "";		

		default:
			return "";
		}
	}

}
