package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsDiurese implements Dominio {
	//Pontuação Ficha SAPS para controle de diurese

	/**
	 * >= 1,000
	 */
	ZERO(0),

	/**
	 * 0,500 - 0,999
	 */
	NEGATIVO_4(-4),

	/**
	 * < 0,500
	 */
	NEGATIVO_11(-11);
	
	
	private int value;

	private DominioPontuacaoSapsDiurese(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ZERO:
			return "0";
		case NEGATIVO_4:
			return "-4";
		case NEGATIVO_11:
			return "-11";
		default:
			return "";
		}
	}

}