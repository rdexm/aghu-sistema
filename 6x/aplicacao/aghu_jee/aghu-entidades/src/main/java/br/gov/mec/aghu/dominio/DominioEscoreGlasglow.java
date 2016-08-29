package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreGlasglow implements Dominio {
	
	E15,
	E10,
	E7,
	E2,
	E0
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E15:
			return "3-4";
		case E10:
			return "5";
		case E7:
			return "6";
		case E2:
			return "7-12";
		case E0:
			return ">=13";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E15:
			return "15";
		case E10:
			return "10";
		case E7:
			return "7";
		case E2:
			return "2";
		case E0:
			return "0";
		default:
			return "";
		}
	}
	

}
