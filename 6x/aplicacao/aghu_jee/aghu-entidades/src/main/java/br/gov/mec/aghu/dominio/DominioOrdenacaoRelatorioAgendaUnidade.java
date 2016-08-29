package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrdenacaoRelatorioAgendaUnidade implements Dominio {
	/*
	 * Ordenação por horário
	 */
	HORARIO,
	/*
	 * Ordenação por unidade
	 */
	UNIDADE;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case HORARIO:
			return "Horário";
		case UNIDADE:
			return "Unidade";
		default:
			return "";
		}	
	}
}
