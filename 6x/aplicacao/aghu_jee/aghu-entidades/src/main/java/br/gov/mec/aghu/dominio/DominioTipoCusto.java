package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica tipo da custo de um centro de custo
 */
public enum DominioTipoCusto implements Dominio {
	/**
	 * Produtivo
	 */
	P,
	
	/**
	 * Base
	 */
	B,
	
	/**
	 * Intermediário
	 */
	I,
	
	/**
	 * Final
	 */
	F;

	@Override
	public int getCodigo() {
		// TODO Auto-generated method stub
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Produtivo";
		case B:
			return "Base";
		case I:
			return "Intermediário";
		case F:
			return "Final";

		default:
			return "";
		}
	}

}
