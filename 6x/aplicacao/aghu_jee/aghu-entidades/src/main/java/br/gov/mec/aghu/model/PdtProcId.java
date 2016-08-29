package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * PdtProcId generated by hbm2java
 */
@Embeddable
public class PdtProcId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7373356577495630447L;
	private Integer ddtSeq;
	private Short seqp;

	public PdtProcId() {
	}

	public PdtProcId(Integer ddtSeq, Short seqp) {
		this.ddtSeq = ddtSeq;
		this.seqp = seqp;
	}

	@Column(name = "DDT_SEQ", nullable = false)
	public Integer getDdtSeq() {
		return this.ddtSeq;
	}

	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	@Column(name = "SEQP", nullable = false)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeqp());
		umHashCodeBuilder.append(this.getDdtSeq());
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
		if (!(obj instanceof PdtProcId)) {
			return false;
		}
		PdtProcId other = (PdtProcId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeqp(), other.getSeqp());
		umEqualsBuilder.append(this.getDdtSeq(), other.getDdtSeq());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}