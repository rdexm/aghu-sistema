package br.gov.mec.casca.model;

import br.gov.mec.dominio.Dominio;

public enum Protocolo implements Dominio {
	
	HTTP, HTTPS;
	
	public int getCodigo() {
		return ordinal();
	}

	public String getDescricao() {
		return toString().toLowerCase();
	}
}
