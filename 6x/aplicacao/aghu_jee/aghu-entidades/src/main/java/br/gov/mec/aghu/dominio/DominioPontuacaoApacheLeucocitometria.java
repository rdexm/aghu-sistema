package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApacheLeucocitometria implements Dominio {

	/**
	 * valor >= 40
	 */
	POSITIVO_4(4),
	/**
	 * valor entre 20 e 39,9
	 */
	POSITIVO_2(2),
	/**
	 * valor entre 15 e 19,9
	 */
	POSITIVO_1(1),
	/**
	 * valor entre 3 e 14,9
	 */
	ZERO(0),
	/**
	 * valor entre 1 e 2,9
	 */
	NEGATIVO_2(-2),
	/**
	 * valor entre < 1
	 */
	NEGATIVO_4(-4);

	private int value;

	private DominioPontuacaoApacheLeucocitometria(int value) {
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
			return ">= 40";
		case POSITIVO_2:
			return "20 - 39,9";
		case POSITIVO_1:
			return "15 - 19,9";
		case ZERO:
			return "3 - 14,9";
		case NEGATIVO_2:
			return "1 - 2,9";
		case NEGATIVO_4:
			return "< 1";
		default:
			return "";
		}
	}

}