package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum StatusPacienteAgendado implements Dominio {
	AGENDADO,
	AGUARDANDO,
	EM_ATENDIMENTO,
	ATENDIDO,
	AUSENTE;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case AGENDADO:
			return "Agendado";
		case AGUARDANDO:
			return "Aguardando";
		case EM_ATENDIMENTO:
			return "Em Atendimento";
		case ATENDIDO:
			return "Atendido";
		case AUSENTE:
			return "Ausente";
		default:
			return "";
		}
	}
}