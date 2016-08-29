package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MbcAvalPreSedacaoId implements EntityCompositeId  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6014593623260880505L;
	
	private Integer dcgCrgSeq;
	
	private Short dcgSeqp;

	public MbcAvalPreSedacaoId() {
	}

	public MbcAvalPreSedacaoId(Integer dcgCrgSeq, Short dcgSeqp) {
		this.dcgCrgSeq = dcgCrgSeq;
		this.dcgSeqp = dcgSeqp;
	}

	@Column(name = "DCG_CRG_SEQ", nullable = false)
	public Integer getDcgCrgSeq() {
		return dcgCrgSeq;
	}

	public void setDcgCrgSeq(Integer dcgCrgSeq) {
		this.dcgCrgSeq = dcgCrgSeq;
	}

	@Column(name = "DCG_SEQP", nullable = false)
	public Short getDcgSeqp() {
		return dcgSeqp;
	}

	public void setDcgSeqp(Short dcgSeqp) {
		this.dcgSeqp = dcgSeqp;
	}
	
	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getDcgSeqp());
		umHashCodeBuilder.append(this.getDcgCrgSeq());
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
		if (!(obj instanceof MbcAvalPreSedacaoId)) {
			return false;
		}
		MbcAvalPreSedacaoId other = (MbcAvalPreSedacaoId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getDcgSeqp(), other.getDcgSeqp());
		umEqualsBuilder.append(this.getDcgCrgSeq(), other.getDcgCrgSeq());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
