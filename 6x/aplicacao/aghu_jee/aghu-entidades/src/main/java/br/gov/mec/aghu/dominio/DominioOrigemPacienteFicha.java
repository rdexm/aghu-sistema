package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioOrigemPacienteFicha implements Dominio {
	INT,
	EME,
	CTI,
	AMB,
	CO;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case INT:
			return "";
		case EME:
			return "";
		case CTI:
			return "";
		case AMB:
			return "";
		case CO:
			return "";
		default:
			return "";
		}
	}

}
