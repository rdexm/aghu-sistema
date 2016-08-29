package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author lsamberg
 *
 */
public enum DominioPorteFichaFinal implements Dominio {

	/**
	 * Mínimo
	 */
	MI, 
	/**
	 * Pequeno
	 */
	PE,
	/**
	 * Médio
	 */
	ME,
	/**
	 * Grande
	 */
	GR;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case MI:
			return "Mínimo";
		case PE:
			return "Pequeno";
		case ME:
			return "Médio";
		case GR:
			return "Grande";
		default:
			return "";
		}
	}
	
}
