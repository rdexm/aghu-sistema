package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreDigestivo implements Dominio {
	
	E0,
	E9,
	E3
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
		case E9:
			return "Pancreatite Grave";
		case E3:
			return "Abdômen Agudo - Outros";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E0:
			return "0";
		case E3:
			return "3";
		case E9:
			return "9";
		default:
			return "";
		}
	}
	
	public Short getShortValue() {
		switch (this) {
		case E0:
			return (short)0;
		case E3:
			return (short)3;
		case E9:
			return (short)9;
		default:
			return null;
		}
	}
	
	
}
