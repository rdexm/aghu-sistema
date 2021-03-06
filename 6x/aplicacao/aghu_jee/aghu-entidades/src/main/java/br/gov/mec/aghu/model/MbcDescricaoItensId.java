package br.gov.mec.aghu.model;

// Generated 19/04/2012 16:57:27 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * MbcDescricaoItensId generated by hbm2java
 */
@Embeddable
public class MbcDescricaoItensId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3867402189382635132L;
	private Integer dcgCrgSeq;
	private Short dcgSeqp;

	public MbcDescricaoItensId() {
	}

	public MbcDescricaoItensId(Integer dcgCrgSeq, Short dcgSeqp) {
		this.dcgCrgSeq = dcgCrgSeq;
		this.dcgSeqp = dcgSeqp;
	}

	@Column(name = "DCG_CRG_SEQ", nullable = false)
	public Integer getDcgCrgSeq() {
		return this.dcgCrgSeq;
	}

	public void setDcgCrgSeq(Integer dcgCrgSeq) {
		this.dcgCrgSeq = dcgCrgSeq;
	}

	@Column(name = "DCG_SEQP", nullable = false)
	public Short getDcgSeqp() {
		return this.dcgSeqp;
	}

	public void setDcgSeqp(Short dcgSeqp) {
		this.dcgSeqp = dcgSeqp;
	}
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getDcgCrgSeq());
		umHashCodeBuilder.append(this.getDcgSeqp());
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MbcDescricaoItensId)) {
			return false;
		}
		MbcDescricaoItensId other = (MbcDescricaoItensId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getDcgCrgSeq(), other.getDcgCrgSeq());
		umEqualsBuilder.append(this.getDcgSeqp(), other.getDcgSeqp());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
