package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoCentroProducaoCustos implements Dominio{
	
	F,
	A, 
	I;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "Finalistico";
		case A:
			return "Apoio";
		case I:
			return "Intermediario";
		default:
			return "";
		}
	}

}
