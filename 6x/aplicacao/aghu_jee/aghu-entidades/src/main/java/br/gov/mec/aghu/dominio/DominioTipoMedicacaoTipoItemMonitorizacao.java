package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 */
public enum DominioTipoMedicacaoTipoItemMonitorizacao implements Dominio {
	/**
	 * 
	 */
	N,
	/**
	 * 
	 */
	C,
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
		case N:
			return "";
		case C:
			return "";
		case S:
			return "";
		default:
			return "";
		}
	}


}
