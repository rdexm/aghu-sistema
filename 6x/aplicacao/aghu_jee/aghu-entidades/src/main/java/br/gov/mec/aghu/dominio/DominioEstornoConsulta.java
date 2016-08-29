package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioEstornoConsulta implements Dominio {

	/**
	 * S
	 */
	S,

	/**
	 * E
	 */
	E,

	/**
	 * A
	 */
	A,

	/**
	 * T
	 */
	T,

	/**
	 * C
	 */
	C,

	/**
	 * I
	 */
	I,

	/**
	 * N
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "S";
		case E:
			return "E";
		case A:
			return "A";
		case T:
			return "T";
		case C:
			return "C";
		case I:
			return "I";
		case N:
			return "N";
		default:
			return "";
		}
	}

}