package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoInfeccao implements Dominio {

	/**
	 * Hospitalar
	 */
	H,
	/**
	 * Comunitária
	 */
	C,
	/**
	 * Não estabelecida
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		switch (this) {
		case H:
			return "Hospitalar";
		case C:
			return "Comunitária";
		case N:
			return "Não estabelecida";
		default:
			return "";
		}
	}
}
