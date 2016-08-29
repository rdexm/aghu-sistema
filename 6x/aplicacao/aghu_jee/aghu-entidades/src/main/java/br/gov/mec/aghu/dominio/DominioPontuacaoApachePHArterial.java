package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApachePHArterial implements Dominio {

	/**
	 * valor >= 7,7
	 */
	POSITIVO_4(4),
	/**
	 * valor entre 7,6 e 7,69
	 */
	POSITIVO_3(3),
	/**
	 * valor entre 7,5 e 7,59
	 */
	POSITIVO_1(1),
	/**
	 * valor entre 7,33 e 7,49
	 */
	ZERO(0),
	/**
	 * valor entre 7,25 e 7,32
	 */
	NEGATIVO_2(-2),
	/**
	 * valor entre 7,15 e 7,24
	 */
	NEGATIVO_3(-3),
	/**
	 * valor < 7,15
	 */
	NEGATIVO_4(-4);

	private int value;

	private DominioPontuacaoApachePHArterial(int value) {
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
			return ">= 7,7";
		case POSITIVO_3:
			return "7,6 - 7,69";
		case POSITIVO_1:
			return "7,5 - 7,59";
		case ZERO:
			return "7,33 - 7,49";
		case NEGATIVO_2:
			return "7,25 - 7,32";
		case NEGATIVO_3:
			return "7,15 - 7,24";
		case NEGATIVO_4:
			return "< 7,15";
		default:
			return "";
		}
	}

}