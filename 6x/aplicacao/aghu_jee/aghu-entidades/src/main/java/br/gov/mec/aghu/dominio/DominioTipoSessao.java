package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioTipoSessao implements Dominio {
	/**
	 * 
	 */
	A,
	
	/**
	 * 
	 */
	I,
	
	/**
	 * 
	 */
	C,
	
	/**
	 * 
	 */
	D;

	@Override
	public int getCodigo() {
		// TODO Auto-generated method stub
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "";
		case I:
			return "";
		case C:
			return "";
		case D:
			return "";

		default:
			return "";
		}
	}

}
