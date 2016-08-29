package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que indica os status do indPendente de uma interconsulta.
 * 
 */
public enum DominioOrigemMbcFichaAnestesia implements Dominio {
	/**
	 * 
	 */
	C,
	/**
	 * 
	 */
	G,
	/**
	 * 
	 */
	E,
	/**
	 * 
	 */
	I,
	/**
	 * 
	 */
	A,
	/**
	 * 
	 */
	M;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "";
		case G:
			return "";
		case E:
			return "";
		case I:
			return "";
		case A:
			return "";
		case M:
			return "";
		default:
			return "";
		}
	}

}
