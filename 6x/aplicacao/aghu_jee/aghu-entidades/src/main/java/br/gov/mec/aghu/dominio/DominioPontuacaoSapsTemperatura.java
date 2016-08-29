package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsTemperatura implements Dominio {
	//Pontuação Ficha SAPS para Temperatura


	/**
	 * >= 39
	 */
	POSITIVO_3(3),
	
	/**
	 * < 39
	 */
	ZERO(0);
	
	
	private int value;

	private DominioPontuacaoSapsTemperatura(int value) {
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
		default:
			return "";
		}
	}

}