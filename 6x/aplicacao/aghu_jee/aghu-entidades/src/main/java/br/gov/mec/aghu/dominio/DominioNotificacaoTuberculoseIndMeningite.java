package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndMeningite implements Dominio {
	//

	/**
	 * Meningite
	 */
	MENINGITE(7),

	/**
	 * 
	 */
	ZERO(0);

	private int value;

	private DominioNotificacaoTuberculoseIndMeningite(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MENINGITE:
			return "Meningite";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}