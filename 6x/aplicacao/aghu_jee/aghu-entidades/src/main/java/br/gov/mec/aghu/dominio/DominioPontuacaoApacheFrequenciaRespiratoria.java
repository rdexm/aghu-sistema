package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApacheFrequenciaRespiratoria implements Dominio {

	/**
	 * valor >= 50
	 */
	POSITIVO_4(4),
	/**
	 * valor entre 35 e 49
	 */
	POSITIVO_3(3),
	/**
	 * valor entre 25 e 34
	 */
	POSITIVO_1(1),
	/**
	 * valor entre 12 e 24
	 */
	ZERO(0),
	/**
	 * valor entre 10 e 11
	 */
	NEGATIVO_1(-1),
	/**
	 * valor entre 6 e 9
	 */
	NEGATIVO_2(-2),
	/**
	 * valor <= 5
	 */
	NEGATIVO_4(-4);

	private int value;

	private DominioPontuacaoApacheFrequenciaRespiratoria(int value) {
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
			return ">= 50";
		case POSITIVO_3:
			return "35 - 49";
		case POSITIVO_1:
			return "25 - 34";
		case ZERO:
			return "12 - 24";
		case NEGATIVO_1:
			return "10 - 11";
		case NEGATIVO_2:
			return "6 - 9";
		case NEGATIVO_4:
			return "<= 5";
		default:
			return "";
		}
	}

}