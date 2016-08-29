package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismBicarbonato implements Dominio {
	//Pontuação Ficha PRISM para Bicarbonato
	/**
	 * > 32
	 */
	POSITIVO_3(3),

	/**
	 * 16 - 32
	 */
	ZERO(0),
	
	/**
	 * < 16
	 */
	NEGATIVO_3(-3);
	
	private int value;

	private DominioPontuacaoPrismBicarbonato(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_3:
			return "3";
		case NEGATIVO_3:
			return "-3";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}