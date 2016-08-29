package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sce_item_receb_provisorios database table.
 * 
 */
@Embeddable
public class SceItemRecebProvisorioId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8102175510431755L;
	private Integer nrpSeq;
	private Integer nroItem;

    public SceItemRecebProvisorioId() {
    }

	@Column(name="NRP_SEQ")
	public Integer getNrpSeq() {
		return this.nrpSeq;
	}
	public void setNrpSeq(Integer nrpSeq) {
		this.nrpSeq = nrpSeq;
	}

	@Column(name="NRO_ITEM")
	public Integer getNroItem() {
		return this.nroItem;
	}
	public void setNroItem(Integer nroItem) {
		this.nroItem = nroItem;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SceItemRecebProvisorioId)) {
			return false;
		}
		SceItemRecebProvisorioId castOther = (SceItemRecebProvisorioId)other;
		return 
			this.nrpSeq.equals(castOther.nrpSeq)
			&& this.nroItem.equals(castOther.nroItem);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.nrpSeq.hashCode();
		hash = hash * prime + this.nroItem.hashCode();
		
		return hash;
    }
}