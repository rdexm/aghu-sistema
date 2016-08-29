package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoTmo implements Dominio{

	G,
	U;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case G:
			return "Alogênico";
		case U:
			return "Autólogo";
		default:
			return "";
		}
	}

}
