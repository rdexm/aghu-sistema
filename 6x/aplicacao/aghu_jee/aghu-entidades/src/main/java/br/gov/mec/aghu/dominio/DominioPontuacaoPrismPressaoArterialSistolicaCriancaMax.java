package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismPressaoArterialSistolicaCriancaMax implements Dominio {
	//Pontuação Ficha PRISM para PA Sistólica Crianças Maiores
	/**
	 * < 50
	 */
	POSITIVO_7(7),
	
	/**
	 * > 200
	 */
	POSITIVO_6(6),
	
	/**
	 * 150 - 200
	 */
	POSITIVO_2(2),

	/**
	 * 76 - 149
	 */
	ZERO(0),
	
	/**
	 * 65 - 75
	 */
	NEGATIVO_2(-2),
	
	/**
	 * 50 - 64
	 */
	NEGATIVO_6(-6)
	
	;
	
	private int value;

	private DominioPontuacaoPrismPressaoArterialSistolicaCriancaMax(int value) {
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