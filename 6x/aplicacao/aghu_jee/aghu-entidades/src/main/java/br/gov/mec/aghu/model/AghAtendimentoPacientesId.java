package br.gov.mec.aghu.model;

// Generated 08/02/2010 17:25:25 by Hibernate Tools 3.2.5.Beta

import javax.persistence.Column;
import javax.persistence.Embeddable;


import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * AghAtendimentoPacientesId generated by hbm2java
 */
@Embeddable
public class AghAtendimentoPacientesId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5464140460149033060L;
	private Integer atdSeq;
	private Integer seq;

	public AghAtendimentoPacientesId() {
	}

	public AghAtendimentoPacientesId(Integer atdSeq, Integer seq) {
		this.atdSeq = atdSeq;
		this.seq = seq;
	}

	@Column(name = "ATD_SEQ", nullable = false, unique = true, updatable = false, precision = 7, scale = 0)
	public Integer getAtdSeq() {
		return this.atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof AghAtendimentoPacientesId)) {
			return false;
		}
		AghAtendimentoPacientesId castOther = (AghAtendimentoPacientesId) other;

		return this.getAtdSeq() != null && this.getAtdSeq().equals(castOther.getAtdSeq()) &&
				this.getSeq() != null && this.getSeq().equals(castOther.getSeq());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + (this.getAtdSeq() == null ? 0 : this.getAtdSeq().hashCode());
		result = 37 * result + (this.getSeq() == null ? 0 : this.getSeq().hashCode());
		return result;
	}

}
