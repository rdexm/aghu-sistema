package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the ael_resultados_codificados database table.
 * 
 */
@Embeddable
public class AelResultadoGrupoPesquisaId implements EntityCompositeId {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4083631048154022876L;
	private Integer rcdGtcSeq;
	private Integer rcdSeqp;
	private Integer gpqSeq;
	
	
	public AelResultadoGrupoPesquisaId() {
		super();
	}


	public AelResultadoGrupoPesquisaId(Integer rcdGtcSeq, Integer rcdSeqp,
			Integer gpqSeq) {
		super();
		this.rcdGtcSeq = rcdGtcSeq;
		this.rcdSeqp = rcdSeqp;
		this.gpqSeq = gpqSeq;
	}


	@Column(name="RCD_GTC_SEQ")
	public Integer getRcdGtcSeq() {
		return rcdGtcSeq;
	}


	public void setRcdGtcSeq(Integer rcdGtcSeq) {
		this.rcdGtcSeq = rcdGtcSeq;
	}

	@Column(name="RCD_SEQP")
	public Integer getRcdSeqp() {
		return rcdSeqp;
	}


	public void setRcdSeqp(Integer rcdSeqp) {
		this.rcdSeqp = rcdSeqp;
	}

	@Column(name="GPQ_SEQ")
	public Integer getGpqSeq() {
		return gpqSeq;
	}

	public void setGpqSeq(Integer gpqSeq) {
		this.gpqSeq = gpqSeq;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gpqSeq == null) ? 0 : gpqSeq.hashCode());
		result = prime * result
				+ ((rcdGtcSeq == null) ? 0 : rcdGtcSeq.hashCode());
		result = prime * result + ((rcdSeqp == null) ? 0 : rcdSeqp.hashCode());
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
		if (!(obj instanceof AelResultadoGrupoPesquisaId)) {
			return false;
		}
		AelResultadoGrupoPesquisaId other = (AelResultadoGrupoPesquisaId) obj;
		if (gpqSeq == null) {
			if (other.gpqSeq != null) {
				return false;
			}
		} else if (!gpqSeq.equals(other.gpqSeq)) {
			return false;
		}
		if (rcdGtcSeq == null) {
			if (other.rcdGtcSeq != null) {
				return false;
			}
		} else if (!rcdGtcSeq.equals(other.rcdGtcSeq)) {
			return false;
		}
		if (rcdSeqp == null) {
			if (other.rcdSeqp != null) {
				return false;
			}
		} else if (!rcdSeqp.equals(other.rcdSeqp)) {
			return false;
		}
		return true;
	}
	
	
}