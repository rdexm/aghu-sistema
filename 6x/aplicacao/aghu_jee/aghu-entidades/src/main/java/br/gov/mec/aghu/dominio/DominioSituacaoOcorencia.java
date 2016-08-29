package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoOcorencia implements Dominio {
	/**
	 * Solicitado
	 */
	S,

	/**
	 * Confirmado
	 */
	C,
	
	/**
	 * Negado
	 */
	N
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Solicitado";
		case C:
			return "Confirmado";
		case N:
			return "Negado";
		default:
			return "";
		}
	}

}
