package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioJustificativa implements Dominio {
	/**
	 * Obrigatório
	 */
	O, 
	/**
	 * Opcional
	 */
	P,
	/**
	 * Não Digita
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case O:
			return "Obrigatório";
		case P:
			return "Opcional";
		case N:
			return "Não Digita";
		default:
			return "";
		}
	}
}
