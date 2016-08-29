package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsFreqCardiaca implements Dominio {
	//Pontuação da Ficha SAPS para freqüência cardíaca
	

	/**
	 * >= 160
	 */
	POSITIVO_7(7),

	/**
	 * 120 - 159
	 */
	POSITIVO_4(4),
	
	/**
	 * 70 - 119
	 */
	ZERO(0),

	/**
	 * 40 - 69
	 */
	NEGATIVO_2(-2),

	/**
	 * < 40
	 */
	NEGATIVO_11(-11);
	
	
	private int value;

	private DominioPontuacaoSapsFreqCardiaca(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_7:
			return "7";
		case POSITIVO_4:
			return "4";
		case ZERO:
			return "0";
		case NEGATIVO_2:
			return "-2";
		case NEGATIVO_11:
			return "-11";
		default:
			return "";
		}
	}

}