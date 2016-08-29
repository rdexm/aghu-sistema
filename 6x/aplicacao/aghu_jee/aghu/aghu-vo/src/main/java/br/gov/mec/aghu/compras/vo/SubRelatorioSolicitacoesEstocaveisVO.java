package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class SubRelatorioSolicitacoesEstocaveisVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -555747254085342427L;

	private Integer numeroSolicitacao;
	private Date dataSolicitacao;
	private Integer quantidadeSolicitada;
	private Integer numeroPAC;
	private String numeroAF;
	private Integer item;
	private Date dataPrev;
	private Integer quantidadeAF;
	private Integer quantidadeRecebida;
	private String st;
	private String fornecedor;

	public SubRelatorioSolicitacoesEstocaveisVO(Integer numeroSolicitacao,
			Date dataSolicitacao, Integer quantidadeSolicitada,
			Integer numeroPAC, String numeroAF, Integer item, Date dataPrev,
			Integer quantidadeAF, Integer quantidadeRecebida, String st,
			String fornecedor) {
		this.numeroSolicitacao = numeroSolicitacao;
		this.dataSolicitacao = dataSolicitacao;
		this.quantidadeSolicitada = quantidadeSolicitada;
		this.numeroPAC = numeroPAC;
		this.numeroAF = numeroAF;
		this.item = item;
		this.dataPrev = dataPrev;
		this.quantidadeAF = quantidadeAF;
		this.quantidadeRecebida = quantidadeRecebida;
		this.st = st;
		this.fornecedor = fornecedor;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public Integer getQuantidadeSolicitada() {
		return quantidadeSolicitada;
	}

	public void setQuantidadeSolicitada(Integer quantidadeSolicitada) {
		this.quantidadeSolicitada = quantidadeSolicitada;
	}

	public Integer getNumeroPAC() {
		return numeroPAC;
	}

	public void setNumeroPAC(Integer numeroPAC) {
		this.numeroPAC = numeroPAC;
	}

	public String getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(String numeroAF) {
		this.numeroAF = numeroAF;
	}

	public Integer getItem() {
		return item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	public Date getDataPrev() {
		return dataPrev;
	}

	public void setDataPrev(Date dataPrev) {
		this.dataPrev = dataPrev;
	}

	public Integer getQuantidadeAF() {
		return quantidadeAF;
	}

	public void setQuantidadeAF(Integer quantidadeAF) {
		this.quantidadeAF = quantidadeAF;
	}

	public Integer getQuantidadeRecebida() {
		return quantidadeRecebida;
	}

	public void setQuantidadeRecebida(Integer quantidadeRecebida) {
		this.quantidadeRecebida = quantidadeRecebida;
	}

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((numeroSolicitacao == null) ? 0 : numeroSolicitacao
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		SubRelatorioSolicitacoesEstocaveisVO other = (SubRelatorioSolicitacoesEstocaveisVO) obj;
		if (other != null) {
			return new EqualsBuilder().append(this.numeroSolicitacao,
					other.numeroSolicitacao).isEquals();
		}

		return false;
	}

}
