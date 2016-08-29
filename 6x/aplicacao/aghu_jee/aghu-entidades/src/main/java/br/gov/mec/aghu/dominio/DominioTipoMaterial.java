package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoMaterial implements Dominio {
	
	E,
	D;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E:
			return "Estocável";
		case D:
			return "Direto";
		default:
			return "";
		}
	}

}
