package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MamMvtoTriagemId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 587827561002851258L;
	private Long trgSeq;
	private Double seqp;

	public MamMvtoTriagemId() {
	}

	public MamMvtoTriagemId(Long trgSeq, Double seqp) {
		this.trgSeq = trgSeq;
		this.seqp = seqp;
	}

	@Column(name = "TRG_SEQ", nullable = false)
	public Long getTrgSeq() {
		return this.trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	@Column(name = "SEQP", nullable = false, precision = 17, scale = 17)
	public Double getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Double seqp) {
		this.seqp = seqp;
	}






	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeqp());
		umHashCodeBuilder.append(this.getTrgSeq());
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
		if (!(obj instanceof MamMvtoTriagemId)) {
			return false;
		}
		MamMvtoTriagemId other = (MamMvtoTriagemId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeqp(), other.getSeqp());
		umEqualsBuilder.append(this.getTrgSeq(), other.getTrgSeq());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
