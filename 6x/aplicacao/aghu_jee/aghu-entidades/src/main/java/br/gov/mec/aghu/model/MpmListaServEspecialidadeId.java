package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;
/**
 * 
 * @author cvagheti
 *
 */

@Embeddable()
public class MpmListaServEspecialidadeId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6745557694640742914L;

	private Integer serMatricula;

	private Short serVinCodigo;

	private Short espSeq;

	// contrutores

	public MpmListaServEspecialidadeId() {

	}

	public MpmListaServEspecialidadeId(Integer serMatricula,
			Short serVinCodigo, Short espSeq) {
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.espSeq = espSeq;
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

	@Column(name = "ESP_SEQ", length = 4, nullable = false)
	public Short getEspSeq() {
		return this.espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("serMatricula", this.serMatricula)
				.append("serVinCodigo", this.serVinCodigo)
				.append("espSeq", this.espSeq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmListaServEspecialidadeId)) {
			return false;
		}
		MpmListaServEspecialidadeId castOther = (MpmListaServEspecialidadeId) other;
		return new EqualsBuilder()
				.append(this.serMatricula, castOther.getSerMatricula())
				.append(this.serVinCodigo, castOther.getSerVinCodigo())
				.append(this.espSeq, castOther.getEspSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.serMatricula)
				.append(this.serVinCodigo).append(this.espSeq).toHashCode();
	}
}
