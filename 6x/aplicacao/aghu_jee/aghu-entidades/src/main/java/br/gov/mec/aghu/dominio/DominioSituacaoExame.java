package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoExame implements Dominio {

	/**
	 * 
	 */
	A, 
	/**
	 * 
	 */
	R;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A:
			return "";
		case R:
			return "";
		default:
			return "";
		}
	}
	
}
