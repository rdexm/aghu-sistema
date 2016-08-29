package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsOxigenioMmhg implements Dominio {
	//Pontuação Ficha SAPS para oxigênio em PaO2 mm Hg/Fio2


	/**
	 * >= 200
	 */
	NEGATIVO_6(-6),

	/**
	 * 100 - 199
	 */
	NEGATIVO_9(-9),

	/**
	 * < 100
	 */
	NEGATIVO_11(-11);
	
	
	
	private int value;

	private DominioPontuacaoSapsOxigenioMmhg(int value) {
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