package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;



public enum DominioCamposValidosItensRequisicaoOpmes  implements Dominio {
	
	SEQ("seq", "Sequencial"),
	REQUISICAO_OPMES("requisicaoOpmes", "Requisição Opmes"),
	FAT_ITENS_PROCED_HOSP("fatItensProcedHospitalar", "Fat Itens Proced Hospitalar"),
	REQUERIDO("requerido", "Requirido"),
	IND_COMPATIVEL("indCompativel", "Indicador Incompatível"),
	IND_AUTORIZADO("indAutorizado", "Indicador Autorizado"),
	IND_CONSUMIDO("indConsumido", "Indicador Consumido"),
	QTD_AUTORIZADA_SUS("quantidadeAutorizadaSus", "Quantidade Autorizadas Sus"),
	VLR_UNITARIO("valorUnitario", "Valor Unitário"),
	QTD_SOLICITADA("quantidadeSolicitada", "Quantidade Solicitada"),
	QTD_AUTO_HOSPITAL("quantidadeAutorizadaHospital", "Quantidade Autorizada Hospital"),
	DESC_INCOMPATIVEL("descricaoImcompativel", "Descrição Incompativel"),
	SOLIC_NOVO_MATERIAL("solicitacaoNovoMaterial", "Solicitação Novo Material"),
	ESPEC_NOVO_MATERIAL("especificacaoNovoMaterial", "Especificação Novo Material"),
	QTD_CONSUMIDA("quantidadeConsumida", "Quantidade Consumida"),
	ANEXO_ORCAMENTO("anexoOrcamento", "Anexo Orçamento"),
	CRIADO_EM("criadoEm", "Criado Em"),
	MODIFICADO_EM("modificadoEm", "Modificado Em"),
	RAP_SERVIDORES("rapServidores", "Rap Servidores"),
	RAP_SERVIDORES_MODIFICACAO("rapServidoresModificacao", "Rap Servidores Modificação"),	
	VERSION("version", "Version"),;
	
	
	private String valor;
	private String descricao;
	
	private DominioCamposValidosItensRequisicaoOpmes(String valor, String descricao){
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
