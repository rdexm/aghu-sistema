package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsBilirrubina implements Dominio {
	//Pontuação Ficha SAPS para no nível de bilirrubinas


	/**
	 * >= 102,6
	 */
	POSITIVO_9(9),
	/**
	 * 68,4 - 102,5
	 */
	POSITIVO_4(4),
	
	/**
	 * < 68,4
	 */
	ZERO(0);
	
	
	private int value;

	private DominioPontuacaoSapsBilirrubina(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_9:
			return "9";
		case POSITIVO_4:
			return "4";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}