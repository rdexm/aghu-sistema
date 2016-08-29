package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscorePressao implements Dominio {
	
	E11,
	E8,
	E3,
	E0
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E11:
			return "< 40";
		case E8:
			return ">= 40 < 70";
		case E3:
			return ">=70 < 120";
		case E0:
			return ">= 120";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E11:
			return "11";
		case E8:
			return "8";
		case E3:
			return "3";
		case E0:
			return "0";
		default:
			return "";
		}
	}
	
	
}
