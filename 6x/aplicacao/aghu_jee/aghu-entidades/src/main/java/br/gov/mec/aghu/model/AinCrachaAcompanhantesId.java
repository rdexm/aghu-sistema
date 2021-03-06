package br.gov.mec.aghu.model;

// Generated 11/06/2010 20:00:31 by Hibernate Tools 3.2.5.Beta

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * AinCrachaAcompanhantesId generated by hbm2java
 */
@Embeddable

public class AinCrachaAcompanhantesId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5306723800000270678L;
	private Integer aciIntSeq;
	private Byte aciSeq;
	private Byte seqp;

	public AinCrachaAcompanhantesId() {
	}

	@Column(name = "ACI_INT_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getAciIntSeq() {
		return this.aciIntSeq;
	}

	@Column(name = "ACI_SEQ", nullable = false, precision = 2, scale = 0)
	public Byte getAciSeq() {
		return this.aciSeq;
	}

	@Column(name = "SEQP", nullable = false, precision = 2, scale = 0)
	public Byte getSeqp() {
		return this.seqp;
	}

	public void setAciIntSeq(Integer aciIntSeq) {
		this.aciIntSeq = aciIntSeq;
	}

	public void setAciSeq(Byte aciSeq) {
		this.aciSeq = aciSeq;
	}

	public void setSeqp(Byte seqp) {
		this.seqp = seqp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((aciIntSeq == null) ? 0 : aciIntSeq.hashCode());
		result = prime * result + ((aciSeq == null) ? 0 : aciSeq.hashCode());
		result = prime * result + ((seqp == null) ? 0 : seqp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AinCrachaAcompanhantesId other = (AinCrachaAcompanhantesId) obj;
		if (aciIntSeq == null) {
			if (other.aciIntSeq != null) {
				return false;
			}
		} else if (!aciIntSeq.equals(other.aciIntSeq)) {
			return false;
		}
		if (aciSeq == null) {
			if (other.aciSeq != null) {
				return false;
			}
		} else if (!aciSeq.equals(other.aciSeq)) {
			return false;
		}
		if (seqp == null) {
			if (other.seqp != null) {
				return false;
			}
		} else if (!seqp.equals(other.seqp)) {
			return false;
		}
		return true;
	}

	
}
