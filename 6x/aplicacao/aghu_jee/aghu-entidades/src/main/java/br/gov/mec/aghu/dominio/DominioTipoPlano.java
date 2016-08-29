package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTipoPlano implements Dominio {
	/**
	 * Ambulatório
	 */
	A, 
	/**
	 * Internação
	 */
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Ambulatório";
		case I:
			return "Internação";		
		default:
			return "";
		}
	}

}
