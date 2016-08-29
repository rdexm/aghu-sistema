package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoEvento implements Dominio {
	/**
	 * 
	 */
	A,

	/**
	 * 
	 */
	G,
	;

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "";
		case G:
			return "";
		default:
			return "";
		}
	}


	//complementar
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

}
