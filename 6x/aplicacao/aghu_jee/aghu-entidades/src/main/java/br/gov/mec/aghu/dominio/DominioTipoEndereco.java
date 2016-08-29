package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoEndereco implements Dominio {
	/**
	 * Comercial
	 */
	C,
	/**
	 * Residencial
	 */
	R,
	/**
	 * Contato
	 */
	M,
	/**
	 * Outros
	 */
	O;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Comercial";
		case R:
			return "Residencial";
		case M:
			return "Contato";
		case O:
			return "Outros";
		default:
			return "";
		}

	}

}
