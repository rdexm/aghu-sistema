package br.gov.mec.aghu.faturamento.vo;

public class ParCthSeqSsmVO {

	private Integer cthSeq = null;
	private String ssmStr = null;

	public ParCthSeqSsmVO() {

		super();
	}

	public Integer getCthSeq() {

		return this.cthSeq;
	}

	public void setCthSeq(final Integer cthSeq) {

		this.cthSeq = cthSeq;
	}

	public String getSsmStr() {

		return this.ssmStr;
	}

	public void setSsmStr(final String ssmStr) {

		this.ssmStr = ssmStr;
	}

	@Override
	public String toString() {

		return "[c: " + this.cthSeq + " s: " + this.ssmStr + "]";
	}
}
