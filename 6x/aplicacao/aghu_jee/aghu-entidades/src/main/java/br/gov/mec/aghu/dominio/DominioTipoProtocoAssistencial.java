package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoProtocoAssistencial implements Dominio {
	
	/**
	 * 
	 */
	Q, 
	/**
	 * 
	 */
	A,
	/**
	 * 
	 */
	D,;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case Q:
			return "";
		case A:
			return "";
		case D:
			return "";
		default:
			return "";
		}
	}
	
}
