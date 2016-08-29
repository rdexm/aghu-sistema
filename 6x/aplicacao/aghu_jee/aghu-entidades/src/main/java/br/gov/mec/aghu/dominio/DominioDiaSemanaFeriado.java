package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioDiaSemanaFeriado implements Dominio {

	DOM,
	SEG,
	TER,
	QUA,
	QUI,
	SEX,
	SAB,
	VFE,
	FER,
	FERM,
	FERT;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {

		switch (this) {
		case DOM:
			return "Domingo";
		case SEG:
			return "Segunda";
		case TER:
			return "Terça";
		case QUA:
			return "Quarta";
		case QUI:
			return "Quinta";
		case SEX:
			return "Sexta";
		case SAB:
			return "Sábado";
		case VFE:
			return "Véspera feriado";
		case FER:
			return "Feriado";
		case FERM:
			return "Feriado manhã";
		case FERT:
			return "Feriado tarde";
		default:
			return "";
		}
	}
	
	
	public static DominioDiaSemanaFeriado getInstance(String valor) {
		if (valor.equalsIgnoreCase("DOM")) {
			return DominioDiaSemanaFeriado.DOM;
		} else if (valor.equalsIgnoreCase("SEG")) {
			return DominioDiaSemanaFeriado.SEG;
		} else if (valor.equalsIgnoreCase("TER")) {
			return DominioDiaSemanaFeriado.TER;
		} else if (valor.equalsIgnoreCase("QUA")) {
			return DominioDiaSemanaFeriado.QUA;
		} else if (valor.equalsIgnoreCase("QUI")) {
			return DominioDiaSemanaFeriado.QUI;
		} else if (valor.equalsIgnoreCase("SEX")) {
			return DominioDiaSemanaFeriado.SEX;
		} else if (valor.equalsIgnoreCase("SAB")) {
			return DominioDiaSemanaFeriado.SAB;
		} else if (valor.equalsIgnoreCase("FER")) {
			return DominioDiaSemanaFeriado.FER;
		} else if (valor.equalsIgnoreCase("VFE")) {
			return DominioDiaSemanaFeriado.VFE;
		} else if (valor.equalsIgnoreCase("FERM")) {
			return DominioDiaSemanaFeriado.FERM;
		} else if (valor.equalsIgnoreCase("FERT")) {
			return DominioDiaSemanaFeriado.FERT;
		}else{
			return null;
		}
	}
}