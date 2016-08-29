package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioCardFluxo implements Dominio {
	
	/**
	 * 
	 */
	I,
	/**
	 * 
	 */
	C;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "Intermitente";
		case C:
			return "Contínua";
		default:
			return "";
		}
	}

}
