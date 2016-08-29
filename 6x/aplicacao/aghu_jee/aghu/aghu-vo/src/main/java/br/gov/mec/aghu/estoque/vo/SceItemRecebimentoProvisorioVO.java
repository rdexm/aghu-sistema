package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

public class SceItemRecebimentoProvisorioVO implements Serializable {

	private static final long serialVersionUID = 8919020456375251002L;
	
	private Integer peaIafAfnNumero;
	private Integer peaIafNumero;
	private Integer irpNrpSeq;
	private Integer irpNroItem;
	private Integer idfQuantidade;
	
	public SceItemRecebimentoProvisorioVO() {

	}

	public Integer getPeaIafAfnNumero() {
		return peaIafAfnNumero;
	}

	public void setPeaIafAfnNumero(Integer peaIafAfnNumero) {
		this.peaIafAfnNumero = peaIafAfnNumero;
	}

	public Integer getPeaIafNumero() {
		return peaIafNumero;
	}

	public void setPeaIafNumero(Integer peaIafNumero) {
		this.peaIafNumero = peaIafNumero;
	}

	public Integer getIrpNrpSeq() {
		return irpNrpSeq;
	}

	public void setIrpNrpSeq(Integer irpNrpSeq) {
		this.irpNrpSeq = irpNrpSeq;
	}

	public Integer getIrpNroItem() {
		return irpNroItem;
	}

	public void setIrpNroItem(Integer irpNroItem) {
		this.irpNroItem = irpNroItem;
	}

	public Integer getIdfQuantidade() {
		return idfQuantidade;
	}

	public void setIdfQuantidade(Integer idfQuantidade) {
		this.idfQuantidade = idfQuantidade;
	}


}
