package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCardiotocografiaPartos implements Dominio {
	DIPI, DIPII, DIPIII, BRAD, REATIVO;

	@Override
	public int getCodigo() {
		return ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case DIPI:
			return "CAT1";
		case DIPII:
			return "CAT2";
		case DIPIII:
			return "CAT3";
		case REATIVO:
			return "Reativo";
		case BRAD:
			return "Brad";
		default:
			return null;
		}
	}

}
