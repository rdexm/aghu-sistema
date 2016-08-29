package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioPeriodicidade implements Dominio {
	/**
	 * Mensal
	 */
	M, 
	/**
	 * Semanal
	 */
	S;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Mensal";
		case S:
			return "Semanal";		
		default:
			return "";
		}
	}

}
