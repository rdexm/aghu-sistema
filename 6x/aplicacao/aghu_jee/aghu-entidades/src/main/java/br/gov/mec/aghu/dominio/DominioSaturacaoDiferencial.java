package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSaturacaoDiferencial implements Dominio {
	A,
	N
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case N:
			return "Normal";
		case A:
			return "Alterado";
		default:
			return "";
		}
	}

}
