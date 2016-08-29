package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoEventoMbcPrincipal implements Dominio {
	/**
	 * 
	 */
	S,

	/**
	 * 
	 */
	C,
	/**
	 * 
	 */
	A,
	/**
	 * 
	 */
	O,
	;

	//complementar
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "";
		case C:
			return "";
		case A:
			return "";
		case O:
			return "";
		default:
			return "";
		}
	}
}
