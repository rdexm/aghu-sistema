package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCoombs implements Dominio{
	P,
	N
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case P:
			return "Pos";
		case N:
			return "Neg";
		default:
			return "";
		}
	}
	
	public static DominioCoombs getInstance(String valor){
		if (valor.equalsIgnoreCase("P")) {
			return DominioCoombs.P;
		}
		else {
			return DominioCoombs.N;
		}
	}

}
