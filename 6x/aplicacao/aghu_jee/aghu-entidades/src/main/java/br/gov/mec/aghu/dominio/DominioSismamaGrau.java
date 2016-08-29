package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para valores de grau - SISMAMA.
 * 
 * @author dpacheco
 *
 */
public enum DominioSismamaGrau implements Dominio {
	
	/**
	 * I
	 */
	I(1), 
	
	/**
	 * II
	 */
	II(2),
	
	/**
	 * III
	 */
	III(3),
	
	/**
	 * Não Avaliável
	 */
	NAO_AVALIAVEL(4);
	
	
	private Integer value;
	
	private DominioSismamaGrau(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return value;
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
		case NAO_AVALIAVEL:
			return "Não Avaliável";
		default:
			return "";
		}
	}
	
	public static DominioSismamaGrau getInstance(Integer value) {
		switch (value) {
		case 1:
			return I;
		case 2:
			return II;
		case 3:
			return III;
		case 4:
			return NAO_AVALIAVEL;
		default:
			return null;
		}
	}
}
