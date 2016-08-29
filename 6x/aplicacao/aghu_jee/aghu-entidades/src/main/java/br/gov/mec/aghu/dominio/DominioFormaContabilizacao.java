package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author israel.haas
 * 
 */
public enum DominioFormaContabilizacao implements Dominio {

	/**
	 * Própria Unidade
	 */
	P,

	/**
	 * Unidade Anterior
	 */
	A;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case P:
			return "Própria Unidade";
		case A:
			return "Unidade Anterior";
		default:
			return "";
		}
	}
}
