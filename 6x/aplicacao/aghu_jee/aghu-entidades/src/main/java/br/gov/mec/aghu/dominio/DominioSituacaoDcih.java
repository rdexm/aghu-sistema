package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que representa as possíveis situações de um DCIH
 * 
 * @author lcmoura
 * 
 */
public enum DominioSituacaoDcih implements Dominio {

	/**
	 * Rejeitada
	 */
	R, 
	
	/**
	 * Apresentada ao SUS
	 */
	A, 
	
	/**
	 * Não Apresentada
	 */
	N,
	
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case R: 
			return "Rejeitada";
		case A: 
			return "Apresentada ao SUS";
		case N: 
			return "Não Apresentada";			
		default:
			return "";
		}
	}
}
