package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the AEL_GRP_DESC_MAT_LACUNAS database table.
 * 
 */
@Embeddable
public class AelGrpDescMatLacunasId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2010703765140223211L;
	private Short gtmSeq;
	private Short ldaSeq;
	private Short seqp;

	public AelGrpDescMatLacunasId() {
	}

	public AelGrpDescMatLacunasId(Short gtmSeq, Short ldaSeq, Short seqp) {
		super();
		this.gtmSeq = gtmSeq;
		this.ldaSeq = ldaSeq;
		this.seqp = seqp;
	}

	@Column(name = "GTM_SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Short getGtmSeq() {
		return this.gtmSeq;
	}

	public void setGtmSeq(Short gtmSeq) {
		this.gtmSeq = gtmSeq;
	}

	@Column(name = "LDA_SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Short getLdaSeq() {
		return this.ldaSeq;
	}

	public void setLdaSeq(Short ldaSeq) {
		this.ldaSeq = ldaSeq;
	}

	@Column(unique = true, nullable = false, precision = 4, scale = 0)
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
		if (!(other instanceof AelGrpDescMatLacunasId)) {
			return false;
		}
		AelGrpDescMatLacunasId castOther = (AelGrpDescMatLacunasId) other;
		return (this.gtmSeq == castOther.gtmSeq) && (this.ldaSeq == castOther.ldaSeq) && (this.seqp == castOther.seqp);
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.gtmSeq;
		result = 37 * result + this.ldaSeq;
		result = 37 * result + this.seqp;
		return result;
	}
}