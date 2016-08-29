package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoControleVidaUtil implements Dominio{
	/**
	 * Tempo
	 */
	T,
	/**
	 * Quantidade
	 */
	Q;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "Tempo";
		case Q:
			return "Quantidade";
		default:
			return "";
		}
	}

}
