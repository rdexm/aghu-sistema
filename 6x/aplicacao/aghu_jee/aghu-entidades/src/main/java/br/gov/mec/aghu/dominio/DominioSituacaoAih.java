package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que representa as possíveis situações de um AIH
 * 
 * @author lcmoura
 * 
 */
public enum DominioSituacaoAih implements Dominio {

	/**
	 * Reapresentada
	 */
	R, 
	
	/**
	 * Apresentada
	 */
	A, 
	
	/**
	 * Liberada
	 */
	L, 
	
	/**
	 * Bloqueada
	 */
	B, 
	
	/**
	 * Útil
	 */
	U, 
	
	/**
	 * Vencida
	 */
	V,
	
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case R: 
			return "Reapresentada";
		case A: 
			return "Apresentada";
		case L: 
			return "Liberada";
		case B: 
			return "Bloqueada";
		case U: 
			return "Útil";
		case V:
			return "Vencida";		
		default:
			return "";
		}
	}
}
