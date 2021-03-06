package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * MamTipoAtestadoProcessoId generated by hbm2java
 */
@Embeddable
public class MamTipoAtestadoProcessoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8966407535281048407L;
	private Short tasSeq;
	private Short rocSeq;

	public MamTipoAtestadoProcessoId() {
	}

	public MamTipoAtestadoProcessoId(Short tasSeq, Short rocSeq) {
		this.tasSeq = tasSeq;
		this.rocSeq = rocSeq;
	}

	@Column(name = "TAS_SEQ", nullable = false)
	public Short getTasSeq() {
		return this.tasSeq;
	}

	public void setTasSeq(Short tasSeq) {
		this.tasSeq = tasSeq;
	}

	@Column(name = "ROC_SEQ", nullable = false)
	public Short getRocSeq() {
		return this.rocSeq;
	}

	public void setRocSeq(Short rocSeq) {
		this.rocSeq = rocSeq;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getRocSeq());
		umHashCodeBuilder.append(this.getTasSeq());
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
		if (!(obj instanceof MamTipoAtestadoProcessoId)) {
			return false;
		}
		MamTipoAtestadoProcessoId other = (MamTipoAtestadoProcessoId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getRocSeq(), other.getRocSeq());
		umEqualsBuilder.append(this.getTasSeq(), other.getTasSeq());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
