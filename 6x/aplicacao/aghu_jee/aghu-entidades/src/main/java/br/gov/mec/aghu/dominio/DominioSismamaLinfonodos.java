package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio contendo os valores possíveis para classificar linfonodos - SISMAMA.
 * 
 * @author dpacheco
 *
 */
public enum DominioSismamaLinfonodos implements Dominio {
	
	/**
	 * Axilares
	 */
	AXILARES(1),
	
	/**
	 * Supraclaviculares
	 */
	SUPRACLAVICULARES(2);
	
	
	private Integer value;
	
	private DominioSismamaLinfonodos(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AXILARES:
			return "Axilares";
		case SUPRACLAVICULARES:
			return "Supraclaviculares";
		default:
			return "";
		}
	}
	
	public static DominioSismamaLinfonodos getInstance(Integer value) {
		switch (value) {
		case 1:
			return AXILARES;
		case 2:
			return SUPRACLAVICULARES;
		default:
			return null;
		}
	}
}
