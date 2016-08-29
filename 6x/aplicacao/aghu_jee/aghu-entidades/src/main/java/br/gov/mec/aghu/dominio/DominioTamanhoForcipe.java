package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTamanhoForcipe implements Dominio {
	M,
	B
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case M:
			return "Médio";
		case B:
			return "Baixo";
		default:
			return "";
		}
	}

}
