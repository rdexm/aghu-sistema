package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoUsoSumario implements Dominio {
	/**
	 * Alta
	 */
	A,
	
	/**
	 * Óbito
	 */
	O;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this){
		case A:
			return "Alta";
		case O:
			return "Óbito";
		default:
			return "";
		}
	}
}
