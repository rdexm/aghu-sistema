package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoCartaColeta implements Dominio {

	/**
	 * A Emitir	
	 */
	AE,
	/**
	 * Emitida
	 */
	EM,
	/**
	 * Retornada
	 */
	RE;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AE:
			return "A Emitir";
		case EM:
			return "Emitida";
		case RE:
			return "Retornada";
		default:
			return "";
		}
	}
}
