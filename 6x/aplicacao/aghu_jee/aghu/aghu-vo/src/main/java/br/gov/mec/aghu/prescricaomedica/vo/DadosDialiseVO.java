package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

public class DadosDialiseVO implements Serializable {
	
	private static final long serialVersionUID = -4696334192110966978L;

	private Integer atdSeq;
	private Integer seqTratamento;
	
	public DadosDialiseVO() {
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getSeqTratamento() {
		return seqTratamento;
	}

	public void setSeqTratamento(Integer seqTratamento) {
		this.seqTratamento = seqTratamento;
	}
}
