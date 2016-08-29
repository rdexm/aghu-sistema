package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoFetal implements Dominio{
	L,
	T,
	O
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch(this){
		case L:
			return "Longitudinal";
		case T:
			return "Transversa";
		case O:
			return "Oblíqua";
		default:
			return "";
		}
	}

}
