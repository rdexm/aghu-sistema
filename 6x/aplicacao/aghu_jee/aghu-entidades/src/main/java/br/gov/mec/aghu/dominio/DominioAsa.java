package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica asa
 * 
 * @author Heliz
 * 
 */



public enum DominioAsa implements Dominio {
	
	/**
	 * 1
	 */
	I(1),//Valor 1, porque ordinal deste registro é 1
	
	/**
	 * 2
	 */
	II(2),//VALOR 2
	
	/**
	 * 3
	 */
	III(3),
	
	/**
	 * 4
	 */
	IV(4),
	
	/**
	 * 5
	 */
	V(5);
	
	private Integer value;
		
	private DominioAsa(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		
		switch (this) {
		case I:
			return "I";
		case II:
			return "II";
		case III:
			return "III";			
		case IV:
			return "IV";
		case V:
			return "V";
		default:
			return "";
		}
	}

}
