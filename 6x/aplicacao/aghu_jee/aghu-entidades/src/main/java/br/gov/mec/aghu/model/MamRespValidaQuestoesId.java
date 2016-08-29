package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Embeddable
public class MamRespValidaQuestoesId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4051828747109736307L;
	
	private Integer qusQutSeq;
	private Short qusSeqp;
	private Integer seqp;
	
	public MamRespValidaQuestoesId() {
	}
	
	@Column(name = "QUS_QUT_SEQ", nullable = false, precision = 6, scale = 0)
	public Integer getQusQutSeq() {
		return qusQutSeq;
	}
	
	public void setQusQutSeq(Integer qusQutSeq) {
		this.qusQutSeq = qusQutSeq;
	}

	@Column(name = "QUS_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getQusSeqp() {
		return qusSeqp;
	}

	public void setQusSeqp(Short qusSeqp) {
		this.qusSeqp = qusSeqp;
	}

	@Column(name = "SEQP", nullable = false, precision = 5, scale = 0)
	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getQusQutSeq());
        umHashCodeBuilder.append(this.getQusSeqp());
        umHashCodeBuilder.append(this.getSeqp());
        return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MamRespValidaQuestoesId other = (MamRespValidaQuestoesId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getQusQutSeq(), other.getQusQutSeq());
        umEqualsBuilder.append(this.getQusSeqp(), other.getQusSeqp());
        umEqualsBuilder.append(this.getSeqp(), other.getSeqp());
        return umEqualsBuilder.isEquals();
	}
}
