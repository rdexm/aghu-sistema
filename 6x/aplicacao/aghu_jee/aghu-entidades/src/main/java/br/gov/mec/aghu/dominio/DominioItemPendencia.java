package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para campos de tela.
 * 
 * @author marcelo.corati
 * 
 */
public enum DominioItemPendencia implements Dominio {
	/**
	 * Item 1
	 */
	I1,
	/**
	 * Item 2
	 */
	I2,
	/**
	 * Solicitado
	 */
	SO,
	/**
	 * Realizado
	 */
	RE;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I1:
			return "Item 1";
		case I2:
			return "Item 2";	
		case SO:
			return "Solicitado";
		case RE:
			return "Realizado";
		default:
			return "";
		}
	}
	
}
