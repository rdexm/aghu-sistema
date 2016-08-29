package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class AelRefCodeId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8185754926141901197L;
	private String rvLowValue;
	private String rvDomain;

	public AelRefCodeId() {
	}

	public AelRefCodeId(String rvLowValue, String rvDomain) {
		this.rvLowValue = rvLowValue;
		this.rvDomain = rvDomain;
	}

	
	@Column(name = "RV_LOW_VALUE", nullable = false, length = 240)
	@Length(max = 240)
	public String getRvLowValue() {
		return this.rvLowValue;
	}

	public void setRvLowValue(String rvLowValue) {
		this.rvLowValue = rvLowValue;
	}
	
	@Column(name = "RV_DOMAIN", nullable = false, length = 100)
	@Length(max = 100)
	public String getRvDomain() {
		return this.rvDomain;
	}

	public void setRvDomain(String rvDomain) {
		this.rvDomain = rvDomain;
	}
	
	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof AelRefCodeId)) {
			return false;
		}
		AelRefCodeId castOther = (AelRefCodeId) other;

		return ((this.getRvLowValue() == castOther.getRvLowValue()) || (this
				.getRvLowValue() != null
				&& castOther.getRvLowValue() != null && this.getRvLowValue()
				.equals(castOther.getRvLowValue())))
				&& ((this.getRvDomain() == castOther.getRvDomain()) || (this
						.getRvDomain() != null
						&& castOther.getRvDomain() != null && this
						.getRvDomain().equals(castOther.getRvDomain())));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getRvLowValue() == null ? 0 : this.getRvLowValue()
						.hashCode());
		result = 37 * result
				+ (getRvDomain() == null ? 0 : this.getRvDomain().hashCode());
		return result;
	}

}
