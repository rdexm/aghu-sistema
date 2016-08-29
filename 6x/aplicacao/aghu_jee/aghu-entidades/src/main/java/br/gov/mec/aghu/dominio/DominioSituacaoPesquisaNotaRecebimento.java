package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoPesquisaNotaRecebimento implements Dominio {
	

	/**
	 * Gerado por
	 */
	G,
	/**
	 * Estornado por
	 */
	E;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case G:
			return "Gerador em";
		case E:
			return "Estornado em";
		default:
			return "";
		}
	}
	
}
