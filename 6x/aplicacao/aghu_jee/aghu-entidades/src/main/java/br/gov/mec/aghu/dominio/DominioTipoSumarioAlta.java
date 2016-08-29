package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoSumarioAlta implements Dominio {
	/**
	 * Internação
	 */
	I, 
	/**
	 * Ambulatório
	 */
	A,
	/**
	 * Todos
	 */
	T,
	/**
	 * Emergência
	 */
	E;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Internação";
		case A:
			return "Ambulatório";
		case T:
			return "Todos";
		case E:
			return "Emergência";	
		default:
			return "";
		}
	}

}
