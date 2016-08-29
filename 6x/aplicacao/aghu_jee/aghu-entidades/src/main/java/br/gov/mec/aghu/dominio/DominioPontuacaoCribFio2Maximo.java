package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoCribFio2Maximo implements Dominio {
	//Pontuação da Ficha CRIB para FIO2 máximo nas primeiras 12 horas
	/**
	 * 0,91 - 1,00
	 */
	POSITIVO_5(5),
	/**
	 * 0,81 - 0,90
	 */
	POSITIVO_3(3),
	/**
	 * 0,41 - 0,80
	 */
	POSITIVO_1(1),
	/**
	 * <= 0,40
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoCribFio2Maximo(int value) {
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
		case POSITIVO_3:
			return "3";
		case POSITIVO_1:
			return "1";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}