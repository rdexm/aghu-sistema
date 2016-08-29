package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioInclusaoLoteReposicao implements Dominio{
	AT,
	MN,
	OL;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AT:
			return "Automática";
		case MN:
			return "Manual";
		case OL:
			return "Outro Lote";	
		default:
			return "";
		}
	}

}
