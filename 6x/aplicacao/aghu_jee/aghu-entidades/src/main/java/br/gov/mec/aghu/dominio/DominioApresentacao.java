package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioApresentacao implements Dominio {
	CF,
	PV,
	CM,
	IN
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case CF:
			return "Cefálica";
		case PV:
			return "Pélvica";
		case CM:
			return "Cósmica";
		case IN:
			return "Indefinida";
		default:
			return "";
		}
	}

}
