package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the ael_resultados_codificados database table.
 * 
 */
@Embeddable
public class AelResultadoCodificadoId implements EntityCompositeId {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7310499145699748620L;
	private Integer gtcSeq;
	private Integer seqp;

    public AelResultadoCodificadoId() {
    }

	public AelResultadoCodificadoId(Integer gtcSeq, Integer seqp) {
		super();
		this.gtcSeq = gtcSeq;
		this.seqp = seqp;
	}

	@Column(name="GTC_SEQ", nullable = false)
	public Integer getGtcSeq() {
		return this.gtcSeq;
	}
	public void setGtcSeq(Integer gtcSeq) {
		this.gtcSeq = gtcSeq;
	}
	@Column(name="SEQP", nullable = false)
	public Integer getSeqp() {
		return this.seqp;
	}
	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AelResultadoCodificadoId)) {
			return false;
		}
		AelResultadoCodificadoId castOther = (AelResultadoCodificadoId)other;
		return 
			this.gtcSeq.equals(castOther.gtcSeq)
			&& this.seqp.equals(castOther.seqp);

    }
    
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.gtcSeq.hashCode();
		hash = hash * prime + this.seqp.hashCode();
		
		return hash;
    }
}