package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAvaliacaoInterconsulta  implements Dominio {
	

	/**
	 * Não avaliado.
	 */
	N,
	/**
	 * Sem Indicação para Agenda.
	 */
	S,
	/**
	 * Solicitação de mais Informações.
	 */
	I,	
	/**
	 * Consultoria sem Agendamento.
	 */
	C,	
	/**
	 * Agendamento pelo consultor.
	 */
	M,	
	/**
	 * Agendamento pela secretária.
	 */
	L,
	/**
	 * On-line.
	 */
	O;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case N:
			return "Não avaliado";
		case S:
			return "Sem Indicação para Agenda";
		case I:
			return "Solicitação de mais Informações";
		case C:
			return "Consultoria sem Agendamento";
		case M:
			return "Agendamento pelo consultor";
		case L:
			return "Agendamento pela secretária";
		case O:
			return "On-line";
		default:
			return "";
		}
	}
}
