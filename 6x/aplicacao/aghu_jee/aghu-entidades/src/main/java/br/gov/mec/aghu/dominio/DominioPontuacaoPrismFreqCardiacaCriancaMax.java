package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismFreqCardiacaCriancaMax implements Dominio {
	//Pontuação Ficha PRISM para Freqüência Cardiaca Crianças Maiores
	/**
	 * > 150
	 */
	POSITIVO_4(4),
	/**
	 * 80 - 150
	 */
	ZERO(0),
	/**
	 * < 80
	 */
	NEGATIVO_4(-4);
	
	private int value;

	private DominioPontuacaoPrismFreqCardiacaCriancaMax(int value) {
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