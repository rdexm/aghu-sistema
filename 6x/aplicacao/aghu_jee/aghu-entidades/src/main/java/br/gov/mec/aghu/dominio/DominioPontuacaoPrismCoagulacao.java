package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoPrismCoagulacao implements Dominio {
	//Pontuação Ficha PRISM para Provas de Coagulação
	/**
	 * > 1,5 x controle
	 */
	POSITIVO_2(2),
	
	/**
	 * <= 1,5 x controle
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoPrismCoagulacao(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_2:
			return "2";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}