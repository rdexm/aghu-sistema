package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismBilirrubina implements Dominio {
	//Pontuação Ficha PRISM para Bilirrubinas
	/**
	 * > 3,5 maiores 1 mês
	 */
	POSITIVO_6(6),
	
	/**
	 * <= 3,5
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoPrismBilirrubina(int value) {
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