package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsSodioSerico implements Dominio {
	//Pontuação Ficha SAPS para nível de bicarbonato sérico


	/**
	 * >= 20
	 */
	ZERO(0),
	
	/**
	 * 15 - 19
	 */
	NEGATIVO_3(-3),
	
	/**
	 * < 15
	 */
	NEGATIVO_6(-6);
	
	
	private int value;

	private DominioPontuacaoSapsSodioSerico(int value) {
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
		case NEGATIVO_3:
			return "-3";
		case NEGATIVO_6:
			return "-6";
		default:
			return "";
		}
	}

}