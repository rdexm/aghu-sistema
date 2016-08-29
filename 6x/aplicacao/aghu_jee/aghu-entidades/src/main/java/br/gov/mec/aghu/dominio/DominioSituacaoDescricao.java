package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica situação da descrição.
 * 
 * @author lcmoura
 *
 */
public enum DominioSituacaoDescricao implements Dominio {
	
	/**
	 * Preliminar
	 */
	PRE, 
	
	/**
	 * Definitivo
	 */
	DEF,
	
	/**
	 * Pendente
	 */
	PEN;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PRE:
			return "Preliminar";
		case DEF:
			return "Definitivo";
		case PEN:
			return "Pendente";
		default:
			return "";
		}
	}

}
