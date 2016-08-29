package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author bsoliveira
 * 
 */
public enum DominioPrioridadeInterconsultas implements Dominio {

	/**
	 * Alta
	 */
	A, 
	/**
	 * Média
	 */
	M,
	/**
	 * Baixa
	 */
	B;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Alta";
		case M:
			return "Média";
		case B:
			return "Baixa";	
		default:
			return "";
		}
	}
	
}
