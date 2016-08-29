package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismReacaoPupila implements Dominio {
	//Pontuação Ficha PRISM para Reações Pupilares
	/**
	 * Fixas e Dilatadas
	 */
	POSITIVO_10(10),
	
	/**
	 * Anisoconcas ou Dilatadas
	 */
	POSITIVO_4(4),
	
	/**
	 * Normal
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoPrismReacaoPupila(int value) {
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
		case POSITIVO_4:
			return "4";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}