package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioRNClassificacaoGestacao implements Dominio{
	NAV,
	NAM,
	NEM
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case NAV:
			return "Nativivo";
		case NAM:
			return "Natimorto";
		case NEM:
			return "Neomorto";
		default:
			return "";
		}
	}

}
