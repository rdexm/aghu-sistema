package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoCentroCusto implements Dominio {
	/**
	 * ??
	 */
	S,

	/**
	 * ??
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Solicitante";
		case A:
			return "Aplicação";
		default:
			return "";
		}
	}
}
