package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPosicaoAtendTrabParto implements Dominio {

	/**
	 * Ocipto Púbica.
	 */
    OP, 
	/**
	 * Ocipto Anterior Esquerda.
	 */
     OAE, 
	/**
	 * Ocipto Transversa Esquerda.
	 */
     OTE, 
	/**
	 * Ocipto Posterior Esquerda.
	 */
     OPE, 
	/**
	 * Ocipto Sacra.
	 */
     OS, 
	/**
	 * Ocipto Posterior Direita.
	 */
     OPD, 
	/**
	 * Ocipto Transversa Direita.
	 */
     OTD,
	/**
	 * Ocipto Anterior Direita.
	 */
     OAD;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case OP:
			return "Ocipto Púbica";
		case OAE:
			return "Ocipto Anterior Esquerda";
		case OTE:
			return "Ocipto Transversa Esquerda";
		case OPE:
			return "Ocipto Posterior Esquerda";
		case OS:
			return "Ocipto Sacra";
		case OPD:
			return "Ocipto Posterior Direita";	
		case OTD:
			return "Ocipto Transversa Direita";
		case OAD:
			return "Ocipto Anterior Direita";	
		default:
			return "";
		}
	}
	
	
}
