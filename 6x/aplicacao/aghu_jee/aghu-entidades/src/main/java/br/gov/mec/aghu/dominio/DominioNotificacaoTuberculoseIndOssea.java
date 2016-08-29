package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndOssea implements Dominio {
	//

	/**
	 * Óssea
	 */
	OSSEA(4),

	/**
	 * 
	 */
	ZERO(0);

	private int value;

	private DominioNotificacaoTuberculoseIndOssea(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case OSSEA:
			return "Óssea";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}