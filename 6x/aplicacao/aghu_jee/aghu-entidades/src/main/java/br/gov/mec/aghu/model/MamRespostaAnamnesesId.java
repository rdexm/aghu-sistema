package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MamRespostaAnamnesesId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 264420674418193252L;
	private Long anaSeq;
	private Integer qusQutSeq;
	private Short qusSeqp;
	private Short seqp;

	public MamRespostaAnamnesesId() {
	}

	public MamRespostaAnamnesesId(Long anaSeq, Integer qusQutSeq, Short qusSeqp,
			Short seqp) {
		this.anaSeq = anaSeq;
		this.qusQutSeq = qusQutSeq;
		this.qusSeqp = qusSeqp;
		this.seqp = seqp;
	}

	@Column(name = "ANA_SEQ", nullable = false, precision = 14, scale = 0)
	public Long getAnaSeq() {
		return this.anaSeq;
	}

	public void setAnaSeq(Long anaSeq) {
		this.anaSeq = anaSeq;
	}

	@Column(name = "QUS_QUT_SEQ", nullable = false, precision = 6, scale = 0)
	public Integer getQusQutSeq() {
		return this.qusQutSeq;
	}

	public void setQusQutSeq(Integer qusQutSeq) {
		this.qusQutSeq = qusQutSeq;
	}

	@Column(name = "QUS_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getQusSeqp() {
		return this.qusSeqp;
	}

	public void setQusSeqp(Short qusSeqp) {
		this.qusSeqp = qusSeqp;
	}

	@Column(name = "SEQP", nullable = false, precision = 3, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof MamRespostaAnamnesesId)) {
			return false;
		}
		MamRespostaAnamnesesId castOther = (MamRespostaAnamnesesId) other;

		return (this.getAnaSeq() == castOther.getAnaSeq())
				&& (this.getQusQutSeq() == castOther.getQusQutSeq())
				&& (this.getQusSeqp() == castOther.getQusSeqp())
				&& (this.getSeqp() == castOther.getSeqp());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getAnaSeq().intValue();
		result = 37 * result + this.getQusQutSeq();
		result = 37 * result + this.getQusSeqp();
		result = 37 * result + this.getSeqp();
		return result;
	}

}
