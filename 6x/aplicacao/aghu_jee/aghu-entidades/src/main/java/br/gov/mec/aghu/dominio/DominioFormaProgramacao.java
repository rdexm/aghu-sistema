package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Dominio que representa as possíveis formas de programação
 * 
 * @author israel.haas
 * 
 */
public enum DominioFormaProgramacao implements Dominio {

	/**
	 * Programar todo Saldo
	 */
	PS, 
	
	/**
	 * Quantidade Limite
	 */
	QL, 
	
	/**
	 * Valor Limite
	 */
	VL, 
	
	/**
	 * Nº de Dias
	 */
	ND, 
	
	/**
	 * Número de Parcelas
	 */
	NP, 
	
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PS: 
			return "Programar todo Saldo";
		case QL: 
			return "Quantidade Limite";
		case VL: 
			return "Valor Limite";
		case ND: 
			return "Nº de Dias";
		case NP: 
			return "Número de Parcelas";
		default:
			return "";
		}
	}
}
