package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndComparacao implements Dominio {

	/**
	 * Comparação entre procedimento realizado e item
	 */
	R,
	/**
	 * Comparação entre itens 
	 */
	I,
	;
	
	@Override
	public int getCodigo() {

		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case R:
			return "Comparação entre procedimento realizado e item";
		case I:
			return "Comparação entre itens ";
		default:
			return "";
	}
	}

}
