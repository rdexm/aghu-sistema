package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoForcipe implements Dominio {
	S,
	P,
	K,
	B,
	V,
	O
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case S:
			return "Simpson";
		case P:
			return "Piper";
		case K:
			return "Kieland";
		case B:
			return "Barton";
		case V:
			return "Vácuo-Extrator";
		case O:
			return "Outro";
		default:
			return "";
		}
	}

}
