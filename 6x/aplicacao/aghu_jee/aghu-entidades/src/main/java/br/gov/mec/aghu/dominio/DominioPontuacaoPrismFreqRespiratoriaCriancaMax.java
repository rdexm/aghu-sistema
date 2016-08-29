package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismFreqRespiratoriaCriancaMax implements Dominio {
	//Pontuação Ficha PRISM para Freqüência Respiratória de Crianças Maiores
	/**
	 * > 90
	 */
	POSITIVO_5(5),
	/**
	 * 51 - 90
	 */
	POSITIVO_1(1),
	/**
	 * >= 50
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoPrismFreqRespiratoriaCriancaMax(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_5:
			return "5";
		case POSITIVO_1:
			return "1";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}