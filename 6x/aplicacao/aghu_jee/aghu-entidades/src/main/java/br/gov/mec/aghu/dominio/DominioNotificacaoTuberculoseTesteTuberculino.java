package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseTesteTuberculino implements Dominio {
	//Tipo de aprovação de uma solicitação de revisão de padronização
	
	/**
	 * Não Reator
	 */
	POSITIVO_1(1),
	
	/**
	 * Reator Fraco
	 */
	POSITIVO_2(2),
	
	/**
	 * Reator Forte
	 */
	POSITIVO_3(3),
	
	/**
	 * Não Realizado
	 */
	POSITIVO_4(4);
	
		
	private int value;

	private DominioNotificacaoTuberculoseTesteTuberculino(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_1:
			return "1";
		case POSITIVO_2:
			return "2";
		case POSITIVO_3:
			return "3";
		case POSITIVO_4:
			return "4";
		default:
			return "";
		}
	}

}