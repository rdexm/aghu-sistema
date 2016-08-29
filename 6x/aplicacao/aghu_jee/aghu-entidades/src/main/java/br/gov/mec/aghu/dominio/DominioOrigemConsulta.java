package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Indica qual a origem da consulta para o ambulatório.
 * 
 * @author diego.pacheco
 *
 */
public enum DominioOrigemConsulta implements Dominio {
	
	/**
	 * AGHU
	 */
	A,
	/**
	 * SISREG
	 */
	S;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "AGHU";
		case S:
			return "SISREG";
		default:
			return "";
		}
	}

}
