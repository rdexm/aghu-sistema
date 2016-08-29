package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o Estado Civil de uma pessoa 
 * 
 * @author ehgsilva
 * 
 */
public enum DominioEstadoCivil implements Dominio {
	
	/**
	 * Casado
	 */
	C,

	/**
	 * Solteiro
	 */
	S,
	
	/**
	 * Desquitado ou separado judicialmente
	 */
	P,

	/**
	 * Divorciado
	 */
	D,
	
	/**

	 * União Estável

	 */

	U,

	/**

	
	/**
	 * Viúvo
	 */
	V,
	
	/**
	 * Outro
	 */
	O;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Casado";
		case S:
			return "Solteiro";
		case P:
			return "Separado";
		case D:
			return "Desquitado ou separado judicialmente";
		case U:
			return "União Estável";
		case V:
			return "Viúvo";	
		case O:
			return "Outro";			
		default:
			return "";
		}
	}

}
