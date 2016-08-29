package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsContagemWbc implements Dominio {
	//Pontuação Ficha SAPS para contagem de WBC


	/**
	 * >= 20,0
	 */
	POSITIVO_3(3),
	
	/**
	 * 1,0 - 19,9
	 */
	ZERO(0),

	
	/**
	 * < 1,0
	 */
	NEGATIVO_12(-12);
	
	
	private int value;

	private DominioPontuacaoSapsContagemWbc(int value) {
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
		case NEGATIVO_12:
			return "-12";
		default:
			return "";
		}
	}

}