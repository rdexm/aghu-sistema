package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Esse dominio é usado na tabela FAT_PROCED_HOSP_INTERNOS nos campos tipoNutrParenteral e tipoNutricaoEnteral
 * tipoNutrParenteral é aplicada por veia
 * tipoNutricaoEnteral é por sonda
 * 
 */
public enum DominioTipoNutricaoParenteral implements Dominio {
	/**
	 * Adulto
	 */
	A,
	/**
	 * Pediatria
	 */
	P,

	/**
	 * Neo Natal
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Adulto";
		case P:
			return "Pediatria";
		case N:
			return "Neo Natal";
		default:
			return "";
		}
	}
	
	
	

}
