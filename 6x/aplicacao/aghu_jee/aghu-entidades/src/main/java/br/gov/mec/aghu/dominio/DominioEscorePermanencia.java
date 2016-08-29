package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscorePermanencia implements Dominio {
	
	E0,
	E6,
	E7;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E0:
			return "< 14";
		case E6:
			return ">= 14 < 28";
		case E7:
			return ">= 28";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E0:
			return "0";
		case E6:
			return "6";
		case E7:
			return "7";
		default:
			return "";
		}
	}
	
	public Short getShortValue() {
		switch (this) {
		case E0:
			return (short)0;
		case E6:
			return (short)6;
		case E7:
			return (short)7;
		default:
			return null;
		}
	}
	
}
