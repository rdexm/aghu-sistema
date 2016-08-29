package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the AEL_DESC_MAT_LACUNAS database table.
 * 
 */
@Embeddable
public class AelDescMatLacunasId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1174103387938804305L;
	
	private Short gtmSeq;
	private Short ldaSeq;
	private Short gmlSeq;
	private Short seqp;

	public AelDescMatLacunasId() {
	}

	public AelDescMatLacunasId(Short gtmSeq, Short ldaSeq, Short gmlSeq, Short seqp) {
		super();
		this.gtmSeq = gtmSeq;
		this.ldaSeq = ldaSeq;
		this.gmlSeq = gmlSeq;
		this.seqp = seqp;
	}

	@Column(name="GTM_SEQ", unique=true, nullable=false, precision=4)
	public Short getGtmSeq() {
		return this.gtmSeq;
	}
	public void setGtmSeq(Short gtmSeq) {
		this.gtmSeq = gtmSeq;
	}

	@Column(name="LDA_SEQ", unique=true, nullable=false, precision=4)
	public Short getLdaSeq() {
		return this.ldaSeq;
	}
	public void setLdaSeq(Short ldaSeq) {
		this.ldaSeq = ldaSeq;
	}

	@Column(name="GML_SEQ", unique=true, nullable=false, precision=4)
	public Short getGmlSeq() {
		return this.gmlSeq;
	}
	public void setGmlSeq(Short gmlSeq) {
		this.gmlSeq = gmlSeq;
	}

	@Column(unique=true, nullable=false, precision=4)
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
		if (!(other instanceof AelDescMatLacunasId)) {
			return false;
		}
		AelDescMatLacunasId castOther = (AelDescMatLacunasId)other;
		return 
			(this.gtmSeq == castOther.gtmSeq)
			&& (this.ldaSeq == castOther.ldaSeq)
			&& (this.gmlSeq == castOther.gmlSeq)
			&& (this.seqp == castOther.seqp);
	}
	
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.gtmSeq;
		result = 37 * result + this.ldaSeq;
		result = 37 * result + this.gmlSeq;
		result = 37 * result + this.seqp;
		return result;
	}

}