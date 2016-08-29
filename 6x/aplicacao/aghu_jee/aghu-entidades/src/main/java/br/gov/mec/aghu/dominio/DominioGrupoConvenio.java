package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioGrupoConvenio implements Dominio {
	/**
	 * Convênio
	 */
	C, 
	/**
	 * Particular
	 */
	P,
	/**
	 * SUS
	 */
	S;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Convênio";
		case P:
			return "Particular";
		case S:
			return "SUS";
		default:
			return "";
		}
	}

}
