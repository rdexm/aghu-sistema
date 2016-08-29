package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoAelExameAp implements Dominio {
	
	/**
	 * Anulado
	 */
	A,
	
	/**
	 * Cancelado
	 */
	C,
	
	/**
	 * Inclusão
	 */
	I,
	
	/**
	 * Revisado
	 */
	R 
	
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case A: return "Anulado";
			case C: return "Cancelado";
			case I: return "Inclusão";
			case R: return "Revisado";
			default: return "";
		}
	}
}