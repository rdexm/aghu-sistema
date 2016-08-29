package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * AbsAnticorpoPacienteId generated by hbm2java
 */
@Embeddable
public class AbsAnticorpoPacienteId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2474976015968096827L;
	private Integer rspPacCodigo;
	private Short antSeq;
	private Date rspDthrRegistro;

	public AbsAnticorpoPacienteId() {
	}

	public AbsAnticorpoPacienteId(Integer rspPacCodigo, Short antSeq, Date rspDthrRegistro) {
		this.rspPacCodigo = rspPacCodigo;
		this.antSeq = antSeq;
		this.rspDthrRegistro = rspDthrRegistro;
	}

	@Column(name = "RSP_PAC_CODIGO", nullable = false)
	public Integer getRspPacCodigo() {
		return this.rspPacCodigo;
	}

	public void setRspPacCodigo(Integer rspPacCodigo) {
		this.rspPacCodigo = rspPacCodigo;
	}

	@Column(name = "ANT_SEQ", nullable = false)
	public Short getAntSeq() {
		return this.antSeq;
	}

	public void setAntSeq(Short antSeq) {
		this.antSeq = antSeq;
	}

	@Column(name = "RSP_DTHR_REGISTRO", nullable = false, length = 29)
	public Date getRspDthrRegistro() {
		return this.rspDthrRegistro;
	}

	public void setRspDthrRegistro(Date rspDthrRegistro) {
		this.rspDthrRegistro = rspDthrRegistro;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getAntSeq());
		umHashCodeBuilder.append(this.getRspPacCodigo());
		umHashCodeBuilder.append(this.getRspDthrRegistro());
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
		if (!(obj instanceof AbsAnticorpoPacienteId)) {
			return false;
		}
		AbsAnticorpoPacienteId other = (AbsAnticorpoPacienteId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getAntSeq(), other.getAntSeq());
		umEqualsBuilder.append(this.getRspPacCodigo(), other.getRspPacCodigo());
		umEqualsBuilder.append(this.getRspDthrRegistro(), other.getRspDthrRegistro());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}