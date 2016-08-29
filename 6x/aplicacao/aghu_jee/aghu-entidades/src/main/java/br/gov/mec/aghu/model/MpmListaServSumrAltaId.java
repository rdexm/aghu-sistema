package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class MpmListaServSumrAltaId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4019562303230312867L;

	private Integer serMatricula;

	private Short serVinCodigo;

	private Integer atdSeq;

	// construtores
	
	public MpmListaServSumrAltaId() {
	}

	public MpmListaServSumrAltaId(Integer serMatricula, Short serVinCodigo,
			Integer atdSeq) {
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.atdSeq = atdSeq;
	}

	// getters & setters

	@Column(name = "SER_MATRICULA", length = 7, nullable = false)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", length = 3, nullable = false)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "ATD_SEQ", length = 7, nullable = false)
	public Integer getAtdSeq() {
		return this.atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("serMatricula", this.serMatricula)
				.append("serVinCodigo", this.serVinCodigo)
				.append("atdSeq", this.atdSeq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmListaServSumrAltaId)) {
			return false;
		}
		MpmListaServSumrAltaId castOther = (MpmListaServSumrAltaId) other;
		return new EqualsBuilder()
				.append(this.serMatricula, castOther.getSerMatricula())
				.append(this.serVinCodigo, castOther.getSerVinCodigo())
				.append(this.atdSeq, castOther.getAtdSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.serMatricula)
				.append(this.serVinCodigo).append(this.atdSeq).toHashCode();
	}
}
