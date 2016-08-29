package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoCribIdadeGestacional implements Dominio {
	
	/**
	 * <= 24 Semanas
	 */
	POSITIVO_1(1),
	/**
	 * > 24 Semanas
	 */
	ZERO(0);

	private int value;

	private DominioPontuacaoCribIdadeGestacional(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_1:
			return "1";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}