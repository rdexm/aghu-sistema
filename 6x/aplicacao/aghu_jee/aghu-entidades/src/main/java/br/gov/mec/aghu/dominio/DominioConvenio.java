package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioConvenio implements Dominio {
	S,
	N;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this) {
		case S:
			return "SUS";
		case N:
			return "Não SUS";
		default:
			return "";
		}
	}
	
}
