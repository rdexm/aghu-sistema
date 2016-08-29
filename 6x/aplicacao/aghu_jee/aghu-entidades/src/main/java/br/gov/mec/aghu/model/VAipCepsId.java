package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Embeddable
public class VAipCepsId implements java.io.Serializable {

	private static final long serialVersionUID = -3584985891273564657L;
	
	private Integer cep;
	private String rowId;

	public VAipCepsId() {
	}

	public VAipCepsId(Integer cep, String rowId) {
		this.cep = cep;
		this.rowId = rowId;
	}

	@Column(name = "CEP")
	public Integer getCep() {
		return this.cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}

	
	@Column(name = "ROW_ID")
	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		
		umHashCodeBuilder.append(this.getCep());
		umHashCodeBuilder.append(this.getRowId());
		
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
		if (!(obj instanceof VAipCepsId)) {
			return false;
		}
		
		VAipCepsId other = (VAipCepsId) obj;
		
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCep(), other.getCep());
		umEqualsBuilder.append(this.getRowId(), other.getRowId());
		
		return umEqualsBuilder.isEquals();
	}
}
