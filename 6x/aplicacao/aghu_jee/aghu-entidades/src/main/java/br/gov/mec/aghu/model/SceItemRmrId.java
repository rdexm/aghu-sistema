package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the SCE_ITEM_RMRS database table.
 * 
 */
@Embeddable

public class SceItemRmrId implements EntityCompositeId {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3015474165406005099L;
	//default serial version id, required for serializable classes.
	private Integer rmrSeq;
	private Integer ealSeq;

    public SceItemRmrId() {
    }
    
	public SceItemRmrId(Integer rmrSeq, Integer ealSeq) {
		super();
		this.rmrSeq = rmrSeq;
		this.ealSeq = ealSeq;
	}

	@Column(name="RMR_SEQ", unique=true, nullable=false, precision=7)
	public Integer getRmrSeq() {
		return this.rmrSeq;
	}
	public void setRmrSeq(Integer rmrSeq) {
		this.rmrSeq = rmrSeq;
	}

	@Column(name="EAL_SEQ", unique=true, nullable=false, precision=7)
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
		if (!(other instanceof SceItemRmrId)) {
			return false;
		}
		SceItemRmrId castOther = (SceItemRmrId)other;
		return 
			(this.rmrSeq == castOther.rmrSeq)
			&& (this.ealSeq == castOther.ealSeq);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + (this.rmrSeq ^ (this.rmrSeq >>> 32));
		hash = hash * prime + (this.ealSeq ^ (this.ealSeq >>> 32));
		
		return hash;
    }
}