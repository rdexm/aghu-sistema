package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class MpmTipoModoUsoProcedimentoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4806902703020823633L;
	private Short pedSeq;
	private Short seqp;

	public MpmTipoModoUsoProcedimentoId() {
	}

	public MpmTipoModoUsoProcedimentoId(Short pedSeq,
			Short seqp) {
		this.pedSeq = pedSeq;
		this.seqp = seqp;
	}

	@Column(name = "PED_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getPedSeq() {
		return pedSeq;
	}

	public void setPedSeq(
			Short pedSeq) {
		this.pedSeq = pedSeq;
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
		if (!(other instanceof MpmTipoModoUsoProcedimentoId)) {
			return false;
		}
		MpmTipoModoUsoProcedimentoId castOther = (MpmTipoModoUsoProcedimentoId) other;

		return ( (this.getPedSeq() == null && castOther.getPedSeq() == null) || this.getPedSeq().equals(castOther
			.getPedSeq()) ) && (this.getSeqp().equals(castOther.getSeqp()));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getPedSeq();
		result = 37 * result + this.getSeqp();
		return result;
	}

}
