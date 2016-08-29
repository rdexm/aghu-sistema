package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreOxigenacao implements Dominio {
	
	E11,
	E7,
	E5,
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
			return "PaO2/FiO2 < 100 e VM";
		case E7:
			return "PaO2/FiO2 >= 100 e VM";
		case E5:
			return "PaO2 < 60 e não VM";
		case E0:
			return "PaO2 >= 60 e não VM";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E11:
			return "11";
		case E7:
			return "7";
		case E5:
			return "5";
		case E0:
			return "0";
		default:
			return "";
		}
	}
	
	
}
