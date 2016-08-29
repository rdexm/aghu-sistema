package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoNotificacao implements Dominio {

	/**
	 * Queda do leito
	 */
	Q,

	/**
	 * Úlcera de pressão
	 */
	U;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case Q:
			return "Queda do leito";
		case U:
			return "Úlcera de pressão";
		default:
			return "";
		}
	}

}
