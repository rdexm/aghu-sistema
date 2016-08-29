package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoIdadeUTI implements Dominio {

	/**
	 * Neonatal
	 */
	N,
	/**
	 * Pediatrica
	 */
	P,
	/**
	 * Adulto
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Neonatal";
		case P:
			return "Pediátrica";
		case A:
			return "Adulto";
		default:
			return "";
		}
	}

}
