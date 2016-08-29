package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLwsTipoLiberacao implements Dominio{
	
	A,
	M;
	
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Automática";		
		case M:	
			return "Manual";	
		default:
			return "";
		}
	}
}
