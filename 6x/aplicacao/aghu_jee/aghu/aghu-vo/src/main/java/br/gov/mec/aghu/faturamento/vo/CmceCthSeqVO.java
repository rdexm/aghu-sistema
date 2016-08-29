package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

public class CmceCthSeqVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 355239158923905625L;
	
	private Integer conCodCentral = null;
	private Integer cthSeq = null;
	
	public CmceCthSeqVO(){
		super();
	}

	public Integer getConCodCentral() {
		return conCodCentral;
	}

	public void setConCodCentral(Integer conCodCentral) {
		this.conCodCentral = conCodCentral;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

}
