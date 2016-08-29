package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApacheFio2Maior5 implements Dominio {

	/**
	 * valor >= 500
	 */
	VALOR_4(4),
	/**
	 * valor entre 350 e 499
	 */
	VALOR_3(3),
	/**
	 * valor entre 200 e 349
	 */
	VALOR_2(2),
	/**
	 * valor entre < 200
	 */
	VALOR_0(0);

	private int value;

	private DominioPontuacaoApacheFio2Maior5(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case VALOR_4:
			return ">= 500";
		case VALOR_3:
			return "350 - 499";
		case VALOR_2:
			return "200 - 349";
		case VALOR_0:
			return "< 200";
		default:
			return "";
		}
	}

}