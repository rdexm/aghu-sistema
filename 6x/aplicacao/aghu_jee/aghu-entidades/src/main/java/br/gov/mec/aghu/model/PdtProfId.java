package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import br.gov.mec.aghu.core.persistence.EntityCompositeId;



@Embeddable
public class PdtProfId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8661779439096271817L;
	private Integer ddtSeq;
	private Short seqp;
	
	public PdtProfId(Integer ddtSeq, Short seqp) {
		super();
		this.ddtSeq = ddtSeq;
		this.seqp = seqp;
	}
	
	public PdtProfId() {
		
	}

	@Column(name = "DDT_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getDdtSeq() {
		return ddtSeq;
	}
	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}
	
	@Column(name = "SEQP", nullable = false, precision = 3, scale = 0)
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ddtSeq == null) ? 0 : ddtSeq.hashCode());
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
		PdtProfId other = (PdtProfId) obj;
		if (ddtSeq == null) {
			if (other.ddtSeq != null) {
				return false;
			}
		} else if (!ddtSeq.equals(other.ddtSeq)) {
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
