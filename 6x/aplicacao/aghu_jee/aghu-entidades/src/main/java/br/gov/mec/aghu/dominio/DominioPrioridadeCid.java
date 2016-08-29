package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPrioridadeCid implements Dominio {
	/**
	 * Principal.
	 */
	P,
	/**
	 * Secundário.
	 */
	S,
	
	/**
	 * Nenhuma.
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
			return "Principal";
		case S:
			return "Secundário";
		case N:
			return "Nenhuma";
		default:
			return "";
		}
	}
}