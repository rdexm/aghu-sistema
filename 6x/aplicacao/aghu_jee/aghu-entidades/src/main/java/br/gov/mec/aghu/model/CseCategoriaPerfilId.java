package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class CseCategoriaPerfilId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4868134980542129902L;

	private String perNome;

	private Short cagSeq;

	// construtores
	public CseCategoriaPerfilId() {
	}

	public CseCategoriaPerfilId(String perNome, Short cagSeq) {
		this.perNome = perNome;
		this.cagSeq = cagSeq;
	}

	// getters & setters

	@Column(name = "PER_NOME", length = 30, updatable = false, nullable = false)
	public String getPerNome() {
		return this.perNome;
	}

	public void setPerNome(String perNome) {
		this.perNome = perNome;
	}

	@Column(name = "CAG_SEQ", length = 4, nullable = false)
	public Short getCagSeq() {
		return this.cagSeq;
	}

	public void setCagSeq(Short cagSeq) {
		this.cagSeq = cagSeq;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("perNome", this.perNome)
				.append("cagSeq", this.cagSeq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CseCategoriaPerfilId)) {
			return false;
		}
		CseCategoriaPerfilId castOther = (CseCategoriaPerfilId) other;
		return new EqualsBuilder().append(this.perNome, castOther.getPerNome())
				.append(this.cagSeq, castOther.getCagSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.perNome).append(this.cagSeq)
				.toHashCode();
	}
}