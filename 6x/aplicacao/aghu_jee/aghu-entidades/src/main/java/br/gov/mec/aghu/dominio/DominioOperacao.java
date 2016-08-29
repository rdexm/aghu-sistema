package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOperacao implements Dominio {
	D,
	M;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case D:
			return "Divide";
		case M:
			return "Multiplica";
		default:
			return "";
		}
	}
	
}
