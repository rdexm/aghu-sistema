package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApacheIdade implements Dominio {

	/**
	 * valor <= 44
	 */
	VALOR_0(0),
	/**
	 * valor entre 45 e 54
	 */
	VALOR_2(2),
	/**
	 * valor entre 55 e 64
	 */
	VALOR_3(3),
	/**
	 * valor entre 65 e 74
	 */
	VALOR_5(5),
	/**
	 * valor >= 75
	 */
	VALOR_6(6), ;

	private int value;

	private DominioPontuacaoApacheIdade(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case VALOR_0:
			return "<= 44";
		case VALOR_2:
			return "45 - 54";
		case VALOR_3:
			return "55 - 64";
		case VALOR_5:
			return "65 - 74";
		case VALOR_6:
			return ">= 75";
		default:
			return "";
		}
	}

}