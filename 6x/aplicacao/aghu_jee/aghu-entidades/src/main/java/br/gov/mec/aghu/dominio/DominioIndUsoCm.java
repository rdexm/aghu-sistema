package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndUsoCm implements Dominio {

	U,
	C,
	N;
	
	@Override
	public int getCodigo() {

		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case U:
			return "U";
		case C:
			return "C";
		case N:
			return "N";
		default:
			return "";
	}
	}

}
