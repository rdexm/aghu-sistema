package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MamRespostaEvolucoesId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2510795073246202149L;
	private Long evoSeq;
	private Integer qusQutSeq;
	private Short qusSeqp;
	private Short seqp;

	public MamRespostaEvolucoesId() {
	}

	public MamRespostaEvolucoesId(Long evoSeq, Integer qusQutSeq, Short qusSeqp,
			Short seqp) {
		this.evoSeq = evoSeq;
		this.qusQutSeq = qusQutSeq;
		this.qusSeqp = qusSeqp;
		this.seqp = seqp;
	}

	@Column(name = "EVO_SEQ", nullable = false, precision = 14, scale = 0)
	public Long getEvoSeq() {
		return this.evoSeq;
	}

	public void setEvoSeq(Long evoSeq) {
		this.evoSeq = evoSeq;
	}

	@Column(name = "QUS_QUT_SEQ", nullable = false, precision = 6, scale = 0)
	public Integer getQusQutSeq() {
		return this.qusQutSeq;
	}

	public void setQusQutSeq(Integer qusQutSeq) {
		this.qusQutSeq = qusQutSeq;
	}

	@Column(name = "QUS_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getQusSeqp() {
		return this.qusSeqp;
	}

	public void setQusSeqp(Short qusSeqp) {
		this.qusSeqp = qusSeqp;
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
		if (!(other instanceof MamRespostaEvolucoesId)) {
			return false;
		}
		MamRespostaEvolucoesId castOther = (MamRespostaEvolucoesId) other;

		return (this.getEvoSeq() == castOther.getEvoSeq())
				&& (this.getQusQutSeq() == castOther.getQusQutSeq())
				&& (this.getQusSeqp() == castOther.getQusSeqp())
				&& (this.getSeqp() == castOther.getSeqp());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getEvoSeq().intValue();
		result = 37 * result + this.getQusQutSeq();
		result = 37 * result + this.getQusSeqp();
		result = 37 * result + this.getSeqp();
		return result;
	}

}
