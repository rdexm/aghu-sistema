package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoProducao implements Dominio{
	/**
	 * Finalistico
	 */
	F,
	/**
	 * Intermediario
	 */
	I,
	/**
	 * Apoio
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "Finalístico";
		case I:
			return "Intermediário";
		case A:
			return "Apoio";
		default:
			return "";
		}
	}

}
