package br.gov.mec.aghu.exames.patologia.vo;

import java.io.Serializable;

public class AelItemSolicitacaoExameLaudoUnicoVO implements Serializable {

	private static final long serialVersionUID = 3530530349061245472L;
	
	private Integer soeSeq;
	private Short seqp;
	
	public AelItemSolicitacaoExameLaudoUnicoVO() {} 
	
	public AelItemSolicitacaoExameLaudoUnicoVO(Integer soeSeq, Short seqp) {
		super();
		this.soeSeq = soeSeq;
		this.seqp = seqp;
	}
	
	public enum Fields {
		SOE_SEQ("soeSeq"),
		SEQP("seqp")
		;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
}
