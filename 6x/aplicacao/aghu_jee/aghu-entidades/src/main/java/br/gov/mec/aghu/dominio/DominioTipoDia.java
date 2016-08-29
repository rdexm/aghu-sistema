package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


/**
 * Representa os dias da Semana. De Domingo a Sabado mais Feriado e Vespera de Feriado.<br>
 * Atributo tipoDia da entidade AghHorariosUnidFuncional.
 * 
 * @author rcorvalao
 *
 */
public enum DominioTipoDia implements Dominio {
	SEG // Segunda-feira
	, TER
	, QUA
	, QUI
	, SEX
	, SAB
	, DOM // Domingo
	, FER // Feriado
	, VFE // Vespera de Feriado
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	public Integer getCodigoNumerico() {

		switch (this) {
		case SEG:
			return 0;
		case TER:
			return 1;
		case QUA:
			return 2;
		case QUI:
			return 3;
		case SEX:
			return 4;
		case SAB:
			return 5;	
		case DOM:
			return 6;
		case FER:
			return 7;
		case VFE:
			return 8;	
		default:
			return 0;
		}
	}

	@Override
	public String getDescricao() {

		switch (this) {
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
		case DOM:
			return "Domingo";
		case FER:
			return "Feriado";
		case VFE:
			return "Véspera feriado";	
		default:
			return "";
		}
	}

	public static DominioTipoDia getInstance(String diaDaSemana) {
		DominioTipoDia returnValue = null;

		if (diaDaSemana != null) {
			String strTipoDia = diaDaSemana.toUpperCase();
			if ("SEG".equals(strTipoDia)) {
				returnValue = DominioTipoDia.SEG;
			} else if ("TER".equals(strTipoDia)) {
				returnValue = DominioTipoDia.TER;
			} else if ("QUA".equals(strTipoDia)) {
				returnValue = DominioTipoDia.QUA;
			} else if ("QUI".equals(strTipoDia)) {
				returnValue = DominioTipoDia.QUI;
			} else if ("SEX".equals(strTipoDia)) {
				returnValue = DominioTipoDia.SEX;
			} else if ("SAB".equals(strTipoDia)) {
				returnValue = DominioTipoDia.SAB;
			} else if ("DOM".equals(strTipoDia)) {
				returnValue = DominioTipoDia.DOM;
			} else if ("FER".equals(strTipoDia)) {
				returnValue = DominioTipoDia.FER;
			} else if ("VFE".equals(strTipoDia)) {
				returnValue = DominioTipoDia.VFE;
			}
		}

		return returnValue;
	}


}
