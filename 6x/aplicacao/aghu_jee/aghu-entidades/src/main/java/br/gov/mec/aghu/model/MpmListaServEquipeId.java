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
public class MpmListaServEquipeId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5078536519315871331L;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short eqpSeq;

	// construtores

	public MpmListaServEquipeId() {
	}

	public MpmListaServEquipeId(Integer serMatricula, Short serVinCodigo,
			Short eqpSeq) {
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.eqpSeq = eqpSeq;
	}

	// getters & setters

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

	@Column(name = "EQP_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getEqpSeq() {
		return this.eqpSeq;
	}

	public void setEqpSeq(Short eqpSeq) {
		this.eqpSeq = eqpSeq;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("serMatricula", this.serMatricula)
				.append("serVinCodigo", this.serVinCodigo)
				.append("eqpSeq", this.eqpSeq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmListaServEquipeId)) {
			return false;
		}
		MpmListaServEquipeId castOther = (MpmListaServEquipeId) other;
		return new EqualsBuilder()
				.append(this.serMatricula, castOther.getSerMatricula())
				.append(this.serVinCodigo, castOther.getSerVinCodigo())
				.append(this.eqpSeq, castOther.getEqpSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.serMatricula)
				.append(this.serVinCodigo).append(this.eqpSeq).toHashCode();
	}

}
