package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreIdade implements Dominio {
	
	E0,
	E5,
	E9,
	E13,
	E15,
	E18;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E0:
			return "< 40";
		case E5:
			return ">=40 < 60";
		case E9:
			return ">=60 < 70";
		case E13:
			return ">=70 < 75";
		case E15:
			return ">=75 < 80";
		case E18:
			return ">=80";
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
		case E9:
			return "9";
		case E13:
			return "13";
		case E15:
			return "15";
		case E18:
			return "18";
		default:
			return "";
		}
	}
	
	public Short getShortValue() {
		switch (this) {
		case E0:
			return (short)0;
		case E5:
			return (short)5;
		case E9:
			return (short)9;
		case E13:
			return (short)13;
		case E15:
			return (short)15;
		case E18:
			return (short)18;
		default:
			return null;
		}
	}
	
}
