package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;

public class InternacaoConvenioVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1392077394546141940L;
	private Integer intSeq;
	private Integer cppSeq;
	
	public Integer getCppSeq() {
		return cppSeq;
	}
	public void setCppSeq(Integer cppSeq) {
		this.cppSeq = cppSeq;
	}
	public Integer getIntSeq() {
		return intSeq;
	}
	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}
	
	public enum Fields {
		INT_SEQ ("intSeq"),
		CPP_SEQ ("cppSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
}
