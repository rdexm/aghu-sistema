package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MamRespQuestEvolucoesId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5483083205706198139L;
	private Long revEvoSeq;
	private Integer revQusQutSeq;
	private Short revQusSeqp;
	private Short revSeqp;
	private Integer seqp;

	public MamRespQuestEvolucoesId() {
	}


	@Column(name = "REV_EVO_SEQ", nullable = false, precision = 14, scale = 0)
	public Long getRevEvoSeq() {
		return this.revEvoSeq;
	}

	public void setRevEvoSeq(Long evoSeq) {
		this.revEvoSeq = evoSeq;
	}

	@Column(name = "REV_QUS_QUT_SEQ", nullable = false, precision = 6, scale = 0)
	public Integer getRevQusQutSeq() {
		return this.revQusQutSeq;
	}

	public void setRevQusQutSeq(Integer revQusQutSeq) {
		this.revQusQutSeq = revQusQutSeq;
	}

	@Column(name = "REV_QUS_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getRevQusSeqp() {
		return this.revQusSeqp;
	}

	public void setRevQusSeqp(Short revQusSeqp) {
		this.revQusSeqp = revQusSeqp;
	}

	@Column(name = "REV_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getRevSeqp() {
		return this.revSeqp;
	}

	public void setRevSeqp(Short revSeqp) {
		this.revSeqp = revSeqp;
	}
	
	@Column(name = "SEQP", nullable = false, precision = 6, scale = 0)
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
		if (!(other instanceof MamRespQuestEvolucoesId)) {
			return false;
		}
		MamRespQuestEvolucoesId castOther = (MamRespQuestEvolucoesId) other;

		return (this.getRevEvoSeq() == castOther.getRevEvoSeq())
				&& (this.getRevQusQutSeq() == castOther.getRevQusQutSeq())
				&& (this.getRevQusSeqp() == castOther.getRevQusSeqp())
				&& (this.getRevSeqp() == castOther.getRevSeqp())
				&& (this.getSeqp() == castOther.getSeqp());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getRevEvoSeq().intValue();
		result = 37 * result + this.getRevQusQutSeq();
		result = 37 * result + this.getRevQusSeqp();
		result = 37 * result + this.getRevSeqp();
		result = 37 * result + this.getSeqp();
		return result;
	}

}
