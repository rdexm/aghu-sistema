package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;


public enum DominioDiaSemana  implements DominioString{

	DOM("DOM"),
	SEG("SEG"),
	TER("TER"),
	QUA("QUA"),
	QUI("QUI"),
	SEX("SEX"),
	SAB("SAB");
	
	private String value;
	
	private DominioDiaSemana(String value) {
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
			return "DOM";
		case SEG:
			return "SEG";
		case TER:
			return "TER";
		case QUA:
			return "QUA";
		case QUI:
			return "QUI";
		case SEX:
			return "SEX";
		case SAB:
			return "SAB";
		default:
			return "";
		}
	}
	
	public String getDescricaoCompleta(){
		switch (this) {
		case DOM:
			return "Domingo";
		case SEG:
			return "Segunda-feira";
		case TER:
			return "Terça-feira";
		case QUA:
			return "Quarta-feira";
		case QUI:
			return "Quinta-feira";
		case SEX:
			return "Sexta-feira";
		case SAB:
			return "Sábado";
		default:
			return "";
		}
	}
	
	public static DominioDiaSemana getDiaDaSemana(Integer diaSemana){
		switch (diaSemana) {
		case 1:
			return DominioDiaSemana.DOM;
		case 2:
			return DominioDiaSemana.SEG;
		case 3:
			return DominioDiaSemana.TER;
		case 4:
			return DominioDiaSemana.QUA;
		case 5:
			return DominioDiaSemana.QUI;
		case 6:
			return DominioDiaSemana.SEX;
		case 7:
			return DominioDiaSemana.SAB;
		default:
			return null;
		}
	}
	
	public Integer getNumeroDiaDaSemana(){
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
		default:
			return null;
		}
	}
	
	public static DominioDiaSemana getInstance(String valor) {
		if ("DOM".equals(valor)) {
			return DominioDiaSemana.DOM;
		} else if ("SEG".equals(valor)) {
			return DominioDiaSemana.SEG;
		} else if ("TER".equals(valor)) {
			return DominioDiaSemana.TER;
		} else if ("QUA".equals(valor)) {
			return DominioDiaSemana.QUA;
		} else if ("QUI".equals(valor)) {
			return DominioDiaSemana.QUI;
		} else if ("SEX".equals(valor)) {
			return DominioDiaSemana.SEX;
		} else if ("SAB".equals(valor)) {
			return DominioDiaSemana.SAB;
		}else {
			return null;
		}
	}
}
