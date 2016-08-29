package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreEstadoCirurgico implements Dominio {
	
	E0,
	E6,
	E5
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E0:
			return "Efetiva";
		case E6:
			return "Cirurgia de emergência";
		case E5:
			return "Não cirúrgico";
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
		case E5:
			return "5";
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
		case E5:
			return (short)5;
		default:
			return null;
		}
	}
	
}
