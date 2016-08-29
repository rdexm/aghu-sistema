package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoApacheFio2Menor5 implements Dominio {

	/**
	 * valor PaO2 > 70
	 */
	ZERO(0),
	/**
	 * valor PaO2 61 - 70
	 */
	NEGATIVO_1(-1),
	/**
	 * valor PaO2 55 - 60
	 */
	NEGATIVO_3(-3),
	/**
	 * valor PaO2 < 55
	 */
	NEGATIVO_4(-4);

	private int value;

	private DominioPontuacaoApacheFio2Menor5(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ZERO:
			return "PaO2 > 70";
		case NEGATIVO_1:
			return "PaO2 61 - 70";
		case NEGATIVO_3:
			return "PaO2 55 - 60";
		case NEGATIVO_4:
			return "PaO2 < 55";
		default:
			return "";
		}
	}

}