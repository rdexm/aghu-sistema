package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Situação horário da entidade AelHorarioExameDisp.
 * 
 * 
 */
public enum DominioSituacaoHorario implements Dominio {

	/**
	 * Bloqueado
	 */
	B,
	
	/**
	 * Executado
	 */	
	E,
	
	/**
	 * Gerado
	 */	
	G,
	
	/**
	 * Liberado
	 */	
	L,
	
	/**
	 * Marcado
	 */	
	M;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case B:
			return "Bloqueado";
		case E:
			return "Executado";
		case G: 
			return "Gerado";
		case L: 
			return "Liberado";
		case M: 
			return "Marcado";			
		default:
			return "";
		}
	}
	
}
