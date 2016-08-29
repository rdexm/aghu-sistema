package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTodosUltimo implements Dominio {
	
	/**
	 * Todos
	 */
	T, 
	/**
	 * Último
	 */
	U;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case T:
			return "Todos";
		case U:
			return "Último";		
		default:
			return "";
		}
	}

}
