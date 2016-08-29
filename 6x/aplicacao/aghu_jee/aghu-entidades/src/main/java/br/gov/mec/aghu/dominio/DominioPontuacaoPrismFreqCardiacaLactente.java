package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismFreqCardiacaLactente implements Dominio {
	//Pontuação Ficha PRISM para Freqüência Cardiaca Lactente
	/**
	 * > 160
	 */
	POSITIVO_4(4),
	/**
	 * 90 - 160
	 */
	ZERO(0),
	/**
	 * < 90
	 */
	NEGATIVO_4(-4);
	
	private int value;

	private DominioPontuacaoPrismFreqCardiacaLactente(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_4:
			return "4";
		case NEGATIVO_4:
			return "-4";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}