package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioModuloMensagem implements Dominio {

	/**
	 * FATP_ATUALIZA_AIH
	 */
	FATP_ATUALIZA_AIH, 
	
	/**
	 * Ambulatório
	 */
	AMB, 
	
	/**
	 * Internação
	 */
	INT,
	/**
	 * APAC
	 */
	APAC,
	/**
	 * AIH
	 */
	AIH	
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case FATP_ATUALIZA_AIH: 
			return "FATP_ATUALIZA_AIH";
		case AMB: 
			return "Ambulatório";
		case INT:
			return "Internação";
		case APAC:
			return "APAC";
		case AIH:
			return "AIH";
		default:
			return "";
		}
	}
}
