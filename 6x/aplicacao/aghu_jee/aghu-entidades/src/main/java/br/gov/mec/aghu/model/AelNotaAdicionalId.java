package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class AelNotaAdicionalId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7601356377416285684L;
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private Integer seqp;
	
	public AelNotaAdicionalId() {
	}
	
	public AelNotaAdicionalId(Integer iseSoeSeq, Short iseSeqp, Integer seqp) {
		super();
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


	@Column(name = "SEQP", nullable = false, precision = 5, scale = 0)
	public Integer getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Integer seqp) {
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
		if (!(other instanceof AelNotaAdicionalId)) {
			return false;
		}
		AelNotaAdicionalId castOther = (AelNotaAdicionalId) other;

		return ( this.getIseSoeSeq() != null && this.getIseSoeSeq().equals(castOther.getIseSoeSeq()))
				&& (this.getIseSeqp() != null && this.getIseSeqp().equals(castOther.getIseSeqp())
				&& (this.getSeqp() != null && this.getSeqp().equals(castOther.getSeqp())));
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

	
}