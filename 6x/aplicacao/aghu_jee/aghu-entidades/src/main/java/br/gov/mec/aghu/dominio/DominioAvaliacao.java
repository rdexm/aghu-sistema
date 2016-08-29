package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAvaliacao implements Dominio {
	/**
	 * Aprovada.
	 */
	A,
	/**
	 * Não aprovada.
	 */
	N,
	
	/**
	 * Pendente avaliação.
	 */
	P;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Aprovada";
		case N:
			return "Não aprovada";
		case P:
			return "Pendente avaliação";
		default:
			return "";
		}
	}
}