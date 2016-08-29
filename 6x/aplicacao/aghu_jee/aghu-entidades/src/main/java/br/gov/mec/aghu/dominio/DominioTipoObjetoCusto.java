package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoObjetoCusto implements Dominio {

	//Apoio
	AP,

	//Assistencial
	AS;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AP:
			return "Apoio";
		case AS:
			return "Assistencial";
		default:
			return "";
		}

	}

}
