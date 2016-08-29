package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioBaseAnaliseReposicao implements Dominio {
	
	CA,
	CO;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CA:
			return "Compra";
		case CO:
			return "Consumo";
		default:
			return "";
		}
	}

}
