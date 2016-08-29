package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseZona implements Dominio {
	//

	/**
	 * Urbana
	 */
	URBANA(1),
	/**
	 * Rural
	 */
	RURAL(2),
	/**
	 * Urbana/Rural
	 */
	URBANA_RURAL(3),
	/**
	 * Ignorado
	 */
	IGNORADO(9);

	private int value;

	private DominioNotificacaoTuberculoseZona(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case IGNORADO:
			return "Ignorado";
		case URBANA_RURAL:
			return "Urbana/Rural";
		case RURAL:
			return "Rural";
		case URBANA:
			return "Urbana";
		default:
			return "";
		}
	}

}