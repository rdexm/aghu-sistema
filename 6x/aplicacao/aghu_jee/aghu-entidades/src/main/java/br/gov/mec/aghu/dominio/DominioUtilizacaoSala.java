package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioUtilizacaoSala implements Dominio {

	/**
	 * Sala Prevista
	 */
	PRE,
	/**
	 * Sala Não Prevista
	 */
	NPR;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PRE:
			return "Sala prevista";
		case NPR:
			return "Sala não prevista";
		default:
			return "";
		}
	}
}
