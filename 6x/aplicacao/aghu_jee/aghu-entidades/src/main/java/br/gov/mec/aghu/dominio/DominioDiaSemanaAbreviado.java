package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioDiaSemanaAbreviado implements Dominio {

	SAB,
	DOM,
	SEG,
	TER,
	QUA,
	QUI,
	SEX;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
		case SAB:
			return "Sábado";
		case DOM:
			return "Domingo";
		case SEG:
			return "2ª feira";
		case TER:
			return "3ª feira";
		case QUA:
			return "4ª feira";
		case QUI:
			return "5ª feira";
		case SEX:
			return "6ª feira";
		default:
			return "";
		}
	}
	public String getDescricaoAbrev(Integer value) {
		
		switch (value) {
		case 0:
			return "DOM";
		case 1:
			return "SEG";
		case 2:
			return "TER";
		case 3:
			return "QUA";
		case 4:
			return "QUI";
		case 5:
			return "SEX";
		case 6:
			return "SAB";
		default:
			return "";
		}
	}

}
