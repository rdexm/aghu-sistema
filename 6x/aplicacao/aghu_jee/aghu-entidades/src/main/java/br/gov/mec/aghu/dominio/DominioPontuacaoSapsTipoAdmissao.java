package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPontuacaoSapsTipoAdmissao implements Dominio {
	//Pontuação Ficha SAPS para tipo de admissão


	/**
	 * Cirurgia de Urgência
	 */
	POSITIVO_8(8),
	/**
	 * Médica
	 */
	POSITIVO_6(6),
	
	/**
	 * Cirurgia Programada
	 */
	ZERO(0);
	
	
	private int value;

	private DominioPontuacaoSapsTipoAdmissao(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_8:
			return "8";
		case POSITIVO_6:
			return "6";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}