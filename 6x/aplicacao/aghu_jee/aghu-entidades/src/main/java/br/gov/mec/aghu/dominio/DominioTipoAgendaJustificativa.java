package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioTipoAgendaJustificativa implements Dominio {
	/**
	 * 
	 */
	ELC,
	
	/**
	 * 
	 */
	ELE,
	
	/**
	 * 
	 */
	EES,
	
	/**
	 * 
	 */
	EEA,
	
	/**
	 * 
	 */
	REA,
	
	/**
	 * 
	 */
	REE,
	
	/**
	 * 
	 */
	TAC,
	
	/**
	 * 
	 */
	TAE,
	
	/**
	 * 
	 */
	TEC,
	
	/**
	 * 
	 */
	TEE;
	

	@Override
	public int getCodigo() {
		// TODO Auto-generated method stub
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ELC:
			return "";
		case ELE:
			return "";
		case EES:
			return "";
		case EEA:
			return "";
		case REA:
			return "";
		case REE:
			return "";
		case TAC:
			return "";
		case TAE:
			return "";
		case TEC:
			return "";
		case TEE:
			return "";

		default:
			return "";
		}
	}

}
