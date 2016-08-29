package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioModoNascimento implements Dominio {
	E,
	F,
	V,
	P,
	D,
	N
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case E:
			return "Eutócico";
		case F:
			return "Instrumentado";
		case V:
			return "Vácuo";
		case P:
			return "Pélvico";
		case D:
			return "Distócico";
		case N:
			return "Não se aplica";
		default:
			return "";
		}
	}

}
