package br.gov.mec.aghu.model;

// Generated 18/03/2010 16:55:22 by Hibernate Tools 3.3.0.GA

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * VAacSiglaUnfSalaId generated by hbm2java
 */
@Embeddable
public class VAacSiglaUnfSalaId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5378455386232448163L;
	private Short unfSeq;
	private Byte sala;
	
	public VAacSiglaUnfSalaId() {
	}

	@Column(name = "UNF_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getUnfSeq() {
		return this.unfSeq;
	}
	
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	
	@Column(name = "SALA", nullable = false, precision = 2, scale = 0)
	public Byte getSala() {
		return this.sala;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sala;
		result = prime * result + unfSeq;
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
		VAacSiglaUnfSalaId other = (VAacSiglaUnfSalaId) obj;
		if (sala != other.sala) {
			return false;
		}
		if (unfSeq != other.unfSeq) {
			return false;
		}
		return true;
	}

	public void setSala(Byte sala) {
		this.sala = sala;
	}

}
