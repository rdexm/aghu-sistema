package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoProcedimento implements Dominio {

	/**
	 * Motora
	 */
	M,

	/**
	 * Respiratória
	 */
	R,

	/**
	 * Ambos
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case M:
			return "Motora";
		case R:
			return "Respiratória";
		case A:
			return "Ambos";
		default:
			return "";
		}
	}

}
