package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * AghDataLimpezaId generated by hbm2java
 */
@Embeddable
public class AghDataLimpezaId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5119845996027068112L;
	private String mesAnoRef;
	private Date dtInicial;
	private String sistema;

	public AghDataLimpezaId() {
	}

	public AghDataLimpezaId(String mesAnoRef, Date dtInicial, String sistema) {
		this.mesAnoRef = mesAnoRef;
		this.dtInicial = dtInicial;
		this.sistema = sistema;
	}

	@Column(name = "MES_ANO_REF", nullable = false, length = 6)
	@Length(max = 6)
	public String getMesAnoRef() {
		return this.mesAnoRef;
	}

	public void setMesAnoRef(String mesAnoRef) {
		this.mesAnoRef = mesAnoRef;
	}

	@Column(name = "DT_INICIAL", nullable = false, length = 29)
	public Date getDtInicial() {
		return this.dtInicial;
	}

	public void setDtInicial(Date dtInicial) {
		this.dtInicial = dtInicial;
	}

	@Column(name = "SISTEMA", nullable = false, length = 3)
	@Length(max = 3)
	public String getSistema() {
		return this.sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getMesAnoRef());
		umHashCodeBuilder.append(this.getDtInicial());
		umHashCodeBuilder.append(this.getSistema());
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
		if (!(obj instanceof AghDataLimpezaId)) {
			return false;
		}
		AghDataLimpezaId other = (AghDataLimpezaId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getMesAnoRef(), other.getMesAnoRef());
		umEqualsBuilder.append(this.getDtInicial(), other.getDtInicial());
		umEqualsBuilder.append(this.getSistema(), other.getSistema());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
