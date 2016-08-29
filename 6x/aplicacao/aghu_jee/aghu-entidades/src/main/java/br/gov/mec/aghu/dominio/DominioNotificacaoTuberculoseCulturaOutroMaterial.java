package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseCulturaOutroMaterial implements Dominio {
	//
	
	
	/**
	 * Não Realizada
	 */
	POSITIVO_4(4),
	/**
	 * Em Andamento
	 */
	POSITIVO_3(3),
	/**
	 * Negativa 
	 */
	POSITIVO_2(2),
	/**
	 * Positiva
	 */
	POSITIVO_1(1);
	
		
	private int value;

	private DominioNotificacaoTuberculoseCulturaOutroMaterial(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case POSITIVO_4:
			return "4";
		case POSITIVO_3:
			return "3";
		case POSITIVO_2:
			return "2";
		case POSITIVO_1:
			return "1";
		default:
			return "";
		}
	}

}