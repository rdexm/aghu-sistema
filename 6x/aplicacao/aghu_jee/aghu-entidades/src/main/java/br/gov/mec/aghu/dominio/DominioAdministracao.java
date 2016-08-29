package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de uma entidade, se Parcial ou Total.
 * 
 * @author gmneto
 * 
 */
public enum DominioAdministracao implements Dominio {
	/**
	 * Parcial
	 */
	P,

	/**
	 * Total
	 */
	T,
	
	/**
	 * Valor Padrão
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Parcial";
		case T:
			return "Total";
		case N:
			return "N";
		default:
			return "";
		}
	}
}
