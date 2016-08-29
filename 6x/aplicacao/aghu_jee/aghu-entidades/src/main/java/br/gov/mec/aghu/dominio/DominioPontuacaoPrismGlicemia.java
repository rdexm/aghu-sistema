package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismGlicemia implements Dominio {
	//Pontuação Ficha PRISM para Glicemia
	/**
	 * > 400
	 */
	POSITIVO_8(8),
	
	/**
	 * 250 - 400
	 */
	POSITIVO_4(4),
	
	/**
	 * 61 - 249
	 */
	ZERO(0),

	/**
	 * 40 - 60
	 */
	NEGATIVO_4(-4),
	/**
	 * < 40
	 */
	NEGATIVO_8(-8);
	
	private int value;

	private DominioPontuacaoPrismGlicemia(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_8:
			return "8";
		case POSITIVO_4:
			return "4";
		case ZERO:
			return "0";
		case NEGATIVO_4:
			return "-4";
		case NEGATIVO_8:
			return "-8";
		default:
			return "";
		}
	}

}