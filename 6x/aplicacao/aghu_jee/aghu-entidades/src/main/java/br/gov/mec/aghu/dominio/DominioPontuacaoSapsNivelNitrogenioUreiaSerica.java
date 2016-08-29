package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsNivelNitrogenioUreiaSerica implements Dominio {
	//Pontuação Ficha SAPS para nível de nitrogênio na uréia sérica


	/**
	 * >= 84
	 */
	POSITIVO_10(10),
	/**
	 * 28 - 83
	 */
	POSITIVO_6(6),
	
	/**
	 * < 28
	 */
	ZERO(0);
	
	
	private int value;

	private DominioPontuacaoSapsNivelNitrogenioUreiaSerica(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_10:
			return "10";
		case POSITIVO_6:
			return "6";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}