package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoRateio implements Dominio {
	
	/**
	 * Peso (Objetos de Custo)
	 */
	P,
	
	/**
	 * Linear (Produção)
	 */
	L;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Peso (Objetos de custos)";
		case L:
			return "Linear (Produção)";
		default:
			return "";
		}
	}
}
