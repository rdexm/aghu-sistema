package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismPotassio implements Dominio {
	//Pontuação Ficha PRISM para Potássio
	/**
	 * > 7,5
	 */
	POSITIVO_5(5),
	
	/**
	 * 6,5 - 7,5
	 */
	POSITIVO_1(1),
	
	/**
	 * 3,6 - 6,4
	 */
	ZERO(0),

	/**
	 * 3,0 - 3,5
	 */
	NEGATIVO_1(-1),
	
	/**
	 * < 3,0
	 */
	NEGATIVO_5(-5);
	
	private int value;

	private DominioPontuacaoPrismPotassio(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_5:
			return "5";
		case POSITIVO_1:
			return "1";
		case ZERO:
			return "0";
		case NEGATIVO_1:
			return "-1";
		case NEGATIVO_5:
			return "-5";
		default:
			return "";
		}
	}

}