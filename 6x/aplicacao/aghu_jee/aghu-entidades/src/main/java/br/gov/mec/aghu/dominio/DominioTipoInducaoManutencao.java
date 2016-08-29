package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author dfriedrich
 * 
 */
public enum DominioTipoInducaoManutencao implements Dominio {
	
	/**
	 * 
	 */
	I,
	/**
	 * 
	 */
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case I:
			return "";
		case M:
			return "";
		default:
			return "";
		}
	}

}
