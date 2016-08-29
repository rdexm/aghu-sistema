package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MptPrescricaoMedicamentoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6766867727761329682L;
	private Integer pteAtdSeq;
	private Integer pteSeq;
	private Integer seq;

	public MptPrescricaoMedicamentoId() {
	}

	public MptPrescricaoMedicamentoId(Integer pteAtdSeq, Integer pteSeq, Integer seq) {
		this.pteAtdSeq = pteAtdSeq;
		this.pteSeq = pteSeq;
		this.seq = seq;
	}

	@Column(name = "PTE_ATD_SEQ", nullable = false, precision = 7, scale = 0)
	public Integer getPteAtdSeq() {
		return this.pteAtdSeq;
	}

	public void setPteAtdSeq(Integer pteAtdSeq) {
		this.pteAtdSeq = pteAtdSeq;
	}

	@Column(name = "PTE_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getPteSeq() {
		return this.pteSeq;
	}

	public void setPteSeq(Integer pteSeq) {
		this.pteSeq = pteSeq;
	}

	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((pteAtdSeq == null) ? 0 : pteAtdSeq.hashCode());
		result = prime * result + ((pteSeq == null) ? 0 : pteSeq.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MptPrescricaoMedicamentoId other = (MptPrescricaoMedicamentoId) obj;
		if (pteAtdSeq == null) {
			if (other.pteAtdSeq != null) {
				return false;
			}
		} else if (!pteAtdSeq.equals(other.pteAtdSeq)) {
			return false;
		}
		if (pteSeq == null) {
			if (other.pteSeq != null) {
				return false;
			}
		} else if (!pteSeq.equals(other.pteSeq)) {
			return false;
		}
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

}
