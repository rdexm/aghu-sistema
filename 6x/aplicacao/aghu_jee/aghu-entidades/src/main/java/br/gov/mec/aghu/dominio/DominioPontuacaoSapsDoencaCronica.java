package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsDoencaCronica implements Dominio {
	//Pontuação Ficha SAPS para doença crônica


	/**
	 * SIDA
	 */
	POSITIVO_17(17),
	/**
	 * Doença Hematológica Maligna
	 */
	POSITIVO_10(10),

	/**
	 * Câncer Metástico
	 */
	POSITIVO_9(9);
	
	
	private int value;

	private DominioPontuacaoSapsDoencaCronica(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_9:
			return "9";
		case POSITIVO_10:
			return "10";
		case POSITIVO_17:
			return "17";
		default:
			return "";
		}
	}

}