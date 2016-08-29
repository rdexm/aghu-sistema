package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioDiaSemanaColetaExames  implements DominioString{

	TODOS("TODOS"),
	SEG("SEG"),
	TER("TER"),
	QUA("QUA"),
	QUI("QUI"),
	SEX("SEX"),
	SAB("SAB"),
	DOM("DOM"),
	FER("FER");
	
	
	private String value;
	
	private DominioDiaSemanaColetaExames(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {

		switch (this) {
		case DOM:
			return "DOMINGO";
		case SEG:
			return "SEGUNDA";
		case TER:
			return "TERÇA";
		case QUA:
			return "QUARTA";
		case QUI:
			return "QUINTA";
		case SEX:
			return "SEXTA";
		case SAB:
			return "SABADO";
		case FER:
			return "FERIADO";
		case TODOS:
			return "TODOS";
		default:
			return "";
		}
	}
	
	public Integer getOrder() {

		switch (this) {
		case DOM:
			return 1;
		case SEG:
			return 2;
		case TER:
			return 3;
		case QUA:
			return 4;
		case QUI:
			return 5;
		case SEX:
			return 6;
		case SAB:
			return 7;
		case FER:
			return 8;
		case TODOS:
			return 9;
		default:
			return 0;
		}
	}
	
}
