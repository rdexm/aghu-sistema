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
 * AfaEscalaProducaoFarmaciaId generated by hbm2java
 */
@Embeddable
public class AfaEscalaProducaoFarmaciaId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2080925448283755927L;
	private Short unfSeq;
	private String tipo;
	private Date data;
	private String turno;

	public AfaEscalaProducaoFarmaciaId() {
	}

	public AfaEscalaProducaoFarmaciaId(Short unfSeq, String tipo, Date data, String turno) {
		this.unfSeq = unfSeq;
		this.tipo = tipo;
		this.data = data;
		this.turno = turno;
	}

	@Column(name = "UNF_SEQ", nullable = false)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "TIPO", nullable = false, length = 1)
	@Length(max = 1)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Column(name = "DATA", nullable = false, length = 29)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Column(name = "TURNO", nullable = false, length = 1)
	@Length(max = 1)
	public String getTurno() {
		return this.turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getData());
		umHashCodeBuilder.append(this.getUnfSeq());
		umHashCodeBuilder.append(this.getTipo());
		umHashCodeBuilder.append(this.getTurno());
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
		if (!(obj instanceof AfaEscalaProducaoFarmaciaId)) {
			return false;
		}
		AfaEscalaProducaoFarmaciaId other = (AfaEscalaProducaoFarmaciaId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getData(), other.getData());
		umEqualsBuilder.append(this.getUnfSeq(), other.getUnfSeq());
		umEqualsBuilder.append(this.getTipo(), other.getTipo());
		umEqualsBuilder.append(this.getTurno(), other.getTurno());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
