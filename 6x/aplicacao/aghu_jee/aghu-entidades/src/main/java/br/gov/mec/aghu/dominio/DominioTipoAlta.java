package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTipoAlta implements Dominio {
	/**
	 * Todos
	 */
	T, 
	/**
	 * Sem óbitos
	 */
	E,
	/**
	 * Só óbitos
	 */
	O;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "Todos";
		case E:
			return "Sem óbitos";
		case O:
			return "Só óbitos";
		default:
			return "";
		}
	}

}