package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTmo implements Dominio {
	T,
	L,
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "Autólogo";
		case L:
			return "Alogênico Aparentado";
		case N:
			return "Alogênico não Aparentado";
		default:
			return "";
		}
	}

}
