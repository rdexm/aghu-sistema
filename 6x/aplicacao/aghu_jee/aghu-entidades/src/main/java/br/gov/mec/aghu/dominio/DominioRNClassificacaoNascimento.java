package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioRNClassificacaoNascimento implements Dominio{
	ABO,
	NAV,
	NAM
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch(this){
		case ABO:
			return "Aborto";
		case NAV:
			return "Nativivo";
		case NAM:
			return "Natimorto";		
		default:
			return "";
		}
	}

}
