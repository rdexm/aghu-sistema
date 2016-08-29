package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

public class MsgErroCthSeqVO implements Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 375915175277145354L;
	
	Integer cthSeq = null;
	String msgErro = null;
	
	public MsgErroCthSeqVO(){
		super();
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public String getMsgErro() {
		return msgErro;
	}

	public void setMsgErro(String msgErro) {
		this.msgErro = msgErro;
	}
	
}
