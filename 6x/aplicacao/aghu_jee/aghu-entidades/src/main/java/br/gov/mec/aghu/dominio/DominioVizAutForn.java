package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioVizAutForn implements Dominio {
	
	/**
	 * Não Programadas
	 */
	N,
	/**
	 * Programadas
	 */
	P,
	/**
	 * Todas
	 */
	T,
	/**
	 * Previsão Entrega até
	 */
	E;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Não Programadas";
		case P:
			return "Programadas";
		case T:
			return "Todas";
		case E:
			return "Previsão Entrega até";
		default:
			return "";
		}
	}
}
