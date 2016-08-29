package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoAdministracao implements Dominio {
	
	/**
	 * ANA
	 */
	ANA, 
	/**
	 * AAC
	 */
	AAC, 
	/**
	 * AEM
	 */
	AEM, 
	/**
	 * ATO
	 */
	ATO, 
	/**
	 * APA
	 */
	APA, 
	/**
	 * ASU
	 */
	ASU;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {	
		switch (this) {
		case ANA:
			return "ANA";
		case AAC:
			return "AAC";
		case AEM:
			return "AEM";
		case ATO:
			return "ATO";
		case APA:
			return "APA";
		case ASU:
			return "ASU";
		default:
			return "";
		}
	}
}
