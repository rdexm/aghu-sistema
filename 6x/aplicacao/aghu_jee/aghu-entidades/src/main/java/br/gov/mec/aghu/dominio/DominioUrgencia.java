package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que representa as possíveis formas de urgência
 * 
 * @author israel.haas
 * 
 */
public enum DominioUrgencia implements Dominio {

	/**
	 * Urgente
	 */
	U, 
	
	/**
	 * Imediato
	 */
	I, 
	
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case U: 
			return "Urgente";
		case I: 
			return "Imediato";
		default:
			return "";
		}
	}
}
