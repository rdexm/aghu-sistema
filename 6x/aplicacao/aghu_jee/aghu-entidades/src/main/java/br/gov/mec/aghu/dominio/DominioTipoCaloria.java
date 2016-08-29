package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTipoCaloria implements Dominio {

	P,


	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Protéicas";
		case N:
			return "Não Protéicas";
		default:
			return "";
		}
	}


}
