package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoSSM implements Dominio {
	
	/**
	 * Realizado
	 */
	R, 
	/**
	 * Solicitado
	 */
	S;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {	
		switch (this) {
		case R:
			return "Realizado";
		case S:
			return "Solicitado";
		default:
			return "";
		}
	}

}
