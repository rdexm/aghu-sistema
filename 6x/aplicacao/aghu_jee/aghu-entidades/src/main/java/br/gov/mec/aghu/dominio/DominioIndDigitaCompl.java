package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author israel.haas
 * 
 */
public enum DominioIndDigitaCompl implements Dominio {
	/**
	 * O
	 */
	O,

	/**
	 * P
	 */
	P,
	
	/**
	 * N
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case O:
			return "O";
		case P:
			return "P";
		case N:
			return "N";
		default:
			return "";
		}
	}
	
}