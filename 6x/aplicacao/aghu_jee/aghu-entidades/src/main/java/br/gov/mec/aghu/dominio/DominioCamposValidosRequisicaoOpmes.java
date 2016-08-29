package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;




public enum DominioCamposValidosRequisicaoOpmes  implements Dominio {
	
	ID("seq", "Sequencial"),
	AGENDA("agendas", "Agendas"),
	CIRURGIA("cirurgia", "Cirurgia"),
	SITUACAO("situacao", "Situação"),
	OBS_OPME("observacaoOpme", "Obversação Opme"),
	JUST_REQ_OPME("justificativaRequisicaoOpme", "Justificativa Requisição Opme"), 
	JUST_CONS_OPME("justificativaConsumoOpme", "Justificativa Consumo Opme"),
	CRIADO_EM("criadoEm", "Criado Em"),
	MODIFICADO_EM("modificadoEm", "Modificado Em"),
	RAP_SERVIDORES("rapServidores", "Rap Servidores"),
	RAP_SERVIDORES_MODIFICACAO("rapServidoresModificacao", "Rap Servidor Modificação"),
	IND_COMPATIVEL("indCompativel", "Indicador Compatível"),
	IND_AUTORIZADO("indAutorizado", "Indicador Autorizado"),
	IND_CONS_APROVACAO("indConsAprovacao", "Indicador Consumo Aprovação"),
	DATA_FIM("dataFim", "Data Fim"),
	FLUXO("fluxo", "Fluxo"),
	;
	
	
	private String valor;
	private String descricao;
	
	private DominioCamposValidosRequisicaoOpmes(String valor, String descricao){
		this.valor = valor;
		this.descricao = descricao;
	}
	
	@Override
	public String toString(){
		return this.valor;
	}
	

	@Override
	public int getCodigo() {
		// TODO Auto-generated method stub
		return ordinal();
	}

	@Override
	public String getDescricao() {
		return this.descricao;
	}
}
