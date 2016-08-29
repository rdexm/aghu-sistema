package br.gov.mec.aghu.compras.contaspagar.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PagamentosRealizadosPeriodoPdfSubDocsVO implements BaseBean {

	private static final long serialVersionUID = 7987730518864130796L;
	
	/*-*-*-* Variaveis *-*-*-*/
	private String doc;
	private Integer numero;
	private Integer titulo;
	private Integer nr;
	private Double valorTitulo;
	private Double desconto;
	private Double tributos;
	private Double vlrDft;
	private Integer licitacao;
	private Double valorPagamento;

	/*-*-*-* Construtores *-*-*-*/
	public PagamentosRealizadosPeriodoPdfSubDocsVO() {
		super();
	}

	/*-*-*-* Getters e Setters *-*-*-*/
	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getTitulo() {
		return titulo;
	}

	public void setTitulo(Integer titulo) {
		this.titulo = titulo;
	}

	public Integer getNr() {
		return nr;
	}

	public void setNr(Integer nr) {
		this.nr = nr;
	}

	public Double getValorTitulo() {
		return valorTitulo;
	}

	public void setValorTitulo(Double valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public Double getDesconto() {
		return desconto;
	}

	public void setDesconto(Double desconto) {
		this.desconto = desconto;
	}

	public Double getTributos() {
		return tributos;
	}

	public void setTributos(Double tributos) {
		this.tributos = tributos;
	}
	
	public Double getVlrDft() {
		return vlrDft;
	}

	public void setVlrDft(Double vlrDft) {
		this.vlrDft = vlrDft;
	}

	public Integer getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(Integer licitacao) {
		this.licitacao = licitacao;
	}

	public Double getValorPagamento() {
		return valorPagamento;
	}

	public void setValorPagamento(Double valorPagamento) {
		this.valorPagamento = valorPagamento;
	}
}
