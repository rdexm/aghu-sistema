package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApacheCreatinaSerica implements Dominio {

	/**
	 * valor >= 3,5
	 */
	POSITIVO_4(4),
	/**
	 * valor entre 2 e 3,4
	 */
	POSITIVO_3(3),
	/**
	 * valor entre 1,5 e 1,9
	 */
	POSITIVO_2(2),
	/**
	 * valor entre 0,6 e 1,4
	 */
	ZERO(0),
	/**
	 * valor entre < 0,6
	 */
	NEGATIVO_2(-2);

	private int value;

	private DominioPontuacaoApacheCreatinaSerica(int value) {
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
			return ">= 3,5";
		case POSITIVO_3:
			return "2 - 3,4";
		case POSITIVO_2:
			return "1,5 - 1,9";
		case ZERO:
			return "0,6 - 1,4";
		case NEGATIVO_2:
			return "< 0,6";
		default:
			return "";
		}
	}

}