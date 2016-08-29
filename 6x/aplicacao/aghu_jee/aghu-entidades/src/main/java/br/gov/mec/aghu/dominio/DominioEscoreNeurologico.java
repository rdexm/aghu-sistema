package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreNeurologico implements Dominio {
	
	E0,
	E11,
	E7,
	E_4,
	E4
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E0:
			return "Outras razões";
		case E11:
			return "Efeito de massa intracraniana";
		case E7:
			return "Déficit neurológico focal";
		case E_4:
			return "Convulsões";
		case E4:
			return "Coma, confusão, agitação";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E0:
			return "0";
		case E11:
			return "11";
		case E7:
			return "7";
		case E_4:
			return "-4";
		case E4:
			return "4";
		default:
			return "";
		}
	}
	
	public Short getShortValue() {
		switch (this) {
		case E0:
			return (short)0;
		case E11:
			return (short)11;
		case E7:
			return (short)7;
		case E_4:
			return (short)-4;
		case E4:
			return (short)4;
		default:
			return null;
		}
	}
	
}
