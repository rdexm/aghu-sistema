package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrigemFichaTecnicaEspecial implements Dominio {

	/**
	 * 
	 */
	C, 
	/**
	 * 
	 */
	M,
	/**
	 * 
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
			return "";
		case M:
			return "";
		case S:
			return "";
		default:
			return "";
		}
	}
	
}
