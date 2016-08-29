package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndInstitucionalizado implements Dominio {
	//
	
	/**
	 * Ignorado
	 */
	POSITIVO_9(9),
	/**
	 * Outro
	 */
	POSITIVO_6(6),
	/**
	 * Hospital Psiquiátrico
	 */
	POSITIVO_5(5),
	/**
	 * Orfanato
	 */
	POSITIVO_4(4),
	/**
	 * Asilo
	 */
	POSITIVO_3(3),
	/**
	 * Presídio
	 */
	POSITIVO_2(2),
	/**
	 * Não
	 */
	POSITIVO_1(1);
	
		
	private int value;

	private DominioNotificacaoTuberculoseIndInstitucionalizado(int value) {
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
		case POSITIVO_6:
			return "6";
		case POSITIVO_5:
			return "5";
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