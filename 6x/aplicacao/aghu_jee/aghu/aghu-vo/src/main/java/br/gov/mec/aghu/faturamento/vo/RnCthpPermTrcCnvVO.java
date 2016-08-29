package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class RnCthpPermTrcCnvVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7315823174786376708L;

	private Integer phiSeq;
	
	private Boolean ok;

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Boolean getOk() {
		return ok;
	}

	public void setOk(Boolean ok) {
		this.ok = ok;
	}
	 
}
