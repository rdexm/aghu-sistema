package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioTipoParto implements Dominio {
	/**
	 * 
	 */
	P,
	/**
	 * 
	 */
	C;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Parto Normal";
		case C:
			return "Parto Cesariana";
		default:
			return "";
		}
	}

}
