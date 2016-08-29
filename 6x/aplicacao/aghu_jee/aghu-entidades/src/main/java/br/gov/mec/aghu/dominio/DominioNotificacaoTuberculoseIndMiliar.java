package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndMiliar implements Dominio {
	//

	/**
	 * Miliar
	 */
	MILIAR(6),

	/**
	 * 
	 */
	ZERO(0);

	private int value;

	private DominioNotificacaoTuberculoseIndMiliar(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MILIAR:
			return "Miliar";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}