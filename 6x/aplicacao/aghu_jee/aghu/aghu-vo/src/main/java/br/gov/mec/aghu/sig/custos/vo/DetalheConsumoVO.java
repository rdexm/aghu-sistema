package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class DetalheConsumoVO implements Serializable {

	private static final long serialVersionUID = -7944336748494401122L;

	private Integer phiSeq;
	private String phiDescricao;
	private Integer matCodigo;
	private BigDecimal quantidade;
	private BigDecimal valor;

	
	public Integer getPhiSeq() {
		return phiSeq;
	}
	
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	
	public String getPhiDescricao() {
		return phiDescricao;
	}
	
	public void setPhiDescricao(String phiDescricao) {
		this.phiDescricao = phiDescricao;
	}
	
	public Integer getMatCodigo() {
		return matCodigo;
	}
	
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	
	public BigDecimal getQuantidade() {
		return quantidade;
	}
	
	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
}
