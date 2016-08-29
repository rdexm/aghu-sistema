package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsIdade implements Dominio {
	//Pontuação  Ficha SAPS para idade
	
	/**
	 * >= 80
	 */
	POSITIVO_19(19),
	
	/**
	 * 75 - 79
	 */
	POSITIVO_16(16),

	/**
	 * 70 - 74
	 */
	POSITIVO_15(15),
	
	/**
	 * 60 - 69
	 */
	POSITIVO_12(12),
	
	/**
	 * 40 - 59
	 */
	POSITIVO_7(7),
	
	/**
	 * < 40
	 */
	ZERO(0);

	private int value;

	private DominioPontuacaoSapsIdade(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_19:
			return "19";
		case POSITIVO_16:
			return "16";
		case POSITIVO_15:
			return "15";
		case POSITIVO_12:
			return "12";
		case POSITIVO_7:
			return "7";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}