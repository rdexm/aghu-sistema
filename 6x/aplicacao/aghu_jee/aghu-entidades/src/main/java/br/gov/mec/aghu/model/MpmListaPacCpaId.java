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
public class MpmListaPacCpaId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9040837353754420802L;
	private Integer serMatricula;
	private Short serVinCodigo;

	// construtores

	public MpmListaPacCpaId() {
	}

	public MpmListaPacCpaId(final Integer serMatricula, final Short serVinCodigo) {
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	// getters & setters

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(final Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(final Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("serMatricula", this.serMatricula).append("serVinCodigo", this.serVinCodigo).toString();
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof MpmListaPacCpaId)) {
			return false;
		}
		MpmListaPacCpaId castOther = (MpmListaPacCpaId) other;
		return new EqualsBuilder().append(this.serMatricula, castOther.getSerMatricula()).append(this.serVinCodigo, castOther.getSerVinCodigo())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.serMatricula).append(this.serVinCodigo).toHashCode();
	}

}
