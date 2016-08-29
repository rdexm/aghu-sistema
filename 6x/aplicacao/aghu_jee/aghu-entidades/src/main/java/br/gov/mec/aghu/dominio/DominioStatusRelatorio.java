package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para reforçar retrições de tipo em campos com comportamento boleano.
 * 
 * @author lalegre
 * 
 */
public enum DominioStatusRelatorio implements Dominio {

	/**
	 * Definitivo
	 */
	D,
	/**
	 * Parcial
	 */
	P,
	/**
	 * Re-impressão
	 */
	R;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case D:
			return "Definitivo";
		case P:
			return "Parcial";
		case R:
			return "Re-impressão";
		default:
			return "Parcial";
		}
	}

}
