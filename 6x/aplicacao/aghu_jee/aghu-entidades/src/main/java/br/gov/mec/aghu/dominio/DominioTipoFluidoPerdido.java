package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoFluidoPerdido implements Dominio {

	/**
	 * 
	 */
	S, 
	/**
	 * 
	 */
	F;
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "";
		case F:
			return "";
		default:
			return "";
		}
	}
	
}
