package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MamRespQuestAnamnesesId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2237134294274428333L;
	
	private Long reaAnaSeq;
	private Integer reaQusQutSeq;
	private Short reaQusSeqp;
	private Short reaSeqp;
	private Integer seqp;

	public MamRespQuestAnamnesesId() {
	}

	@Column(name = "REA_ANA_SEQ", nullable = false, precision = 14, scale = 0)
	public Long getReaAnaSeq() {
		return this.reaAnaSeq;
	}

	public void setReaAnaSeq(Long evoSeq) {
		this.reaAnaSeq = evoSeq;
	}

	@Column(name = "REA_QUS_QUT_SEQ", nullable = false, precision = 6, scale = 0)
	public Integer getReaQusQutSeq() {
		return this.reaQusQutSeq;
	}

	public void setReaQusQutSeq(Integer reaQusQutSeq) {
		this.reaQusQutSeq = reaQusQutSeq;
	}

	@Column(name = "REA_QUS_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getReaQusSeqp() {
		return this.reaQusSeqp;
	}

	public void setReaQusSeqp(Short reaQusSeqp) {
		this.reaQusSeqp = reaQusSeqp;
	}

	@Column(name = "REA_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getReaSeqp() {
		return this.reaSeqp;
	}

	public void setReaSeqp(Short reaSeqp) {
		this.reaSeqp = reaSeqp;
	}
	
	@Column(name = "SEQP", nullable = false, precision = 6, scale = 0)
	public Integer getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getReaAnaSeq());
        umHashCodeBuilder.append(this.getReaQusQutSeq());
        umHashCodeBuilder.append(this.getReaQusSeqp());
        umHashCodeBuilder.append(this.getReaSeqp());
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
		MamRespQuestAnamnesesId other = (MamRespQuestAnamnesesId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getReaAnaSeq(), other.getReaAnaSeq());
        umEqualsBuilder.append(this.getReaQusQutSeq(), other.getReaQusQutSeq());
        umEqualsBuilder.append(this.getReaQusSeqp(), other.getReaQusSeqp());
        umEqualsBuilder.append(this.getReaSeqp(), other.getReaSeqp());
        umEqualsBuilder.append(this.getSeqp(), other.getSeqp());
        return umEqualsBuilder.isEquals();
	}
}
