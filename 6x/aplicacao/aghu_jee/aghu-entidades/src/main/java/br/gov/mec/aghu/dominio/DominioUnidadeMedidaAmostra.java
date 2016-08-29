package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author lalegre
 *
 */
public enum DominioUnidadeMedidaAmostra implements Dominio {

	/**
	 * Frasco
	 */
	FR, 
	/**
	 * Mililitro
	 */
	ML;


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case ML:
			return "Mililitro";
		case FR:
			return "Frasco";
		default:
			return "";
		}
	}
	
}
