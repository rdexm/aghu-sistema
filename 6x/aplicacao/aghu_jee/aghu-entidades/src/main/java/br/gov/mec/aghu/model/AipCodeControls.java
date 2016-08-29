package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntity;

@Entity
@Table(name = "AIP_CODE_CONTROLS", schema = "AGH")
public class AipCodeControls implements BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3455225780791555855L;
	private String domain;
	private String comment;
	private Integer nextValue;

	public AipCodeControls() {
	}

	@Id
	@Column(name = "CC_DOMAIN", nullable = false, length = 30)
	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Column(name = "CC_COMMENT", length = 240)
	@Length(max = 240)
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "CC_NEXT_VALUE", nullable = false, precision = 15, scale = 0)
	public Integer getNextValue() {
		return this.nextValue;
	}

	public void setNextValue(Integer nextValue) {
		this.nextValue = nextValue;
	}

	public enum Fields {
		DOMAIN("domain"),
		COMMENT("comment"),
		NEXT_VALUE("nextValue")
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getDomain() == null) ? 0 : getDomain().hashCode());
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
		if (!(obj instanceof AipCodeControls)) {
			return false;
		}
		AipCodeControls other = (AipCodeControls) obj;
		if (getDomain() == null) {
			if (other.getDomain() != null) {
				return false;
			}
		} else if (!getDomain().equals(other.getDomain())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}