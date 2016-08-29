package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * 
 */

@Embeddable
public class MpmPacAtendProfissionalId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -465409046353383059L;
	private Integer atdSeq;
	private Integer serMatricula;
	private Short serVinCodigo;

	// construtores

	public MpmPacAtendProfissionalId() {
	}

	public MpmPacAtendProfissionalId(Integer atdSeq, Integer serMatricula,
			Short serVinCodigo) {
		this.atdSeq = atdSeq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	// getters & setters

	@Column(name = "ATD_SEQ", nullable = false, precision = 7, scale = 0)
	public Integer getAtdSeq() {
		return this.atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("atdSeq", this.atdSeq)
				.append("serMatricula", this.serMatricula)
				.append("serVinCodigo", this.serVinCodigo).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmPacAtendProfissionalId)) {
			return false;
		}
		MpmPacAtendProfissionalId castOther = (MpmPacAtendProfissionalId) other;
		return new EqualsBuilder().append(this.atdSeq, castOther.getAtdSeq())
				.append(this.serMatricula, castOther.getSerMatricula())
				.append(this.serVinCodigo, castOther.getSerVinCodigo())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.atdSeq)
				.append(this.serMatricula).append(this.serVinCodigo)
				.toHashCode();
	}

}
