package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o número de linfonodos comprometidos - SISMAMA.
 * 
 * @author dpacheco
 *
 */
public enum DominioSismamaNumeroLinfonodosComprometidos implements Dominio {
	
	/**
	 * 0 (zero) 
	 */
	ZERO(1),
	
	/**
	 * 1 a 3
	 */
	UM_A_TRES(2),
	
	/**
	 * 4 a 10
	 */
	QUATRO_A_DEZ(3),
	
	/**
	 * >10
	 */
	MAIS_DE_DEZ(4);
	
	
	private Integer value;
	
	private DominioSismamaNumeroLinfonodosComprometidos(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ZERO:
			return "0 (zero)";
		case UM_A_TRES:
			return "1 a 3";
		case QUATRO_A_DEZ:
			return "4 a 10";
		case MAIS_DE_DEZ:
			return ">10";
		default:
			return "";
		}
	}
	
	public static DominioSismamaNumeroLinfonodosComprometidos getInstance(
			Integer value) {
		switch (value) {
		case 1:
			return ZERO;
		case 2:
			return UM_A_TRES;
		case 3:
			return QUATRO_A_DEZ;
		case 4:
			return MAIS_DE_DEZ;
		default:
			return null;
		}
	}
}
