package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ItensLicitacaoCadastradaVO implements BaseBean{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1213484761549330580L;
	
	private String item;
	private String mercadoria;
	private String produto;
	private String quantidade;
	private String descricao;
	private String ocorrencia;
	
	
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getMercadoria() {
		return mercadoria;
	}
	public void setMercadoria(String mercadoria) {
		this.mercadoria = mercadoria;
	}
	public String getProduto() {
		return produto;
	}
	public void setProduto(String produto) {
		this.produto = produto;
	}
	public String getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getOcorrencia() {
		return ocorrencia;
	}
	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}
}
