package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoMedulaOssea implements Dominio {
	
	T,
	A,
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
		case A:
			return "Alogênico Aparentado";
		case N:
			return "Alogênico Não Aparentado";
		default:
			return "";
		}
	}

}