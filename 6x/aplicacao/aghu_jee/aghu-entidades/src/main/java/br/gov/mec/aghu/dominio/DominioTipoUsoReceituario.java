package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoUsoReceituario implements Dominio {

	/**
	 * Uso Interno
	 */
	S,
	
	/**
	 * Uso Externo
	 */
	N;
	
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {

		switch (this) {
		case S:
			return "Interno";
		case N:
			return "Externo";
		default:
			return "";
		}

	}

}
