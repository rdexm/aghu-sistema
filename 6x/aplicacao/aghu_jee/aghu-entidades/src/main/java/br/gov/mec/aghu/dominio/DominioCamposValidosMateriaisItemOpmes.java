package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;



public enum DominioCamposValidosMateriaisItemOpmes  implements Dominio {
	
	SEQ("seq", "Sequencial"),
	ITEM_REQUISICAO_OPMES("itensRequisicaoOpmes", "Item Requisição Opmes"),
	PROCED_HOSP_INTERNO("procedHospInternos", "Procedimento Hospital Interno"),
	MATERIAL("material", "Material"),
	SITUACAO("situacao", "Situação"),
	QTD_SOLICITADA("quantidadeSolicitada", "Quantidade Solicitada"),
	QTD_CONSUMIDA("quantidadeConsumida", "Quantidade Consumida"),
	CRIADO_EM("criadoEm", "Criado Em"),
	MODIFICADO_EM("modificadoEm", "Modificado Em"),
	RAP_SERVIDORES("rapServidores", "Rap Servidores"),
	RAP_SERVIDORES_MODIFICACAO("rapServidoresModificacao", "Rap Servidores Modificação"),	
	VERSION("version", "Version");

	private String valor;
	private String descricao;
	
	private DominioCamposValidosMateriaisItemOpmes(String valor, String descricao){
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
