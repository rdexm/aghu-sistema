package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoSala implements Dominio {
	/**
	 * Cirúrgica
	 */
	C,
	/**
	 * PDT
	 */
	P,
	/**
	 * Curativo
	 */
	U,
	/**
	 * Cirurgica/PDT
	 */
	A;

	private DominioTipoSala() {
	}

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Cirúrgica";
		case P:
			return "PDT";
		case U:
			return "Curativo";
		case A:
			return "Cirúrgica/PDT";
		default:
			return "";
		}
	}

}
