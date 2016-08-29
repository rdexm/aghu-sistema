package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOrdenacaoProtocoloEntregaNotasConsumo implements Dominio {
	/*
	 * Agenda
	 */
	AGENDA,
	/*
	 * Nome
	 */
	NOME;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case AGENDA:
			return "Agenda";
		case NOME:
			return "Nome";
		default:
			return "";
		}	
	}
}
