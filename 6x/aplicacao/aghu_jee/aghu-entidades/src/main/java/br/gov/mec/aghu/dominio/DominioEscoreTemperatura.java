package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreTemperatura implements Dominio {
	
	E7,
	E0
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E7:
			return "< 35";
		case E0:
			return ">=35";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E7:
			return "7";
		case E0:
			return "0";
		default:
			return "";
		}
	}
	
}
