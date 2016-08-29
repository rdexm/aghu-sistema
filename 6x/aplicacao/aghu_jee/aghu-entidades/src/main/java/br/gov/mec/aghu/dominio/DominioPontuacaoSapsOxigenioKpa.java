package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsOxigenioKpa implements Dominio {
	//Pontuação Ficha SAPS para oxigênio PaO2 kPa/Fio2


	/**
	 * >= 26,6
	 */
	NEGATIVO_6(-6),

	/**
	 * 13,3 - 26,5
	 */
	NEGATIVO_9(-9),

	/**
	 * < 13,3
	 */
	NEGATIVO_11(-11);
	
	
	
	private int value;

	private DominioPontuacaoSapsOxigenioKpa(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NEGATIVO_6:
			return "-6";
		case NEGATIVO_9:
			return "-9";
		case NEGATIVO_11:
			return "-11";
		default:
			return "";
		}
	}

}