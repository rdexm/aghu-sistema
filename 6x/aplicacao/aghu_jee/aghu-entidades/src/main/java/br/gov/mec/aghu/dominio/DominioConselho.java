package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioConselho implements Dominio {
	CREMERS,
	COREN;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case CREMERS:
			return "CREMERS";
		case COREN:
			return "COREN";
		default:
			return "";
		}
	}

}
