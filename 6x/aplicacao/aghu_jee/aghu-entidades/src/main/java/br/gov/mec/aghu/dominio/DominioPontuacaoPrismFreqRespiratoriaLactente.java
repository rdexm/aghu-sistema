package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismFreqRespiratoriaLactente implements Dominio {
	//Pontuação Ficha PRISM para Freqüência Respiratória Lactente
	/**
	 * 
	 */
	POSITIVO_7(7),
	/**
	 * 
	 */
	POSITIVO_4(4),
	/**
	 * 
	 */
	POSITIVO_1(1),
	/**
	 * 
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoPrismFreqRespiratoriaLactente(int value) {
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
		case POSITIVO_4:
			return "4";
		case POSITIVO_1:
			return "1";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}