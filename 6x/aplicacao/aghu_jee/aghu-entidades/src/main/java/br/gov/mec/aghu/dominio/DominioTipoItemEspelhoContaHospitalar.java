package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoItemEspelhoContaHospitalar implements Dominio {
	
	O, // Obrigatório
	R, // Realizado
	D; // Demais

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case O:
			return "Obrigatório";
		case R:
			return "Realizado";
		case D:
			return "Demais";
		default:
			return "";
		}
	}
}
