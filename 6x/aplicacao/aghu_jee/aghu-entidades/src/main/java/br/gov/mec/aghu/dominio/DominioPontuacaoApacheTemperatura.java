package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApacheTemperatura implements Dominio {

	/**
	 * valor >= 41
	 */
	POSITIVO_4(4),
	/**
	 * valor entre 39 e 40,9
	 */
	POSITIVO_3(3),
	/**
	 * valor entre 38,5 e 38,9
	 */
	POSITIVO_1(1),
	/**
	 * valor entre 36 e 38,4
	 */
	ZERO(0),
	/**
	 * valor entre 34 e 35,9
	 */
	NEGATIVO_1(-1),
	/**
	 * valor entre 32 e 33,9
	 */
	NEGATIVO_2(-2),
	/**
	 * valor entre 30 a 31,9
	 */
	NEGATIVO_3(-3),
	/**
	 * valor <= 29,9
	 */
	NEGATIVO_4(-4);

	private int value;

	private DominioPontuacaoApacheTemperatura(int value) {
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
			return ">= 41";
		case POSITIVO_3:
			return "39 - 40,9";
		case POSITIVO_1:
			return "38,5 - 38,9";
		case ZERO:
			return "36 - 38,4";
		case NEGATIVO_1:
			return "34 - 35,9";
		case NEGATIVO_2:
			return "32 - 33,9";
		case NEGATIVO_3:
			return "30 - 31,9";
		case NEGATIVO_4:
			return "<= 29,9";
		default:
			return "";
		}
	}

}