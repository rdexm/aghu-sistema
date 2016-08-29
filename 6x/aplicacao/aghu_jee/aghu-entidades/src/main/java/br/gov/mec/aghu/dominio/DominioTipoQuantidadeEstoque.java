package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoQuantidadeEstoque implements Dominio {
	
	/**
	 * Bloqueada
	 */
	BLOQUEADA,

	/**
	 * Disponivel
	 */
	DISPONIVEL, 
	
	/**
	 * Total
	 */
	TOTAL; 
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case BLOQUEADA:
			return "B";
		case DISPONIVEL:
			return "D";
		case TOTAL:
			return "T";
		default:
			return "";
		}
	}

}
