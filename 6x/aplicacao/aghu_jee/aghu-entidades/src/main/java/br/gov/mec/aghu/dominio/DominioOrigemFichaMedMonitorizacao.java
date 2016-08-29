package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Indica qual a origem da consulta para o ambulatório.
 * 
 * @author diego.pacheco
 *
 */
public enum DominioOrigemFichaMedMonitorizacao implements Dominio {
	
	/**
	 * 
	 */
	I,
	/**
	 * 
	 */
	C,
	/**
	 * 
	 */
	E,
	/**
	 * 
	 */
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "";
		case C:
			return "";
		case E:
			return "";
		case M:
			return "";
		default:
			return "";
		}
	}

}
