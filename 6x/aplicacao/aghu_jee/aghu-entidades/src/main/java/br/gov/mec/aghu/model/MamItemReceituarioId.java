package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class MamItemReceituarioId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4357127884111302885L;
	private Long rctSeq;
	private Short seqp;

	// construtores
	public MamItemReceituarioId() {
	}

	public MamItemReceituarioId(Long rctSeq, Short seqp) {
		this.rctSeq = rctSeq;
		this.seqp = seqp;
	}

	// getters & setters

	@Column(name = "RCT_SEQ", length = 14, nullable = false)
	public Long getRctSeq() {
		return this.rctSeq;
	}

	public void setRctSeq(Long rctSeq) {
		this.rctSeq = rctSeq;
	}

	@Column(name = "SEQP", length = 3, nullable = false)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("rctSeq", this.rctSeq)
				.append("seqp", this.seqp).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MamItemReceituarioId)) {
			return false;
		}
		MamItemReceituarioId castOther = (MamItemReceituarioId) other;
		return new EqualsBuilder().append(this.rctSeq, castOther.getRctSeq())
				.append(this.seqp, castOther.getSeqp()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.rctSeq).append(this.seqp)
				.toHashCode();
	}
}
