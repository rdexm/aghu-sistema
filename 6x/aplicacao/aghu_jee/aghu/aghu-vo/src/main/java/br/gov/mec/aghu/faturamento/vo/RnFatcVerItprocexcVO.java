package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

public class RnFatcVerItprocexcVO implements Serializable {
	private static final long serialVersionUID = 6568315087680207966L;
	
	private Short phoSeq;	
	private Short qtdItem;
	private Integer seq;
	
	
	public RnFatcVerItprocexcVO() {

	}
	
	public RnFatcVerItprocexcVO(Short phoSeq, Short qtdItem, Integer seq) {
		this.phoSeq = phoSeq;
		this.qtdItem = qtdItem;
		this.seq = seq;
	}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public Short getQtdItem() {
		return qtdItem;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public void setQtdItem(Short qtdItem) {
		this.qtdItem = qtdItem;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	
}
