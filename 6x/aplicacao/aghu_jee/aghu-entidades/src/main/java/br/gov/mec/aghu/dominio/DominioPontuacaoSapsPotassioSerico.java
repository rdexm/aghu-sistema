package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsPotassioSerico implements Dominio {
	//Pontuação Ficha SAPS para potássio sérico(mmol/d)


	/**
	 * >= 5,0
	 */
	POSITIVO_3(3),
	
	/**
	 * 3,0 - 4,9
	 */
	ZERO(0),

	
	/**
	 * < 3,0
	 */
	NEGATIVO_3(-3);
	
	
	private int value;

	private DominioPontuacaoSapsPotassioSerico(int value) {
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
		case ZERO:
			return "0";
		case NEGATIVO_3:
			return "-3";
		default:
			return "";
		}
	}

}