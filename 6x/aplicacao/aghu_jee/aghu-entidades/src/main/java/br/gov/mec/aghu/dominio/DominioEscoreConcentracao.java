package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreConcentracao implements Dominio {
	
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
		case E3:
			return "<= 7.25";
		case E0:
			return "> 7.25";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E3:
			return "3";
		case E0:
			return "0";
		default:
			return "";
		}
	}
	
	
}
