package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioVencimentoDiaNaoUtil implements Dominio {
	

	/**
	 * Antecipa
	 */
	A,
	
	/**
	 * Posterga
	 */
	P,
	
	/**
	 * Mantém data
	 */
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Antecipa";
		case P:
			return "Posterga";
		case M:
			return "Mantém data";
		default:
			return "";
		}
	}
}