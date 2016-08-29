package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreCreatinina implements Dominio {
	
	E0,
	E2,
	E7,
	E8
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E0:
			return "<  1.2mg/dL   (<106.1 µmol/L)";
		case E2:
			return ">= 1.2 < 2mg/dL   (>=106.1 <176.8 µmol/L)";
		case E7:
			return ">= 2  < 3.5mg/dL  (>=176.8 <309.4 µmol/L)";
		case E8:
			return ">= 3.5 mg/dL    (>=309.4 µmol/L)";
		default:
			return "";
		}
	}
	

	public String getNumero() {
		switch (this) {
		case E0:
			return "0";
		case E2:
			return "2";
		case E7:
			return "7";
		case E8:
			return "8";
		default:
			return "";
		}
	}
	
}
