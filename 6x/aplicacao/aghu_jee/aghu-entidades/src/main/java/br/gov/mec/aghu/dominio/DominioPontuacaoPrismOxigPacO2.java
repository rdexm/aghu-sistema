package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismOxigPacO2 implements Dominio {
	//Ficha PRISM para Oxigênio PaCO2
	/**
	 * > 65
	 */
	POSITIVO_5(5),
	
	/**
	 * 51 - 65
	 */
	POSITIVO_1(1),
	
	/**
	 * <= 50
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoPrismOxigPacO2(int value) {
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