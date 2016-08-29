package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismPressaoArterialSistolicaLactente implements Dominio {
	//Pontuação Ficha PRISM para PA Sistólica Lactente
	/**
	 * < 40
	 */
	POSITIVO_7(7),
	
	/**
	 * > 160
	 */
	POSITIVO_6(6),
	
	/**
	 * 130 - 160
	 */
	POSITIVO_2(2),

	/**
	 * 55 - 65
	 */
	ZERO(0),
	
	/**
	 * 55 - 65
	 */
	NEGATIVO_2(-2),
	
	/**
	 * 40 - 54
	 */
	NEGATIVO_6(-6)
	
	;
	
	private int value;

	private DominioPontuacaoPrismPressaoArterialSistolicaLactente(int value) {
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