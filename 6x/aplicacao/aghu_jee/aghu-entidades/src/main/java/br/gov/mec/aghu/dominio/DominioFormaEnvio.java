package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a forma de envio, se e correio ou fax.
 * 
 * @author gfmenezes
 * 
 */
public enum DominioFormaEnvio implements Dominio {
	/**
	 * Correio
	 */
	C,

	/**
	 * Fax
	 */
	F;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Correio";
		case F:
			return "Fax";
		default:
			return "";
		}
	}
	
	
	

}
