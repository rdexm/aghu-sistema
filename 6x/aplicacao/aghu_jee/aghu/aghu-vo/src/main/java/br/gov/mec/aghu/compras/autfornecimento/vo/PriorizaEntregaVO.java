package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;

public class PriorizaEntregaVO implements Serializable{

	private static final long serialVersionUID = 7996316869124604079L;

	private Integer rowId;
	private Integer numeroAf;	
	private Short numeroItemAf;
	private Integer seqProgEntrega;
	private Integer parcelaProgEntrega;
	private Integer seqRecebimento;
	private Integer itemRecebimento;
	
	private Long seqItemRecbXProgrEntrega;
	private Long seqSolicitacaoProgramacaoEntrega;
	
	private Integer numeroParcela;
	private Integer numeroAfp;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private ScoSolicitacaoServico solicitacaoServico;
	private Date previsaoEntrega;
	private Integer saldoSolicitacaoCompra;
	private Integer qtdeRecebidaSolicitacaoCompra;
	private BigDecimal saldoSolicitacaoServico;
	private BigDecimal valorRecebidoSolicitacaoServico;
	private Short prioridade;
	

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNumeroItemAf() {
		return numeroItemAf;
	}

	public void setNumeroItemAf(Short numeroItemAf) {
		this.numeroItemAf = numeroItemAf;
	}

	public Integer getSeqProgEntrega() {
		return seqProgEntrega;
	}

	public void setSeqProgEntrega(Integer seqProgEntrega) {
		this.seqProgEntrega = seqProgEntrega;
	}

	public Integer getParcelaProgEntrega() {
		return parcelaProgEntrega;
	}

	public void setParcelaProgEntrega(Integer parcelaProgEntrega) {
		this.parcelaProgEntrega = parcelaProgEntrega;
	}

	public Integer getSeqRecebimento() {
		return seqRecebimento;
	}

	public void setSeqRecebimento(Integer seqRecebimento) {
		this.seqRecebimento = seqRecebimento;
	}

	public Integer getItemRecebimento() {
		return itemRecebimento;
	}

	public void setItemRecebimento(Integer itemRecebimento) {
		this.itemRecebimento = itemRecebimento;
	}

	public Integer getNumeroParcela() {
		return numeroParcela;
	}

	public void setNumeroParcela(Integer numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	public Integer getNumeroAfp() {
		return numeroAfp;
	}

	public void setNumeroAfp(Integer numeroAfp) {
		this.numeroAfp = numeroAfp;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}

	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	public Date getPrevisaoEntrega() {
		return previsaoEntrega;
	}

	public void setPrevisaoEntrega(Date previsaoEntrega) {
		this.previsaoEntrega = previsaoEntrega;
	}

	public Integer getSaldoSolicitacaoCompra() {
		return saldoSolicitacaoCompra;
	}

	public void setSaldoSolicitacaoCompra(Integer saldoSolicitacaoCompra) {
		this.saldoSolicitacaoCompra = saldoSolicitacaoCompra;
	}

	public Integer getQtdeRecebidaSolicitacaoCompra() {
		return qtdeRecebidaSolicitacaoCompra;
	}

	public void setQtdeRecebidaSolicitacaoCompra(
			Integer qtdeRecebidaSolicitacaoCompra) {
		this.qtdeRecebidaSolicitacaoCompra = qtdeRecebidaSolicitacaoCompra;
	}

	public BigDecimal getSaldoSolicitacaoServico() {
		return saldoSolicitacaoServico;
	}

	public void setSaldoSolicitacaoServico(BigDecimal saldoSolicitacaoServico) {
		this.saldoSolicitacaoServico = saldoSolicitacaoServico;
	}

	public BigDecimal getValorRecebidoSolicitacaoServico() {
		return valorRecebidoSolicitacaoServico;
	}

	public void setValorRecebidoSolicitacaoServico(
			BigDecimal valorRecebidoSolicitacaoServico) {
		this.valorRecebidoSolicitacaoServico = valorRecebidoSolicitacaoServico;
	}

	public Short getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Short prioridade) {
		this.prioridade = prioridade;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rowId == null) ? 0 : rowId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		PriorizaEntregaVO other = (PriorizaEntregaVO) obj;
		if (rowId == null) {
			if (other.rowId != null){
				return false;
			}
		} else if (!rowId.equals(other.rowId)){
			return false;
		}
		
		return true;
	}

	public Long getSeqItemRecbXProgrEntrega() {
		return seqItemRecbXProgrEntrega;
	}

	public void setSeqItemRecbXProgrEntrega(Long seqItemRecbXProgrEntrega) {
		this.seqItemRecbXProgrEntrega = seqItemRecbXProgrEntrega;
	}

	public Long getSeqSolicitacaoProgramacaoEntrega() {
		return seqSolicitacaoProgramacaoEntrega;
	}

	public void setSeqSolicitacaoProgramacaoEntrega(
			Long seqSolicitacaoProgramacaoEntrega) {
		this.seqSolicitacaoProgramacaoEntrega = seqSolicitacaoProgramacaoEntrega;
	}


}
