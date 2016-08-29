package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApacheHematocrito implements Dominio {

	/**
	 * valor >= 60
	 */
	POSITIVO_4(4),
	/**
	 * valor entre 50 e 59,9
	 */
	POSITIVO_2(2),
	/**
	 * valor entre 46 e 49,9
	 */
	POSITIVO_1(1),
	/**
	 * valor entre 30 e 45,9
	 */
	ZERO(0),
	/**
	 * valor entre 20 e 29,9
	 */
	NEGATIVO_2(-2),
	/**
	 * valor entre < 20
	 */
	NEGATIVO_4(-4);

	private int value;

	private DominioPontuacaoApacheHematocrito(int value) {
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
			return ">= 60";
		case POSITIVO_2:
			return "50 - 59,9";
		case POSITIVO_1:
			return "46 - 49,9";
		case ZERO:
			return "30 - 45,9";
		case NEGATIVO_2:
			return "20 - 29,9";
		case NEGATIVO_4:
			return "< 20";
		default:
			return "";
		}
	}

}