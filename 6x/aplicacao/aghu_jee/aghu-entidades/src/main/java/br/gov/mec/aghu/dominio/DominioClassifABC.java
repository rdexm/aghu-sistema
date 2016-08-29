package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioClassifABC implements Dominio {
	A,
	B,
	C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "A";
		case B:
			return "B";
		case C:
			return "C";
		default:
			return "";
		}
	}

}
