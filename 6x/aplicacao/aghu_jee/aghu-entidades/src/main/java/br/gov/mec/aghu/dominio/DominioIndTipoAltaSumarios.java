package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioIndTipoAltaSumarios implements Dominio {
	/**
	 * Alta
	 */
	ALT, 
	/**
	 * Óbito
	 */
	OBT,
	/**
	 * 
	 */
	TRF,
	/**
	 * 
	 */
	SEM,
	/**
	 * 
	 */
	ANT;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ALT:
			return "Alta";
		case OBT:
			return "Óbito";
		case TRF:
			return "TRF";
		case ANT:
			return "ANT";
		case SEM:
			return "SEM";
		default:
			return "";
		}
	}

}