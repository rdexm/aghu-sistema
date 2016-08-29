package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscorePlaquetas implements Dominio {
	
	E13,
	E8,
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
		case E13:
			return "< 20";
		case E8:
			return ">= 20 < 50";
		case E5:
			return ">=50 < 100";
		case E0:
			return ">= 100";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E13:
			return "13";
		case E8:
			return "8";
		case E5:
			return "5";
		case E0:
			return "0";
		default:
			return "";
		}
	}
	

	
}
