package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndCutanea implements Dominio {
	//

	/**
	 * Cutânea
	 */
	CUTANEA(8),

	/**
	 * 
	 */
	ZERO(0);

	private int value;

	private DominioNotificacaoTuberculoseIndCutanea(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CUTANEA:
			return "8";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}