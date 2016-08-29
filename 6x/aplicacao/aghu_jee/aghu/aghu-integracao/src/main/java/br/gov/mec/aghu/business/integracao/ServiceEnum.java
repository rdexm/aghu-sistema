package br.gov.mec.aghu.business.integracao;

/**
 * Enum que lista os serviços consumidos pelo AGHU.
 * 
 * @author aghu
 * 
 */
@SuppressWarnings("ucd")
public enum ServiceEnum {

	CONSULTA_EQUIPAMENTO("servico_patrimonio_consulta_equipamento"), 
	BUSCA_EQUIPAMENTOS_DEPRECIADOS_MES("servico_patrimonio_busca_equipamentos_depreciados_mes"),
	BUSCA_FOLHA_PAGAMENTO_MENSAL("servico_folha_pagamento_carga_mensal_colaborador"),
	CONSULTA_EQUIPAMENTO_BY_DESCRICAO("servico_patrimonio_busca_equipamento_by_descricao"),
	CONSULTA_EQUIPAMENTO_BY_ID("servico_patrimonio_busca_equipamento_by_id"),
	BUSCA_PEDIDOS_PAPELARIA("servico_papelaria_busca_pedido");

	private String nome;

	ServiceEnum(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return this.nome;
	}

}
