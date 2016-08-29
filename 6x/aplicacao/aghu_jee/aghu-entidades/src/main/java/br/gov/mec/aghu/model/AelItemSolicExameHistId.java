package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class AelItemSolicExameHistId implements EntityCompositeId, IAelItemSolicitacaoExamesId {

	private static final long serialVersionUID = 6184927925997153943L;
	private Integer soeSeq;
	private Short seqp;

	public AelItemSolicExameHistId() {
	}

	public AelItemSolicExameHistId(Integer soeSeq, Short seqp) {
		this.soeSeq = soeSeq;
		this.seqp = seqp;
	}

	@Column(name = "SOE_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSoeSeq() {
		return this.soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	@Column(name = "SEQP", nullable = false, precision = 3, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	
	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof AelItemSolicitacaoExamesId)) {
			return false;
		}
		AelItemSolicExameHistId castOther = (AelItemSolicExameHistId) other;
		return ( this.getSoeSeq() != null && this.getSoeSeq().equals(castOther.getSoeSeq()))
				&& (this.getSeqp() != null && this.getSeqp().equals(castOther.getSeqp())  );
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + ((this.getSoeSeq() != null) ? this.getSoeSeq() : 0);
		result = 37 * result + ((this.getSeqp() != null) ? this.getSeqp() : 0);
		return result;
	}
	
}
