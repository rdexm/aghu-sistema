package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ScoSolicitacaoEntregaVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5429227404145455505L;
	
	private Integer numeroCompra;
	private Integer quantidade;
	private Integer quantidadeEntregue;
	private Integer saldoQuantidade;
	
	private Integer numeroServico;
	private Double valor;
	private Double valorEfetivado;
	private Double saldoValor;
	
	private Integer numeroCentroCustos;
	private String descricaoCentroCustos;
	
	
	public ScoSolicitacaoEntregaVO() {
		
	}

	public enum Fields {

		NUMERO_COMPRA("numeroCompra"),
		QUANTIDADE("quantidade"),
		QUANTIDADE_ENTREGUE("quantidadeEntregue"),
		SALDO_QUANTIDADE("saldoQuantidade"),
		NUMERO_SERVICO("numeroServico"),
		VALOR("valor"),
		DESCRICAO_CC("descricaoCentroCustos"),
		CODIGO_CC("numeroCentroCustos"),
		VALOR_EFETIVADO("valorEfetivado"),
		SALDO_VALOR("saldoValor");
				
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Integer getQuantidadeEntregue() {
		return quantidadeEntregue;
	}

	public void setQuantidadeEntregue(Integer quantidadeEntregue) {
		this.quantidadeEntregue = quantidadeEntregue;
	}

	public Integer getSaldoQuantidade() {
		return saldoQuantidade;
	}

	public void setSaldoQuantidade(Integer saldoQuantidade) {
		this.saldoQuantidade = saldoQuantidade;
	}

	public Integer getNumeroCompra() {
		return numeroCompra;
	}

	public void setNumeroCompra(Integer numeroCompra) {
		this.numeroCompra = numeroCompra;
	}

	public Integer getNumeroServico() {
		return numeroServico;
	}

	public void setNumeroServico(Integer numeroServico) {
		this.numeroServico = numeroServico;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Double getValorEfetivado() {
		return valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}

	public Double getSaldoValor() {
		return saldoValor;
	}

	public void setSaldoValor(Double saldoValor) {
		this.saldoValor = saldoValor;
	}

	public Integer getNumeroCentroCustos() {
		return numeroCentroCustos;
	}

	public void setNumeroCentroCustos(Integer numeroCentroCustos) {
		this.numeroCentroCustos = numeroCentroCustos;
	}

	public String getDescricaoCentroCustos() {
		return descricaoCentroCustos;
	}

	public void setDescricaoCentroCustos(String descricaoCentroCustos) {
		this.descricaoCentroCustos = descricaoCentroCustos;
	}

}
