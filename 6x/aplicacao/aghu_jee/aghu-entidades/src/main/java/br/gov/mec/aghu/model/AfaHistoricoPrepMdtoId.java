package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * AfaHistoricoPrepMdtoId generated by hbm2java
 */
@Embeddable
public class AfaHistoricoPrepMdtoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6995555272813974975L;
	private Integer ptoSeq;
	private Float seqp;

	public AfaHistoricoPrepMdtoId() {
	}

	public AfaHistoricoPrepMdtoId(Integer ptoSeq, Float seqp) {
		this.ptoSeq = ptoSeq;
		this.seqp = seqp;
	}

	@Column(name = "PTO_SEQ", nullable = false)
	public Integer getPtoSeq() {
		return this.ptoSeq;
	}

	public void setPtoSeq(Integer ptoSeq) {
		this.ptoSeq = ptoSeq;
	}

	@Column(name = "SEQP", nullable = false, precision = 8, scale = 8)
	public Float getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Float seqp) {
		this.seqp = seqp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ptoSeq == null) ? 0 : ptoSeq.hashCode());
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
		if (!(obj instanceof AfaHistoricoPrepMdtoId)) {
			return false;
		}
		AfaHistoricoPrepMdtoId other = (AfaHistoricoPrepMdtoId) obj;
		if (ptoSeq == null) {
			if (other.ptoSeq != null) {
				return false;
			}
		} else if (!ptoSeq.equals(other.ptoSeq)) {
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
