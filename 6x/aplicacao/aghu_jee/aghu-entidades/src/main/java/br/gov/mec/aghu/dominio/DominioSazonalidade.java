package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author gandriotti
 * TODO
 */
public enum DominioSazonalidade implements Dominio {
	/**
	 */
	I,
	/**
	 */
	O,
	/**
	 */
	P,
	/**
	 */
	V,
	/**
	 */
	T;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Inverno";
		case O:
			return "Outono";
		case P:
			return "Primavera";
		case V:
			return "Verão";
		case T:
			return "Todo Ano";
		default:
			return "";
		}
	}
	
}
