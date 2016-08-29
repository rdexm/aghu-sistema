package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação de um prontuário.
 * 
 * @author ehgsilva
 * 
 */
public enum DominioPermiteManuscrito implements Dominio {
	
	/**
	 *  Unidade permite sumário de alta manuscrito
	 */
	U,

	/**
	 * SAMIS liberou sumário de alta manuscrito
	 */
	S;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case U:
			return "Unidade permite sumário de alta manuscrito";
		case S:
			return "SAMIS liberou sumário de alta manuscrito";
		default:
			return "";
		}
	}

}
