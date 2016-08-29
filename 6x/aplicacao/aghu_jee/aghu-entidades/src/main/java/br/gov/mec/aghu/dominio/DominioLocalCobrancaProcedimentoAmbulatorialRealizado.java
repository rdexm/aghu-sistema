package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioLocalCobrancaProcedimentoAmbulatorialRealizado implements DominioString {
	
	/**
	 * APAC
	 */
	A("A"),

	/**
	 * Internação
	 */
	I("I"),
	
	/**
	 * Ambulatório
	 */
	B("B"),

	/**
	 * Apac Exame
	 */
	E("E"),

	/**
	 * Apac Fotocoagulação
	 */
	F("F"),

	/**
	 * Apac Acompanhamento
	 */
	POSITIVO_5("5"),

	/**
	 * Apac Radioterapia
	 */
	R("R"),

	/**
	 * Siscolo
	 */
	S("S"),

	/**
	 * Apac Otorrino
	 */
	O("O"),

	/**
	 * Apac Nefro
	 */
	N("N"),

	/**
	 * Apac Nefro
	 */
	M("M");
	
	private String value;
	
	private DominioLocalCobrancaProcedimentoAmbulatorialRealizado(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "A";
		case I:
			return "I";
		case B:
			return "B";
		case E:
			return "E";
		case F:
			return "F";
		case POSITIVO_5:
			return "5";
		case R:
			return "R";
		case S:
			return "S";
		case O:
			return "O";
		case N:
			return "N";
		case M:
			return "M";
		default:
			return "";
		}
	}

}
