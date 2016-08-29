package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreFrequencia implements Dominio {
	
	E0,
	E5,
	E7
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E0:
			return "< 120";
		case E5:
			return ">= 120 < 160";
		case E7:
			return ">= 160";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E0:
			return "0";
		case E5:
			return "5";
		case E7:
			return "7";
		default:
			return "";
		}
	}
	

	
}
