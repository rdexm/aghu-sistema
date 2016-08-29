package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseForma implements Dominio {
	//

	/**
	 * Pulmonar
	 */
	PULMONAR(1),

	/**
	 * Extrapulmonar
	 */
	EXTRAPULMONAR(2),

	/**
	 * Pulmonar + Extrapulmonar
	 */
	PULMONAR_EXTRAPULMONAR(3);

	private int value;

	private DominioNotificacaoTuberculoseForma(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PULMONAR:
			return "Pulmonar";
		case EXTRAPULMONAR:
			return "Extrapulmonar";
		case PULMONAR_EXTRAPULMONAR:
			return "Pulmonar + Extrapulmonar";
		default:
			return "";
		}
	}

}