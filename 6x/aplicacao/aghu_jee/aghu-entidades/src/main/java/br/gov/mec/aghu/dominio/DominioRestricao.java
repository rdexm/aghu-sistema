package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author bsoliveira
 *
 */
public enum DominioRestricao implements Dominio {

	/**
	 * Obrigatório
	 */
	O, 
	/**
	 * Opcional
	 */
	C,
	/**
	 * Não Digita
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case O:
			return "Obrigatório";
		case C:
			return "Opcional";
		case N:
			return "Não Digita";
		default:
			return "";
		}
	}
	
}
