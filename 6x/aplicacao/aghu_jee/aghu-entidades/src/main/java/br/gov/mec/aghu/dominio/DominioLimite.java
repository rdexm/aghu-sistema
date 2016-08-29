package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLimite implements Dominio {
	
	/**
	 * Mês Inicial
	 */
	I, 
	/**
	 * Mês Anterior
	 */
	N,
	/**
	 * Mês Alta
	 */
	L;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {	
		switch (this) {
		case I:
			return "Mês Inicial";
		case N:
			return "Mês Anterior";
		case L:
			return "Mês Alta";
		default:
			return "";
		}
	}
	
}
