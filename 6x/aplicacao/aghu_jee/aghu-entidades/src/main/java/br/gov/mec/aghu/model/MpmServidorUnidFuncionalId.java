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
public class MpmServidorUnidFuncionalId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7744297300732276986L;

	private Integer serMatricula;

	private Short serVinCodigo;

	private Short unfSeq;

	// construtores

	public MpmServidorUnidFuncionalId() {
	}

	public MpmServidorUnidFuncionalId(Integer serMatricula,
			Short serVinCodigo, Short unfSeq) {
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.unfSeq = unfSeq;
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

	@Column(name = "UNF_SEQ", length = 4, nullable = false)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("serMatricula", this.serMatricula)
				.append("serVinCodigo", this.serVinCodigo)
				.append("unfSeq", this.unfSeq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmServidorUnidFuncionalId)) {
			return false;
		}
		MpmServidorUnidFuncionalId castOther = (MpmServidorUnidFuncionalId) other;
		return new EqualsBuilder()
				.append(this.serMatricula, castOther.getSerMatricula())
				.append(this.serVinCodigo, castOther.getSerVinCodigo())
				.append(this.unfSeq, castOther.getUnfSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.serMatricula)
				.append(this.serVinCodigo).append(this.unfSeq).toHashCode();
	}
}
