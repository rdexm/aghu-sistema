package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApacheFrequenciaCardiaca implements Dominio {

	/**
	 * valor >= 180
	 */
	POSITIVO_4(4),
	/**
	 * valor entre 140 e 179
	 */
	POSITIVO_3(3),
	/**
	 * valor entre 110 e 139
	 */
	POSITIVO_2(2),
	/**
	 * valor entre 70 e 109
	 */
	ZERO(0),
	/**
	 * valor entre 55 - 69
	 */
	NEGATIVO_2(-2),
	/**
	 * valor 40 e 54
	 */
	NEGATIVO_3(-3),
	/**
	 * valor <= 39
	 */
	NEGATIVO_4(-4);

	private int value;

	private DominioPontuacaoApacheFrequenciaCardiaca(int value) {
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
			return "valor >= 180";
		case POSITIVO_3:
			return "140 - 179";
		case POSITIVO_2:
			return "110 - 139";
		case ZERO:
			return "70 - 109";
		case NEGATIVO_2:
			return "55 - 69";
		case NEGATIVO_3:
			return "40 - 54";
		case NEGATIVO_4:
			return "<= 39";
		default:
			return "";
		}
	}

}