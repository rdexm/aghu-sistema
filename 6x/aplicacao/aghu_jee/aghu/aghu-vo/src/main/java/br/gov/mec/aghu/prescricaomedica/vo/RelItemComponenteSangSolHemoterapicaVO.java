package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.List;

public class RelItemComponenteSangSolHemoterapicaVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8879960531034314825L;

	private String descricao; // F_DESCRICAO
	
	private String quantidade;
	private String aprazamento;
	private String quantidadeAplicacoes;
	
	private List<RelSolHemoterapicasJustificativaVO> listaJustificativas;
	
	private Integer atdSeqHemoterapia;
	private Integer seqHemoterapia;
	private Integer sequencia;
	
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}
	public String getAprazamento() {
		return aprazamento;
	}
	public void setAprazamento(String aprazamento) {
		this.aprazamento = aprazamento;
	}
	
	public String getQuantidadeAplicacoes() {
		return quantidadeAplicacoes;
	}
	public void setQuantidadeAplicacoes(String quantidadeAplicacoes) {
		this.quantidadeAplicacoes = quantidadeAplicacoes;
	}
	
	public Integer getAtdSeqHemoterapia() {
		return atdSeqHemoterapia;
	}
	public void setAtdSeqHemoterapia(Integer atdSeqHemoterapia) {
		this.atdSeqHemoterapia = atdSeqHemoterapia;
	}
	public Integer getSeqHemoterapia() {
		return seqHemoterapia;
	}
	public void setSeqHemoterapia(Integer seqHemoterapia) {
		this.seqHemoterapia = seqHemoterapia;
	}
	public Integer getSequencia() {
		return sequencia;
	}
	public void setSequencia(Integer sequencia) {
		this.sequencia = sequencia;
	}
	public void setListaJustificativas(
			List<RelSolHemoterapicasJustificativaVO> listaJustificativas) {
		this.listaJustificativas = listaJustificativas;
	}
	public List<RelSolHemoterapicasJustificativaVO> getListaJustificativas() {
		return listaJustificativas;
	}

}
