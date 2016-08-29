package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApachePressaoArterialMedia implements Dominio {

	/**
	 * valor >= 160
	 */
	POSITIVO_4(4),
	/**
	 * valor entre 130 e 159
	 */
	POSITIVO_3(3),
	/**
	 * valor entre 110 e 129
	 */
	POSITIVO_2(2),
	/**
	 * valor entre 70 e 109
	 */
	ZERO(0),
	/**
	 * valor entre 50 e 69
	 */
	NEGATIVO_2(-2),
	/**
	 * valor <= 49
	 */
	NEGATIVO_4(-4);

	private int value;

	private DominioPontuacaoApachePressaoArterialMedia(int value) {
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
			return ">= 160";
		case POSITIVO_3:
			return "130 - 159";
		case POSITIVO_2:
			return "110 - 129";
		case ZERO:
			return "70 - 109";
		case NEGATIVO_2:
			return "50 - 69";
		case NEGATIVO_4:
			return "<= 49";
		default:
			return "";
		}
	}

}