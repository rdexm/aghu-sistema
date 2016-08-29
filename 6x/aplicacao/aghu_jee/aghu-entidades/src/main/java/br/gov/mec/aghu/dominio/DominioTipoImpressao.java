package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoImpressao implements Dominio {
	
	/**
	 * Todos
	 */
	T, 
	/**
	 * A partir de
	 */
	A,
	/**
	 * Informado
	 */
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "Todos";
		case A:
			return "A partir de";
		case I:
			return "Informado";
		default:
			return "";
		}
	}
}
