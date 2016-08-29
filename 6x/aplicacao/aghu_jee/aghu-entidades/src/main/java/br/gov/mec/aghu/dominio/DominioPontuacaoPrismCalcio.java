package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismCalcio implements Dominio {
	//Pontuação Ficha PRISM para Cálcio
	/**
	 * > 15,0
	 */
	POSITIVO_6(6),
	
	/**
	 * 12,0 - 15,0
	 */
	POSITIVO_2(2),
	
	/**
	 * 8,1 - 11,9
	 */
	ZERO(0),

	/**
	 * 7,0 - 8,0
	 */
	NEGATIVO_2(-2),
	
	/**
	 * < 7,0
	 */
	NEGATIVO_6(-6);
	
	private int value;

	private DominioPontuacaoPrismCalcio(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_6:
			return "6";
		case POSITIVO_2:
			return "2";
		case ZERO:
			return "0";
		case NEGATIVO_2:
			return "-2";
		case NEGATIVO_6:
			return "-6";
		default:
			return "";
		}
	}

}