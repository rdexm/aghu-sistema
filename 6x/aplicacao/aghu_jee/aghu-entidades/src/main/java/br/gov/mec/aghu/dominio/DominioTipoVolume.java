package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoVolume implements Dominio {
	/**
	 * Validado
	 */
	K,
	/**
	 * Não validado
	 */
	M,
	/**
	 * Exclusão não validada
	 */
	A,
	/**
	 * Alteração não validada
	 */
	V,
	/**
	 * Não validado - Modelo Básico
	 */
	T;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case K:
			return "Kg";
		case M:
			return "m2";
		case A:
			return "Kg ou m2";
		case V:
			return "ml";
		case T:
			return "kg/min";
		default:
			return "";
		}
	}

}
