package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * MptExtratoCtrlFreqSessaoId generated by hbm2java
 */
@Embeddable
public class MptExtratoCtrlFreqSessaoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6533486962790804174L;
	private Integer cfsAgeSeq;
	private Date cfsData;
	private Short seqp;

	public MptExtratoCtrlFreqSessaoId() {
	}

	public MptExtratoCtrlFreqSessaoId(Integer cfsAgeSeq, Date cfsData, Short seqp) {
		this.cfsAgeSeq = cfsAgeSeq;
		this.cfsData = cfsData;
		this.seqp = seqp;
	}

	@Column(name = "CFS_AGE_SEQ", nullable = false)
	public Integer getCfsAgeSeq() {
		return this.cfsAgeSeq;
	}

	public void setCfsAgeSeq(Integer cfsAgeSeq) {
		this.cfsAgeSeq = cfsAgeSeq;
	}

	@Column(name = "CFS_DATA", nullable = false, length = 29)
	public Date getCfsData() {
		return this.cfsData;
	}

	public void setCfsData(Date cfsData) {
		this.cfsData = cfsData;
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
		umHashCodeBuilder.append(this.getCfsAgeSeq());
		umHashCodeBuilder.append(this.getCfsData());
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
		if (!(obj instanceof MptExtratoCtrlFreqSessaoId)) {
			return false;
		}
		MptExtratoCtrlFreqSessaoId other = (MptExtratoCtrlFreqSessaoId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeqp(), other.getSeqp());
		umEqualsBuilder.append(this.getCfsAgeSeq(), other.getCfsAgeSeq());
		umEqualsBuilder.append(this.getCfsData(), other.getCfsData());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
