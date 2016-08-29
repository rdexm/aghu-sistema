package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoPrescricaoProcedimento implements Dominio {

	/**
	 * Procedimento
	 */
	P,
	/**
	 * Material
	 */
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		switch (this) {
		case P:
			return "Procedimento";
		case M:
			return "Material";
		default:
			return "";
		}
	}
}
