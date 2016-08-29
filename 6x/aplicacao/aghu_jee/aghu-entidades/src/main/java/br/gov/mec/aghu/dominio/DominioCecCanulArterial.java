package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioCecCanulArterial implements Dominio {
	
	/**
	 * 
	 */
	A,
	/**
	 * 
	 */
	F;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "Aórtica";
		case F:
			return "Femoral";
		default:
			return "";
		}
	}

}
