package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.util.Date;

public class ConsultaRecebimentoMaterialServicoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 620053907719426687L;

	private Short itlNumero;
	private Integer itemAFNNumero;
	private Integer itemNumero;
	private Integer parcela;
	private Integer seqParcela;
	private Integer afp;
	private Date dtPrevEntrega;
	private Integer qtde;
	private Integer qtdeEntregue;
	private Double valorTotal;
	private Double valorEfetivado;
	
	public Short getItlNumero() {
		return itlNumero;
	}

	public void setItlNumero(Short itlNumero) {
		this.itlNumero = itlNumero;
	}

	public Integer getItemAFNNumero() {
		return itemAFNNumero;
	}

	public void setItemAFNNumero(Integer itemAFNNumero) {
		this.itemAFNNumero = itemAFNNumero;
	}

	public Integer getItemNumero() {
		return itemNumero;
	}

	public void setItemNumero(Integer itemNumero) {
		this.itemNumero = itemNumero;
	}

	public Integer getParcela() {
		return parcela;
	}
	
	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}
	
	public Integer getAfp() {
		return afp;
	}
	
	public void setAfp(Integer afp) {
		this.afp = afp;
	}
	
	public Date getDtPrevEntrega() {
		return dtPrevEntrega;
	}
	
	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}
	
	public Integer getQtde() {
		return qtde;
	}

	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}

	public Integer getQtdeEntregue() {
		return qtdeEntregue;
	}

	public void setQtdeEntregue(Integer qtdeEntregue) {
		this.qtdeEntregue = qtdeEntregue;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Double getValorEfetivado() {
		return valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}

	public Integer getSeqParcela() {
		return seqParcela;
	}

	public void setSeqParcela(Integer seqParcela) {
		this.seqParcela = seqParcela;
	}

}
