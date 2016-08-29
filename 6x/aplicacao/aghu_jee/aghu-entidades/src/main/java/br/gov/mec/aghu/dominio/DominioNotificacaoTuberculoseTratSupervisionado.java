package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseTratSupervisionado implements Dominio {
	//
	
	/**
	 * Ignorado
	 */
	POSITIVO_9(9),
	/**
	 * Não
	 */
	POSITIVO_2(2),
	/**
	 * Sim
	 */
	POSITIVO_1(1);
	
		
	private int value;

	private DominioNotificacaoTuberculoseTratSupervisionado(int value) {
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
		case POSITIVO_2:
			return "2";
		case POSITIVO_1:
			return "1";
		default:
			return "";
		}
	}

}