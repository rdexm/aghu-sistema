package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseRaca implements Dominio {
	//

	/**
	 * Ignorada
	 */
	IGNORADA(9),
	/**
	 * Indígena
	 */
	INDIGENA(5),
	/**
	 * Parda
	 */
	PARDA(4),
	/**
	 * Amarela
	 */
	AMARELA(3),
	/**
	 * Preta
	 */
	PRETA(2),
	/**
	 * Branca
	 */
	BRANCA(1);

	private int value;

	private DominioNotificacaoTuberculoseRaca(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case IGNORADA:
			return "Ignorada";
		case INDIGENA:
			return "Indígena";
		case PARDA:
			return "Parda";
		case AMARELA:
			return "Amarela";
		case PRETA:
			return "Preta";
		case BRANCA:
			return "Branca";
		default:
			return "";
		}
	}

}