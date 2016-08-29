package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "AEL_REF_CODES", schema = "AGH")
public class AelRefCode extends BaseEntityId<AelRefCodeId> implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1121065120802540135L;
	private AelRefCodeId id;
	private String rvHighValue;
	private String rvAbbreviation;
	private String rvMeaning;
	private String rvType;
	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "rvLowValue", column = @Column(name = "RV_LOW_VALUE", nullable = false, length = 240)),
			@AttributeOverride(name = "rvDomain", column = @Column(name = "RV_DOMAIN", nullable = false, length = 100)) })
	public AelRefCodeId getId() {
		return id;
	}
	
	public void setId(AelRefCodeId id) {
		this.id = id;
	}
	
	@Column(name = "RV_HIGH_VALUE", nullable = true, length = 240)
	public String getRvHighValue() {
		return rvHighValue;
	}
	
	public void setRvHighValue(String rvHighValue) {
		this.rvHighValue = rvHighValue;
	}
	
	@Column(name = "RV_ABBREVIATION", nullable = true, length = 240)
	public String getRvAbbreviation() {
		return rvAbbreviation;
	}
	
	public void setRvAbbreviation(String rvAbbreviation) {
		this.rvAbbreviation = rvAbbreviation;
	}
	
	@Column(name = "RV_MEANING", nullable = true, length = 240)
	public String getRvMeaning() {
		return rvMeaning;
	}
	
	public void setRvMeaning(String rvMeaning) {
		this.rvMeaning = rvMeaning;
	}
	
	@Column(name = "RV_TYPE", nullable = true, length = 10)
	public String getRvType() {
		return rvType;
	}
	
	public void setRvType(String rvType) {
		this.rvType = rvType;
	}
	
	public enum Fields {
		
		RV_LOW_VALUE("id.rvLowValue"),
		RV_HIGH_VALUE("rvHighValue"),
		RV_ABBREVIATION("rvAbbreviation"),
		RV_DOMAIN("id.rvDomain"),
		RV_MEANING("rvMeaning"),
		RV_TYPE("rvType");

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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AelRefCode)) {
			return false;
		}
		AelRefCode other = (AelRefCode) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
