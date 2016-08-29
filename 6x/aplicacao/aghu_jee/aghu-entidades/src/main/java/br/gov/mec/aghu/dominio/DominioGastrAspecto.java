package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioGastrAspecto implements Dominio{
	C,
	M,
	S
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Claro";
		case M:
			return "Meconial";
		case S:
			return "Sanguinolento";
		default:
			return "";
		}
	}

}
