package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioEcog implements DominioString {
	/**
	 * Um.
	 */
	UM,
	/**
	 * Dois.
	 */
	DOIS,
	/**
	 * Três.
	 */
	TRES,
	/**
	 * Quatro.
	 */
	QUATRO,
	/**
	 * Não se aplica.
	 */
	NAO_SE_APLICA;
	
	
	
	
	@Override
	public String getCodigo() {
		switch (this) {
		case UM:
			return "1";
		case DOIS:
			return "2";
		case TRES:
			return "3";
		case QUATRO:
			return "4";
		case NAO_SE_APLICA:
			return "N";
		default:
			return "";
		}

	}

	@Override
	public String getDescricao() {

		switch (this) {
		case UM:
			return "1";
		case DOIS:
			return "2";
		case TRES:
			return "3";
		case QUATRO:
			return "4";
		case NAO_SE_APLICA:
			return "Não se aplica";
		default:
			return "";
		}
	}
}
