package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sce_item_nrs database table.
 * 
 */
@Embeddable
public class SceItemNotaRecebimentoIdHist implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7655297310193764354L;
	private Integer nrsSeq;
	private Integer afnNumero;
	private Integer itemNumero;

    public SceItemNotaRecebimentoIdHist() {
    }
    
    @Column(name="NRS_SEQ")
	public Integer getNrsSeq() {
		return this.nrsSeq;
	}

	public void setNrsSeq(Integer sceNotaRecebimento) {
		this.nrsSeq = sceNotaRecebimento;
	}

	@Column(name="IAF_AFN_NUMERO")
	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	@Column(name="IAF_NUMERO")
	public Integer getItemNumero() {
		return itemNumero;
	}

	public void setItemNumero(Integer itemNumero) {
		this.itemNumero = itemNumero;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SceItemNotaRecebimentoIdHist)) {
			return false;
		}
		SceItemNotaRecebimentoIdHist castOther = (SceItemNotaRecebimentoIdHist)other;

		return this.nrsSeq.equals(castOther.nrsSeq) && this.afnNumero.equals(castOther.afnNumero) && this.itemNumero.equals(castOther.itemNumero);
    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.nrsSeq.hashCode();
		hash = hash * prime + this.afnNumero.hashCode();
		hash = hash * prime + this.itemNumero.hashCode();
		
		return hash;
    }
}