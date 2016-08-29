package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApacheSodioPlasmatico implements Dominio {

	/**
	 * valor >= 180
	 */
	POSITIVO_4(4),
	/**
	 * valor entre 160 a 179
	 */
	POSITIVO_3(3),
	/**
	 * valor entre 155 a 159
	 */
	POSITIVO_2(2),
	/**
	 * valor entre 150 a 154
	 */
	POSITIVO_1(1),
	/**
	 * valor entre 130 a 149
	 */
	ZERO(0),
	/**
	 * valor entre 120 a 129
	 */
	NEGATIVO_2(-2),
	/**
	 * valor entre 111 a 119
	 */
	NEGATIVO_3(-3),
	/**
	 * valor <= 110
	 */
	NEGATIVO_4(-4);

	private int value;

	private DominioPontuacaoApacheSodioPlasmatico(int value) {
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
			return ">= 180";
		case POSITIVO_3:
			return "160 - 179";
		case POSITIVO_2:
			return "155 - 159";
		case POSITIVO_1:
			return "150 - 154";
		case ZERO:
			return "130 - 149";
		case NEGATIVO_2:
			return "120 - 129";
		case NEGATIVO_3:
			return "111 - 119";
		case NEGATIVO_4:
			return "<= 110";
		default:
			return "";
		}
	}

}