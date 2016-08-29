package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioTipoOcorrenciaFichaFarmaco implements Dominio {
	/**
	 * 
	 */
	I,
	/**
	 * 
	 */
	F,
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
		case I:
			return "";
		case F:
			return "";
		case S:
			return "";
		default:
			return "";
		}
	}


}
