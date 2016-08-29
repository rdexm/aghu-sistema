package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismPressaoArterialDiastolica implements Dominio {
	//Pontuação Ficha PRISM para PA Diastólica
	
	/**
	 * > 110
	 */
	POSITIVO_6(6),
	
	/**
	 * 0 - 110
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoPrismPressaoArterialDiastolica(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_6:
			return "6";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}