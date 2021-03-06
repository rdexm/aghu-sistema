package br.gov.mec.aghu.model;

// Generated 07/05/2010 10:12:13 by Hibernate Tools 3.2.5.Beta

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * MamAreaAtuNrosId generated by hbm2java
 */
@Embeddable

public class MamAreaAtuacaoNumeroId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6064524896422836307L;
	private Integer seqp;
	private Integer araSeq;

	public MamAreaAtuacaoNumeroId() {
	}

	public MamAreaAtuacaoNumeroId(Integer seqp, Integer araSeq) {
		this.seqp = seqp;
		this.araSeq = araSeq;
	}

	@Column(name = "SEQP", nullable = false, precision = 5, scale = 0)
	public Integer getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	@Column(name = "ARA_SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getAraSeq() {
		return this.araSeq;
	}

	public void setAraSeq(Integer araSeq) {
		this.araSeq = araSeq;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof MamAreaAtuacaoNumeroId)) {
			return false;
		}
		MamAreaAtuacaoNumeroId castOther = (MamAreaAtuacaoNumeroId) other;

		return (this.getSeqp() == castOther.getSeqp())
				&& (this.getAraSeq() == castOther.getAraSeq());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getSeqp();
		result = 37 * result + this.getAraSeq();
		return result;
	}

}
