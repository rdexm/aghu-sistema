package br.gov.mec.aghu.internacao.pesquisa.vo;

import java.io.Serializable;


public class DetalhesPesquisarSituacaoLeitosVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -793309197156253587L;

	private String situacao;
	
	private Integer quantidade;
	
	public DetalhesPesquisarSituacaoLeitosVO() {
	}
	
	public DetalhesPesquisarSituacaoLeitosVO(String situacao, Integer quantidade) {
		this.situacao = situacao;
		this.quantidade = quantidade;
	}
	
	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
}
