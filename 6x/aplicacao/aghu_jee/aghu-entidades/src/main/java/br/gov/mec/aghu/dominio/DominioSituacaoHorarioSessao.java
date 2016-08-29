package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoHorarioSessao implements Dominio {
	
	/**
	 * Agendado 
	 */
	A,
	
	/**
	 * Cancelado 
	 */
	C,
	
	/**
	 * N
	 */
	N,
	
	/**
	 * Reservado 
	 */
	R,
	
	/**
	 * E
	 */
	E,
	
	/**
	 * Marcado
	 */
	M
	
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case A: return "Agendado";
			case C: return "Cancelado";
			case N: return "N";
			case R: return "Reservado";
			case E: return "Extra";
			case M: return "Marcado";
			default: return "";
		}
	}
}