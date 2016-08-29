package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoValidade implements Dominio {

	
	/**
	 * Qualquer Amostra.
	 */
	Q,
	/**
	 * Ultima Amostra.
	 */
	U;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case Q:
			return "Qualquer Amostra";
		case U:
			return "Ultima Amostra";
		default:
			return "";
		}
	}
	
}
