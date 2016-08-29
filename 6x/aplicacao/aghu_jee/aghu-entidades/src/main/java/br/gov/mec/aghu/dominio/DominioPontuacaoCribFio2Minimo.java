package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoCribFio2Minimo implements Dominio {
	//Pontuação da Ficha CRIB para FIO2 mínimo nas primeiras 12 horas
	/**
	 * 0,91 - 1,00
	 */
	POSITIVO_4(4),
	/**
	 * 0,61 - 0,90
	 */
	POSITIVO_3(3),
	/**
	 * 0,41 - 0,60
	 */
	POSITIVO_2(2),
	/**
	 * <= 0,40
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoCribFio2Minimo(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_4:
			return "4";
		case POSITIVO_3:
			return "3";
		case POSITIVO_2:
			return "2";
		case ZERO:
			return "0";
		
		default:
			return "";
		}
	}

}