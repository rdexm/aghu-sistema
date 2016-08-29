package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioCardMomento implements Dominio {
	
	/**
	 * 
	 */
	R,
	/**
	 * 
	 */
	A;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case R:
			return "Retrógrada";
		case A:
			return "Anterógrada";
		default:
			return "";
		}
	}

}
