package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoAditivo implements Dominio {

	/**
	 * Acréscimo
	 */
	A,

	/**
	 * Supressão
	 */
	S;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Acréscimo";
		case S:
			return "Supressão";
		default:
			return "";
		}
	}

}
