package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica as margens cirúrgicas - SISMAMA.
 * 
 * @author dpacheco
 *
 */
public enum DominioSismamaMargensCirurgicas implements Dominio {
	
	/**
	 * Livres
	 */
	LIVRES(1),
	
	/**
	 * Não Avaliável
	 */
	NAO_AVALIAVEL(2),
	
	/**
	 * Comprometidas
	 */
	COMPROMETIDAS(3);
	
	
	private Integer value;
	
	private DominioSismamaMargensCirurgicas(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case LIVRES:
			return "Livres";
		case NAO_AVALIAVEL:
			return "Não Avaliável";
		case COMPROMETIDAS:
			return "Comprometidas";
		default:
			return "";
		}
	}
	
	public static DominioSismamaMargensCirurgicas getInstance(Integer value) {
		switch (value) {
		case 1:
			return LIVRES;
		case 2:
			return NAO_AVALIAVEL;
		case 3:
			return COMPROMETIDAS;
		default:
			return null;
		}
	}
}
