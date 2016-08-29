package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para classificação de receptor - SISMAMA.
 * 
 * @author dpacheco
 *
 */
public enum DominioSismamaReceptor implements Dominio {
	
	/**
	 * Negativo
	 */
	NEGATIVO(1),
	
	/**
	 * NR
	 */
	NR(2),
	
	/**
	 * Positivo
	 */
	POSITIVO(3);
	
	
	private Integer value;
	
	private DominioSismamaReceptor(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NEGATIVO:
			return "Negativo";
		case NR:
			return "NR";
		case POSITIVO:
			return "Positivo";
		default:
			return "";
		}
	}
	
	public static DominioSismamaReceptor getInstance(Integer value) {
		switch (value) {
		case 1:
			return NEGATIVO;
		case 2:
			return NR;
		case 3:
			return POSITIVO;
		default:
			return null;
		}
	}
}
