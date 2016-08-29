package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoCribMalformacaoCongenita implements Dominio {
	
	/**
	 * Com necessidade de UTI
	 */
	POSITIVO_3(3),
	/**
	 * Sem necessidade de UTI
	 */
	POSITIVO_1(1),
	/**
	 * Nenhuma
	 */
	ZERO(0);
	
	private int value;

	private DominioPontuacaoCribMalformacaoCongenita(int value) {
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
		case POSITIVO_1:
			return "1";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}