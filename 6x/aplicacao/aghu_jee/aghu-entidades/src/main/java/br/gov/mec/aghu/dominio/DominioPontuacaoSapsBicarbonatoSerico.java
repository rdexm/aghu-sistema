package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsBicarbonatoSerico implements Dominio {
	//Pontuação Ficha SAPS para sódio sérico


	/**
	 * >= 145
	 */
	POSITIVO_1(1),
	
	/**
	 * 125 - 144
	 */
	ZERO(0),

	
	/**
	 * < 125
	 */
	NEGATIVO_5(-5);
	
	
	private int value;

	private DominioPontuacaoSapsBicarbonatoSerico(int value) {
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
		case NEGATIVO_5:
			return "-5";
		default:
			return "";
		}
	}

}