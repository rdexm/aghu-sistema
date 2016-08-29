package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismOxigPao2Fio2 implements Dominio {
	//Pontuação Ficha PRISM para Oxigênio PaO2/FIO2
	/**
	 * < 200
	 */
	POSITIVO_3(3),
	/**
	 * 200 - 300
	 */
	POSITIVO_2(2),
	/**
	 * > 300
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoPrismOxigPao2Fio2(int value) {
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
		case POSITIVO_2:
			return "2";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}