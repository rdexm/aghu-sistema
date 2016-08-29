package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoCirurgia implements Dominio {
	/**
	 * Agendada.
	 */
	AGND, 
	/**
	 * Cancelada.
	 */
	CANC, 
	/**
	 * Chamada.
	 */
	CHAM, 
	/**
	 * Preparada.
	 */
	PREP, 
	/**
	 * Realizada.
	 */
	RZDA, 
	/**
	 * Transferida.
	 */
	TRAN;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CANC:
			return "Cirurgia/PDT Cancelada";
		case RZDA:
			return "Cirurgia/PDT Realizada";
		case PREP:
			return "Paciente em Preparo";
		case CHAM:
			return "Paciente Chamado";
		case AGND:
			return "Cirurgia/PDT Agendada";
		case TRAN:
			return "Paciente em Transoperatório";				
		default:
			return "";
		}
	}
	
	public String getEndImagem(){
		switch (this) {
		case CANC:
			return "silk-close-window";
		case RZDA:
			return "silk-checked";
		case PREP:
			return "silk-status";
		case CHAM:
			return "silk-fone";	
		case AGND:
			return "silk-agendado";
		case TRAN:
			return "silk-paciente";	
		default:
			return "";
		}
	}
}
