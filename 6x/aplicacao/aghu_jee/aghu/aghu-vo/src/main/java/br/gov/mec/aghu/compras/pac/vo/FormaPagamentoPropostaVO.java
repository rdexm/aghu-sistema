package br.gov.mec.aghu.compras.pac.vo;

import java.math.BigDecimal;

public class FormaPagamentoPropostaVO {
	private String condicao;
	private BigDecimal acrescimo;
	private BigDecimal desconto;
	private Integer prazo;
	private BigDecimal percentual;
	private BigDecimal valor;
	
	
	public String getCondicao() {
		return condicao;
	}
	
	public void setCondicao(String condicao) {
		this.condicao = condicao;
	}
	
	public BigDecimal getAcrescimo() {
		return acrescimo;
	}
	
	public void setAcrescimo(BigDecimal acrescimo) {
		this.acrescimo = acrescimo;
	}
	
	public BigDecimal getDesconto() {
		return desconto;
	}
	
	public void setDesconto(BigDecimal desconto) {
		this.desconto = desconto;
	}
	
	public Integer getPrazo() {
		return prazo;
	}
	
	public void setPrazo(Integer prazo) {
		this.prazo = prazo;
	}
	
	public BigDecimal getPercentual() {
		return percentual;
	}

	public void setPercentual(BigDecimal percentual) {
		this.percentual = percentual;
	}

	public BigDecimal getValor() {
		return valor;
	}
	
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
}
