package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioDiaSemanaSigla implements Dominio {

	/**
	 * SEGUNDA
	 */
	SEG,

	/**
	 * TERÇA
	 */
	TER,

	/**
	 * QUARTA
	 */
	QUA,

	/**
	 * QUINTA
	 */
	QUI,

	/**
	 * SEXTA
	 */
	SEX,

	/**
	 * SABADO
	 */
	SAB,

	/**
	 * DOMINGO
	 */
	DOM;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
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
		case DOM:
			return "DOMINGO";
		default:
			return "";
		}
	}

	public String getDescricaoCompleta() {
		switch (this) {
		case SEG:
			return "Segunda Feira";
		case TER:
			return "Terça Feira";
		case QUA:
			return "Quarta Feira";
		case QUI:
			return "Quinta Feira";
		case SEX:
			return "Sexta Feira";
		case SAB:
			return "Sábado";
		case DOM:
			return "Domingo";
		default:
			return "";
		}
	}

	/**
	 * Obtém DominioDiaSemanaSigla através do número do dia da semana
	 * <p>
	 * 1 = DOM, 2 = SEG, 3 = TER, 4 = QUA, 5 = QUI, 6 = SEX, 7 = SAB
	 * <p>
	 * 
	 * @param numeroDiaSemana
	 * @return DominioDiaSemanaSigla
	 */
	public static DominioDiaSemanaSigla getDiaSemanaSigla(int numeroDiaSemana) {
		switch (numeroDiaSemana) {
		case 1:
			return DominioDiaSemanaSigla.DOM;
		case 2:
			return DominioDiaSemanaSigla.SEG;
		case 3:
			return DominioDiaSemanaSigla.TER;
		case 4:
			return DominioDiaSemanaSigla.QUA;
		case 5:
			return DominioDiaSemanaSigla.QUI;
		case 6:
			return DominioDiaSemanaSigla.SEX;
		case 7:
			return DominioDiaSemanaSigla.SAB;
		default:
			throw new IllegalArgumentException();
		}
	}
}
