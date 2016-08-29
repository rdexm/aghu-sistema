package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author gandriotti
 * TODO
 */
public enum DominioClassifyXYZ implements Dominio {
	/**
	 */
	X,
	/**
	 */
	Y,
	/**
	 */
	Z;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case X:
			return "X";
		case Y:
			return "Y";
		case Z:
			return "Z";
		default:
			return "";
		}
	}

}
