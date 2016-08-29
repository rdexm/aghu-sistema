package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioModoExibicao implements Dominio {
	
	/**
	 * Tela
	 */
	T,
	/**
	 * Impressão
	 */
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {	
		switch (this) {
		case T:
			return "Tela";
		case I:
			return "Impressão";		
		default:
			return "";
		}
	}

}
