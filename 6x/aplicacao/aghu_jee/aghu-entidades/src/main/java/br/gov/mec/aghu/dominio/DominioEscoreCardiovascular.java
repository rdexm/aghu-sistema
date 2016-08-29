package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioEscoreCardiovascular implements Dominio {
	
	E0,
	E_5,
	E3,
	E5,
	E5_;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case E0:
			return "Outras razões";
		case E_5:
			return "Arritmias";
		case E3:
			return "Choque Hipovolêmico Hemorrágico, Choque Hipovolêmico Não hemorrágico";
		case E5:
			return "Choque Séptico";
		case E5_:
			return "Choque Anafilático, Misto e Não definido";
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
		case E3:
			return "3";
		case E_5:
			return "-5";
		case E5_:
			return "5";
		default:
			return "";
		}
	}
	
	
}
