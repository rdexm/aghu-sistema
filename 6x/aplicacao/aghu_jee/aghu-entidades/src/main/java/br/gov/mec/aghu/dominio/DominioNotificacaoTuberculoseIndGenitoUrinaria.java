package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndGenitoUrinaria implements Dominio {
	//

	/**
	 * Pleural
	 */
	PLEURAL(3),

	/**
	 * 
	 */
	ZERO(0);

	private int value;

	private DominioNotificacaoTuberculoseIndGenitoUrinaria(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PLEURAL:
			return "Pleural";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}