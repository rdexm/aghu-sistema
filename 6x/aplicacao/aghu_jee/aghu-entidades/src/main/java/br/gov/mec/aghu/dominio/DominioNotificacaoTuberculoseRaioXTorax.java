package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioNotificacaoTuberculoseRaioXTorax implements Dominio {
	//

	/**
	 * Suspeito
	 */
	SUSPEITO(1),

	/**
	 * Normal
	 */
	NORMAL(2),

	/**
	 * Outra Patologia
	 */
	OUTRA_PATOLOGIA(3),

	/**
	 * Não Realizado
	 */
	NAO_REALIZADO(4);

	private int value;

	private DominioNotificacaoTuberculoseRaioXTorax(int value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case SUSPEITO:
			return "Suspeito";
		case NORMAL:
			return "Normal";
		case OUTRA_PATOLOGIA:
			return "Outra Patologia";
		case NAO_REALIZADO:
			return "Não Realizado";
		default:
			return "";
		}
	}

}