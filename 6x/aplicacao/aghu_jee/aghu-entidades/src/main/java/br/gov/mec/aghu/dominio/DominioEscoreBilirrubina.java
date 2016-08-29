package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreBilirrubina implements Dominio {
	
	E0,
	E4,
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
			return "< 2mg/dL (< 34.2 µmol/L)";
		case E4:
			return ">=2 < 6mg/dL (>=34.2 < 102.6 µmol/L)";
		case E5:
			return ">=6mg/dL (>=102.6 µmol/L)";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E0:
			return "0";
		case E4:
			return "4";
		case E5:
			return "5";
		default:
			return "";
		}
	}
	
}
