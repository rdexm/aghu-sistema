package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApachePotassioPlasmatico implements Dominio {

	/**
	 * valor >= 7,0
	 */
	POSITIVO_4(4),
	/**
	 * valor entre 6 e 6,9
	 */
	POSITIVO_3(3),
	/**
	 * valor entre 5,5 e 5,9
	 */
	POSITIVO_1(1),
	/**
	 * valor entre 3,5 e 5,4
	 */
	ZERO(0),
	/**
	 * valor entre 3 e 3,4
	 */
	NEGATIVO_1(-1),
	/**
	 * valor entre 2,5 e 2,9
	 */
	NEGATIVO_2(-2),
	/**
	 * valor < 2,5
	 */
	NEGATIVO_4(-4);

	private int value;

	private DominioPontuacaoApachePotassioPlasmatico(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_4:
			return ">= 7,0";
		case POSITIVO_3:
			return "6 - 6,9";
		case POSITIVO_1:
			return "5,5 - 5,9";
		case ZERO:
			return "3,5 - 5,4";
		case NEGATIVO_1:
			return "3 - 3,4";
		case NEGATIVO_2:
			return "2,5 - 2,9";
		case NEGATIVO_4:
			return "< 2,5";
		default:
			return "";
		}
	}

}