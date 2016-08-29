package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoOrgao implements Dominio {
	O,
	H,
	I,
	R,
	P,
	C,
	U,
	X;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case O:
			return "Córnea";
		case H:
			return "Hepático Adulto";
		case I:
			return "Hepático Infantil";
		case R:
			return "Rim";
		case P:
			return "Pâncreas";
		case C:
			return "Coração";
		case U:
			return "Pulmão";
		case X:
			return "Pâncreas e Rim";
		default:
			return "";
		}
	}
}
