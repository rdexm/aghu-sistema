package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPosicaoCervice implements Dominio {
	P,
	C,
	A
	;

	@Override
	public int getCodigo() {
		
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case P:
			return "Posterior";
		case C:
			return "Centrado";
		case A:
			return "Anterior";
		default:
			return "";
		}
	}

}
