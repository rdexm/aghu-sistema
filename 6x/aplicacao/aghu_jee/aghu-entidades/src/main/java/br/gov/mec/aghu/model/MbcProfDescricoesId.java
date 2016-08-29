package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * Migrado durante o desenvolvimento do módulo Faturamento</br>
 * TODO: <b>REVISAR CAMPOS (RELACIONAMENTOS)</b>
 */

@Embeddable
public class MbcProfDescricoesId implements EntityCompositeId {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1345052279159509034L;
	private Integer dcgCrgSeq;
	private Short dcgSeqp;
	private Integer seqp;

	public MbcProfDescricoesId() {
	}

	public MbcProfDescricoesId(Integer dcgCrgSeq, Short dcgSeqp, Integer seqp) {
		this.dcgCrgSeq = dcgCrgSeq;
		this.dcgSeqp = dcgSeqp;
		this.seqp = seqp;
	}

	@Column(name = "DCG_CRG_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getDcgCrgSeq() {
		return dcgCrgSeq;
	}

	@Column(name = "DCG_SEQP", nullable = false, precision = 2, scale = 0)
	public Short getDcgSeqp() {
		return dcgSeqp;
	}

	@Column(name = "SEQP", nullable = false)
	public Integer getSeqp() {
		return seqp;
	}

	public void setDcgCrgSeq(Integer dcgCrgSeq) {
		this.dcgCrgSeq = dcgCrgSeq;
	}

	public void setDcgSeqp(Short dcgSeq) {
		this.dcgSeqp = dcgSeq;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dcgCrgSeq == null) ? 0 : dcgCrgSeq.hashCode());
		result = prime * result + ((dcgSeqp == null) ? 0 : dcgSeqp.hashCode());
		result = prime * result + ((seqp == null) ? 0 : seqp.hashCode());
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
		MbcProfDescricoesId other = (MbcProfDescricoesId) obj;
		if (dcgCrgSeq == null) {
			if (other.dcgCrgSeq != null) {
				return false;
			}
		} else if (!dcgCrgSeq.equals(other.dcgCrgSeq)) {
			return false;
		}
		if (dcgSeqp == null) {
			if (other.dcgSeqp != null) {
				return false;
			}
		} else if (!dcgSeqp.equals(other.dcgSeqp)) {
			return false;
		}
		if (seqp == null) {
			if (other.seqp != null) {
				return false;
			}
		} else if (!seqp.equals(other.seqp)) {
			return false;
		}
		return true;
	}
}
