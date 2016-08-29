package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * PdtCidPorProcId generated by hbm2java
 */
@Embeddable
public class PdtCidPorProcId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7460175594081132301L;
	private Integer dptSeq;
	private Integer cidSeq;

	public PdtCidPorProcId() {
	}

	public PdtCidPorProcId(Integer dptSeq, Integer cidSeq) {
		this.dptSeq = dptSeq;
		this.cidSeq = cidSeq;
	}

	@Column(name = "DPT_SEQ", nullable = false)
	public Integer getDptSeq() {
		return this.dptSeq;
	}

	public void setDptSeq(Integer dptSeq) {
		this.dptSeq = dptSeq;
	}

	@Column(name = "CID_SEQ", nullable = false)
	public Integer getCidSeq() {
		return this.cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCidSeq());
		umHashCodeBuilder.append(this.getDptSeq());
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
		if (!(obj instanceof PdtCidPorProcId)) {
			return false;
		}
		PdtCidPorProcId other = (PdtCidPorProcId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCidSeq(), other.getCidSeq());
		umEqualsBuilder.append(this.getDptSeq(), other.getDptSeq());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}