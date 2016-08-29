package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoMovimento implements Dominio {

	E, S, C;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case E :
				return "";
			case S :
				return "";
			case C :
				return "";
			default :
				return "";
		}
	}

}
