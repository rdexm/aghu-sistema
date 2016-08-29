package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author tfelini
 *
 */

public enum DominioDestinoAltaMamAltaSumario implements Dominio {
	
	/**
	 * Rede
	 */
	R, 
	/**
	 * Ambulatório HCPA
	 */
	A,
	/**
	 * Domicílio
	 */
	D,;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case R:
			return "Rede";
		case A:
			return "Ambulatório HCPA";
		case D:
			return "Domicílio";
		default:
			return "";
		}
	}
	
}
