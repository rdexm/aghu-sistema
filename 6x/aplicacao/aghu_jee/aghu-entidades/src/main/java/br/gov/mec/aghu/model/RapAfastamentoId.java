package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class RapAfastamentoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6922487087699654266L;

	private Integer serMatricula;

	private Integer serVinCodigo;

	private Integer sequencia;

	// getters & setters

	@Column(name = "SER_MATRICULA", length = 7, nullable = false)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", length = 3, nullable = false)
	public Integer getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Integer serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "SEQUENCIA", length = 3, nullable = false)
	public Integer getSequencia() {
		return this.sequencia;
	}

	public void setSequencia(Integer sequencia) {
		this.sequencia = sequencia;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("serMatricula",
				this.serMatricula).append("serVinCodigo", this.serVinCodigo)
				.append("sequencia", this.sequencia).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof RapAfastamentoId)) {
			return false;
		}
		RapAfastamentoId castOther = (RapAfastamentoId) other;
		return new EqualsBuilder().append(this.serMatricula,
				castOther.getSerMatricula()).append(this.serVinCodigo,
				castOther.getSerVinCodigo()).append(this.sequencia,
				castOther.getSequencia()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.serMatricula).append(
				this.serVinCodigo).append(this.sequencia).toHashCode();
	}
}
