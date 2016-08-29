package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class AelExtratoItemSolicitacaoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6228129307795869525L;
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private Short seqp;

	public AelExtratoItemSolicitacaoId() {
	}

	public AelExtratoItemSolicitacaoId(Integer iseSoeSeq, Short iseSeqp, Short seqp) {
		this.iseSoeSeq = iseSoeSeq;
		this.iseSeqp = iseSeqp;
		this.seqp = seqp;
	}

	@Column(name = "ISE_SOE_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getIseSoeSeq() {
		return this.iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	@Column(name = "ISE_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getIseSeqp() {
		return this.iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	@Column(name = "SEQP", nullable = false, precision = 3, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iseSeqp == null) ? 0 : iseSeqp.hashCode());
		result = prime * result
				+ ((iseSoeSeq == null) ? 0 : iseSoeSeq.hashCode());
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
		AelExtratoItemSolicitacaoId other = (AelExtratoItemSolicitacaoId) obj;
		if (iseSeqp == null) {
			if (other.iseSeqp != null) {
				return false;
			}
		} else if (!iseSeqp.equals(other.iseSeqp)) {
			return false;
		}
		if (iseSoeSeq == null) {
			if (other.iseSoeSeq != null) {
				return false;
			}
		} else if (!iseSoeSeq.equals(other.iseSoeSeq)) {
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
