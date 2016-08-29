package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioEspessuraCervice implements Dominio {
	G,
	M,
	F
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case G:
			return "Grosso";
		case M:
			return "Médio";
		case F:
			return "Fino";
		default:
			return "";
		}
	}

}
