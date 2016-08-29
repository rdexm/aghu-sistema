package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * @author israel.haas
 * 
 */
public enum DominioTipoAgenda implements Dominio {
	/**
	 * Fixo
	 */
	F,
	/**
	 * Livre
	 */
	L;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "Fixo";
		case L:
			return "Livre";
		default:
			return "";
		}
	}

}
