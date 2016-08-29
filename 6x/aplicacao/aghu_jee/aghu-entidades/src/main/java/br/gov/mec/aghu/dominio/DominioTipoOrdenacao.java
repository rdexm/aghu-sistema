package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoOrdenacao implements Dominio {
	
	ALEATORIA,
	ALFABETICA;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ALEATORIA:
			return "Aleatória";
		case ALFABETICA:
			return "Alfabética";
		default:
			return "";
		}
	}

}
