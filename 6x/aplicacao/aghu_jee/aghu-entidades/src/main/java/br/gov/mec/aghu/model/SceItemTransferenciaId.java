package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sce_item_trs database table.
 * 
 */
@Embeddable
public class SceItemTransferenciaId implements EntityCompositeId {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7406266321724408298L;

	//default serial version id, required for serializable classes.	
	private Integer ealSeq;
	private Integer trfSeq;
    
	public SceItemTransferenciaId() {
    }


	@Column(name = "TRF_SEQ", nullable = false)
	public Integer getTrfSeq() {
		return this.trfSeq;
	}

	public void setTrfSeq(Integer trfSeq) {
		this.trfSeq = trfSeq;
	}
	

	@Column(name = "EAL_SEQ", nullable = false)
	public Integer getEalSeq() {
		return this.ealSeq;
	}

	public void setEalSeq(Integer ealSeq) {
		this.ealSeq = ealSeq;
	}

	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SceItemTransferenciaId)) {
			return false;
		}
		SceItemTransferenciaId castOther = (SceItemTransferenciaId)other;
		return 
			this.trfSeq.equals(castOther.trfSeq)
			&& this.ealSeq.equals(castOther.ealSeq);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.trfSeq.hashCode();
		hash = hash * prime + this.ealSeq.hashCode();
		
		return hash;
    }
}