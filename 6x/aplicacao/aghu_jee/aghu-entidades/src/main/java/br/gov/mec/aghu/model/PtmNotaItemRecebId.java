
package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class PtmNotaItemRecebId implements EntityCompositeId {
	
	private static final long serialVersionUID = 6745321533796078607L;

	private Long notSeq;
	private Long irpSeq;
	
	@Column(name = "NOT_SEQ", nullable = false)
	public Long getNotSeq() {
		return notSeq;
	}
	public void setNotSeq(Long notSeq) {
		this.notSeq = notSeq;
	}
	@Column(name = "IRP_SEQ", nullable = false)
	public Long getIrpSeq() {
		return irpSeq;
	}
	public void setIrpSeq(Long irpSeq) {
		this.irpSeq = irpSeq;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getNotSeq());
		umHashCodeBuilder.append(this.getIrpSeq());
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PtmNotaItemRecebId)) {
			return false;
		}
		PtmNotaItemRecebId other = (PtmNotaItemRecebId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getIrpSeq(), other.getIrpSeq());	
		umEqualsBuilder.append(this.getNotSeq(), other.getNotSeq());
		return umEqualsBuilder.isEquals();
	}
	
}
