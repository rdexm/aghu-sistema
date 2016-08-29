package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.OrderBy;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class ScoItemPropostaFornecedorId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5209741934945540078L;
	private Integer pfrLctNumero;
	private Integer pfrFrnNumero;
	private Short numero;

	// construtores
	public ScoItemPropostaFornecedorId() {
	}

	public ScoItemPropostaFornecedorId(Integer pfrLctNumero,
			Integer pfrFrnNumero, Short numero) {
		this.pfrLctNumero = pfrLctNumero;
		this.pfrFrnNumero = pfrFrnNumero;
		this.numero = numero;
	}

	// getters & setters

	@Column(name = "PFR_LCT_NUMERO", length = 7, nullable = false)
	public Integer getPfrLctNumero() {
		return this.pfrLctNumero;
	}

	public void setPfrLctNumero(Integer pfrLctNumero) {
		this.pfrLctNumero = pfrLctNumero;
	}

	@Column(name = "PFR_FRN_NUMERO", length = 5, nullable = false)
	public Integer getPfrFrnNumero() {
		return this.pfrFrnNumero;
	}

	public void setPfrFrnNumero(Integer pfrFrnNumero) {
		this.pfrFrnNumero = pfrFrnNumero;
	}

	@Column(name = "NUMERO", length = 3, nullable = false)
	@OrderBy("numero")
	public Short getNumero() {
		return this.numero;
	}

	public void setNumero(Short numero) {
		this.numero = numero;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("pfrLctNumero", this.pfrLctNumero)
				.append("pfrFrnNumero", this.pfrFrnNumero)
				.append("numero", this.numero).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoItemPropostaFornecedorId)){
			return false;
		}
		ScoItemPropostaFornecedorId castOther = (ScoItemPropostaFornecedorId) other;
		return new EqualsBuilder()
				.append(this.pfrLctNumero, castOther.getPfrLctNumero())
				.append(this.pfrFrnNumero, castOther.getPfrFrnNumero())
				.append(this.numero, castOther.getNumero()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.pfrLctNumero)
				.append(this.pfrFrnNumero).append(this.numero).toHashCode();
	}
}
