package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MamUnidXGeralId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7057147753396255387L;
	private Integer itgSeq;
	private Short uanUnfSeq;

	public MamUnidXGeralId() {
	}

	public MamUnidXGeralId(Integer itgSeq, Short uanUnfSeq) {
		this.itgSeq = itgSeq;
		this.uanUnfSeq = uanUnfSeq;
	}
	
	@Column(name = "ITG_SEQ", nullable = false)
	public Integer getItgSeq() {
		return itgSeq;
	}

	public void setItgSeq(Integer itgSeq) {
		this.itgSeq = itgSeq;
	}
	
	@Column(name = "UAN_UNF_SEQ", nullable = false)
	public Short getUanUnfSeq() {
		return this.uanUnfSeq;
	}

	public void setUanUnfSeq(Short uanUnfSeq) {
		this.uanUnfSeq = uanUnfSeq;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getItgSeq());
		umHashCodeBuilder.append(this.getUanUnfSeq());
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
		if (!(obj instanceof MamUnidXGeralId)) {
			return false;
		}
		MamUnidXGeralId other = (MamUnidXGeralId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getItgSeq(), other.getItgSeq());
		umEqualsBuilder.append(this.getUanUnfSeq(), other.getUanUnfSeq());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
