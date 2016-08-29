package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class CursorVaprVO implements Serializable {
		
	private static final long serialVersionUID = 7691266026246448446L;

	private Short iphPhoSeq;
	
	private Integer iphSeq;

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}
	
	
	
}