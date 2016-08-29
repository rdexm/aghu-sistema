package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoTMO implements Dominio {
	ALOGENICO,
	AUTOLOGO;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ALOGENICO:
			return "Alogênico";
		case AUTOLOGO:
			return "Autólogo";
		default:
			return "";
		}
	}
}
