package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoFluidoAdministrado implements Dominio {

	/**
	 * 
	 */
	CR, 
	/**
	 * 
	 */
	CO,
	/**
	 * 
	 */
	HE;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CR:
			return "";
		case CO:
			return "";
		case HE:
			return "";
		default:
			return "";
		}
	}
	
}
