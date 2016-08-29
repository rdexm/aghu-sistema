package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MciCriterioGmrId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4434303976807593085L;
	
	private Integer bmrSeq;
	private Integer ambSeq;

	public MciCriterioGmrId() {
	}

	public MciCriterioGmrId(Integer bmrSeq, Integer ambSeq) {
		this.bmrSeq = bmrSeq;
		this.ambSeq = ambSeq;
	}

	@Column(name = "BMR_SEQ", nullable = false)
	public Integer getBmrSeq() {
		return this.bmrSeq;
	}

	public void setBmrSeq(Integer bmrSeq) {
		this.bmrSeq = bmrSeq;
	}

	@Column(name = "AMB_SEQ", nullable = false)
	public Integer getAmbSeq() {
		return this.ambSeq;
	}

	public void setAmbSeq(Integer ambSeq) {
		this.ambSeq = ambSeq;
	}
	
	public enum Fields {

		BMR_SEQ("bmrSeq"),
		AMB_SEQ("ambSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	// ##### GeradorEqualsHashCodeMain #####
		@Override
		public int hashCode() {
			HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
			umHashCodeBuilder.append(this.getAmbSeq());
			umHashCodeBuilder.append(this.getBmrSeq());
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
			if (!(obj instanceof MciCriterioGmrId)) {
				return false;
			}
			MciCriterioGmrId other = (MciCriterioGmrId) obj;
			EqualsBuilder umEqualsBuilder = new EqualsBuilder();
			umEqualsBuilder.append(this.getAmbSeq(), other.getAmbSeq());
			umEqualsBuilder.append(this.getBmrSeq(), other.getBmrSeq());
			return umEqualsBuilder.isEquals();
		}
		// ##### GeradorEqualsHashCodeMain #####

}
