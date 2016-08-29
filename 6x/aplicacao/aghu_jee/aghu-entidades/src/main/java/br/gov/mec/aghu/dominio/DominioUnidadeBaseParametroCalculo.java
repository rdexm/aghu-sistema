package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioUnidadeBaseParametroCalculo implements Dominio {

	/**
	 * Dose unitária
	 */
	D,
	/**
	 * m2
	 */
	M,
	/**
	 * AUC
	 */
	A,
	/**
	 * Kg
	 */
	K;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case D:
			return "Dose unitária";
		case M:
			return "m2";
		case A:
			return "AUC";
		case K:
			return "Kg";
		default:
			return "";
		}
	}

}
