package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsNivelUreiaSerica implements Dominio {
	//Pontuação Ficha SAPS para nível da uréia sérica


	/**
	 * >= 30,0
	 */
	POSITIVO_10(10),
	/**
	 * 10,0 - 29,9
	 */
	POSITIVO_6(6),
	
	/**
	 * < 10,0
	 */
	ZERO(0);
	
	
	private int value;

	private DominioPontuacaoSapsNivelUreiaSerica(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_10:
			return "10";
		case POSITIVO_6:
			return "6";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}