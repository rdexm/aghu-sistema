package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoReceituario implements Dominio {
	/**
	 * Receita Geral
	 */
	G,
	/**
	 * Receita Especial	
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
			return "Receita geral";
		case E:
			return "Receita especial";
		default:
			return "";
		}
	}	
	
}
