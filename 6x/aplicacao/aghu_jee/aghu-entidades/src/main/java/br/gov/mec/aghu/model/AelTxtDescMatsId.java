package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the AEL_TXT_DESC_MATS database table.
 * 
 */
@Embeddable
public class AelTxtDescMatsId implements EntityCompositeId {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1946148321558898294L;
	
	private Short gtmSeq;
	private Short seqp;

	public AelTxtDescMatsId() {
	}

	public AelTxtDescMatsId(Short gtmSeq, Short seqp) {
		super();
		this.gtmSeq = gtmSeq;
		this.seqp = seqp;
	}

	@Column(name="GTM_SEQ", unique=true, nullable=false, precision=4, scale=0)
	public Short getGtmSeq() {
		return this.gtmSeq;
	}
	public void setGtmSeq(Short gtmSeq) {
		this.gtmSeq = gtmSeq;
	}

	@Column(unique=true, nullable=false, precision=4, scale=0)
	public Short getSeqp() {
		return this.seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AelTxtDescMatsId)) {
			return false;
		}
		AelTxtDescMatsId castOther = (AelTxtDescMatsId)other;
		return 
			(this.gtmSeq == castOther.gtmSeq)
			&& (this.seqp == castOther.seqp);
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getGtmSeq();
		result = 37 * result + this.getSeqp();
		return result;
	}
	
}