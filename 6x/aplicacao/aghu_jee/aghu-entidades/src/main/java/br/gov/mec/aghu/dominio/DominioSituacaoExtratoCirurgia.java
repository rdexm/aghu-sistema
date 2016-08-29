package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioSituacaoExtratoCirurgia implements Dominio {

	/**
	 * 
	 */
	AGND,
	/**
	 * 
	 */
	RZDA,
	/**
	 * 
	 */
	CANC,
	/**
	 * 
	 */
	CHAM,
	/**
	 * 
	 */
	PREP,
	/**
	 * 
	 */
	TRAN;
	
		
	private int value;

	@Override
	public int getCodigo() {
		return value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AGND:
			return "";
		case RZDA:
			return "";
		case CANC:
			return "";
		case CHAM:
			return "";
		case PREP:
			return "";
		case TRAN:
			return "";
		default:
			return "";
		}
	}
	
}
