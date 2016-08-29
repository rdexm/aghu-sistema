package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioSituacaoAtendimento implements Dominio {
	PACIENTE_AGENDADO(9), 
	PACIENTE_ATENDIDO(10), 
	AGUARDANDO_ATENDIMENTO(20), 
	PROFISSIONAL_FALTOU(30), 
	PACIENTE_FALTOU(40), 
	PACIENTE_DESISTIU_CONS(50), 
	EM_ATENDIMENTO(60);
	
	private Integer codigo; 
	
	DominioSituacaoAtendimento(Integer codigo) {
		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {
		return codigo;
	}

	@Override
	public String getDescricao() {
		return null;
	}

}
