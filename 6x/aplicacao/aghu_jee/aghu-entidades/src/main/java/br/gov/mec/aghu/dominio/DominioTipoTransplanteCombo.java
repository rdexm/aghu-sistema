package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoTransplanteCombo implements Dominio {
	T,
	O;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "TMO";
		case O:
			return "Órgãos";
		default:
			return "";
		}
	}
}
