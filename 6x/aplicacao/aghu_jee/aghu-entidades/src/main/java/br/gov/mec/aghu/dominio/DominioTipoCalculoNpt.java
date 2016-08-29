package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoCalculoNpt implements Dominio{

	K,

	M,
	
	A,
	
	V,
	
	T;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case K:
			return "kg";
		case M:
			return "m²";
		case A:
			return "kg ou m²";	
		case V:
			return "ml";
		case T:
			return "kg/min";
		default:
			return "";
		}
	}

}
