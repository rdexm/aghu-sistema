package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseIndGangPerif implements Dominio {
	//

	/**
	 * GangPerif
	 */
	GANG_PERIF(2),

	/**
	 * 
	 */
	ZERO(0);

	private int value;

	private DominioNotificacaoTuberculoseIndGangPerif(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case GANG_PERIF:
			return "GangPerif";
		case ZERO:
			return "0";
		default:
			return "";
		}
	}

}