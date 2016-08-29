package br.gov.mec.aghu.casca.model;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum Protocolo implements Dominio {
	
	HTTP, HTTPS;
	
	@Override
	public int getCodigo() {
		return ordinal();
	}

	public String getDescricao() {
		return toString().toLowerCase();
	}
}
