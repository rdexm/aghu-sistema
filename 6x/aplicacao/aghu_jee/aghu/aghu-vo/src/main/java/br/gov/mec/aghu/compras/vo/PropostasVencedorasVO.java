package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioMotivoCancelamentoComissaoLicitacao;

public class PropostasVencedorasVO implements Serializable {
	
	private static final long serialVersionUID = -7468004122874334159L;
	
	private Short item;
	private DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento;
	private String fornecedor; 
	private String condicao;
	private Short parcela;
	
	public Short getItem() {
		return item;
	}
	public void setItem(Short item) {
		this.item = item;
	}
	public DominioMotivoCancelamentoComissaoLicitacao getMotivoCancelamento() {
		return motivoCancelamento;
	}
	public void setMotivoCancelamento(DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}
	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}
	public String getFornecedor() {
		return fornecedor;
	}
	public void setCondicao(String condicao) {
		this.condicao = condicao;
	}
	public String getCondicao() {
		return condicao;
	}
	public void setParcela(Short parcela) {
		this.parcela = parcela;
	}
	public Short getParcela() {
		return parcela;
	}

}
