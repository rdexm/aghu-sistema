package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsPressaoArterial implements Dominio {
	//Pontuação Ficha SAPS para pressão arterial


	/**
	 * >= 200
	 */
	POSITIVO_2(2),
	
	/**
	 * 100 - 199
	 */
	ZERO(0),

	/**
	 * 70 - 99
	 */
	NEGATIVO_5(-5),

	/**
	 * < 70
	 */
	NEGATIVO_13(-13);
	
	
	private int value;

	private DominioPontuacaoSapsPressaoArterial(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_2:
			return "2";
		case ZERO:
			return "0";
		case NEGATIVO_5:
			return "-5";
		case NEGATIVO_13:
			return "-13";
		default:
			return "";
		}
	}

}