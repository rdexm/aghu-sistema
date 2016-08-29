

package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class PtmBemProcessoId implements EntityCompositeId{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4055074027650558692L;
	/**
	 * 
	 */
	
	private Long bpeSeq;
	private Long proSeq;
	
	@Column(name = "BPE_SEQ", nullable=false)
	public Long getBpeSeq() {
		return bpeSeq;
	}
	public void setBpeSeq(Long bpeSeq) {
		this.bpeSeq = bpeSeq;
	}
	@Column(name = "PRO_SEQ",  nullable=false)
	public Long getProSeq() {
		return proSeq;
	}
	public void setProSeq(Long proSeq) {
		this.proSeq = proSeq;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getBpeSeq());
		umHashCodeBuilder.append(this.getProSeq());
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
		PtmBemProcessoId other = (PtmBemProcessoId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getBpeSeq(), other.getBpeSeq());	
		umEqualsBuilder.append(this.getProSeq(), other.getProSeq());
		return umEqualsBuilder.isEquals();
	}
}