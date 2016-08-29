package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoNascimento implements Dominio {
	P,
	C
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case P:
			return "Parto";
		case C:
			return "Cesariana";
		default:
			return "";
		}
	}

}
