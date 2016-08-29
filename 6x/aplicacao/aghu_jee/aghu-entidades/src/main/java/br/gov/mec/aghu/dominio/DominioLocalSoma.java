package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLocalSoma implements Dominio {
	
	R, // Realizado
	D; // Demais

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case R:
			return "Realizado";
		case D:
			return "Demais";
		default:
			return "";
		}
	}
	
}
