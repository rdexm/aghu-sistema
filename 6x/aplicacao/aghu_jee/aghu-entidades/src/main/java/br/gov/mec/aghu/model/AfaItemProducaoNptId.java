package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class AfaItemProducaoNptId implements EntityCompositeId {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3408809333468913789L;
	private Long cpnSeq;
	private Integer seqp;

	public AfaItemProducaoNptId() {
	}

	public AfaItemProducaoNptId(Long cpnSeq, Integer seqp) {
		this.cpnSeq = cpnSeq;
		this.seqp = seqp;
	}

	@Column(name = "CPN_SEQ", nullable = false)
	public Long getCpnSeq() {
		return this.cpnSeq;
	}

	public void setCpnSeq(Long cntSeq) {
		this.cpnSeq = cntSeq;
	}

	@Column(name = "SEQP", nullable = false)
	public Integer getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}


	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeqp());
		umHashCodeBuilder.append(this.getCpnSeq());
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
		if (!(obj instanceof AfaItemProducaoNptId)) {
			return false;
		}
		AfaItemProducaoNptId other = (AfaItemProducaoNptId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeqp(), other.getSeqp());
		umEqualsBuilder.append(this.getCpnSeq(), other.getCpnSeq());
		return umEqualsBuilder.isEquals();
	}
	
}
