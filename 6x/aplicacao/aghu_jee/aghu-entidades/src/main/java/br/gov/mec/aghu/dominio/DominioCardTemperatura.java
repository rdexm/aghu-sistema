package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioCardTemperatura implements Dominio {
	
	/**
	 * 
	 */
	F,
	/**
	 * 
	 */
	Q;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case F:
			return "Fria";
		case Q:
			return "Quente";
		default:
			return "";
		}
	}

}
