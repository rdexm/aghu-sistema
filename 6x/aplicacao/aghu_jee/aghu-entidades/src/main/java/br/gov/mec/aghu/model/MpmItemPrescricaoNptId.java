package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

import javax.persistence.Column;
import javax.persistence.Embeddable;


import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * MpmItemPrescricaoNptsId generated by hbm2java
 */
@Embeddable
public class MpmItemPrescricaoNptId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8681834982633022310L;
	private Integer cptPnpAtdSeq;
	private Integer cptPnpSeq;
	private Short cptSeqp;
	private Short seqp;

	public MpmItemPrescricaoNptId() {
	}

	public MpmItemPrescricaoNptId(Integer cptPnpAtdSeq, Integer cptPnpSeq,
			Short cptSeqp, Short seqp) {
		this.cptPnpAtdSeq = cptPnpAtdSeq;
		this.cptPnpSeq = cptPnpSeq;
		this.cptSeqp = cptSeqp;
		this.seqp = seqp;
	}

	@Column(name = "CPT_PNP_ATD_SEQ", nullable = false, precision = 7, scale = 0)
	public Integer getCptPnpAtdSeq() {
		return this.cptPnpAtdSeq;
	}

	public void setCptPnpAtdSeq(Integer cptPnpAtdSeq) {
		this.cptPnpAtdSeq = cptPnpAtdSeq;
	}

	@Column(name = "CPT_PNP_SEQ", nullable = false, precision = 14, scale = 0)
	public Integer getCptPnpSeq() {
		return this.cptPnpSeq;
	}

	public void setCptPnpSeq(Integer cptPnpSeq) {
		this.cptPnpSeq = cptPnpSeq;
	}

	@Column(name = "CPT_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getCptSeqp() {
		return this.cptSeqp;
	}

	public void setCptSeqp(Short cptSeqp) {
		this.cptSeqp = cptSeqp;
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
		if (!(other instanceof MpmItemPrescricaoNptId)) {
			return false;
		}
		MpmItemPrescricaoNptId castOther = (MpmItemPrescricaoNptId) other;

		return (this.getCptPnpAtdSeq() != null && this.getCptPnpAtdSeq().equals(castOther.getCptPnpAtdSeq()))
				&& (this.getCptPnpSeq() != null && this.getCptPnpSeq().equals(castOther.getCptPnpSeq()))
				&& (this.getCptSeqp() != null && this.getCptSeqp().equals(castOther.getCptSeqp()))
				&& (this.getSeqp() != null && this.getSeqp().equals(castOther.getSeqp()));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + (this.getCptPnpAtdSeq() == null ? 0 : this.getCptPnpAtdSeq().hashCode());
		result = 37 * result + (this.getCptPnpSeq() == null ? 0 : this.getCptPnpSeq().hashCode());
		result = 37 * result + (this.getCptSeqp() == null ? 0 : this.getCptSeqp().hashCode());
		result = 37 * result + (this.getSeqp() == null ? 0 : this.getSeqp().hashCode());
		
		return result;
	}

}
