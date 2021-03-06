package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * ScoFornRamoComercialId generated by hbm2java
 */
@Embeddable
public class ScoFornRamoComercialId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1287593954093010356L;
	private Integer frnNumero;
	private Short rcmCodigo;

	public ScoFornRamoComercialId() {
	}

	public ScoFornRamoComercialId(Integer frnNumero, Short rcmCodigo) {
		this.frnNumero = frnNumero;
		this.rcmCodigo = rcmCodigo;
	}

	@Column(name = "FRN_NUMERO", nullable = false)
	public Integer getFrnNumero() {
		return this.frnNumero;
	}

	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}

	@Column(name = "RCM_CODIGO", nullable = false)
	public Short getRcmCodigo() {
		return this.rcmCodigo;
	}

	public void setRcmCodigo(Short rcmCodigo) {
		this.rcmCodigo = rcmCodigo;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getFrnNumero());
		umHashCodeBuilder.append(this.getRcmCodigo());
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
		if (!(obj instanceof ScoFornRamoComercialId)) {
			return false;
		}
		ScoFornRamoComercialId other = (ScoFornRamoComercialId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getFrnNumero(), other.getFrnNumero());
		umEqualsBuilder.append(this.getRcmCodigo(), other.getRcmCodigo());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
