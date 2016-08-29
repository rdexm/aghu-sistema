package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class ScoLoteLicitacaoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8101664173560944897L;
	private Integer lctNumero;
	private Short numero;

	// construtores
	public ScoLoteLicitacaoId() {
	}

	public ScoLoteLicitacaoId(Integer lctNumero, Short numero) {
		this.lctNumero = lctNumero;
		this.numero = numero;
	}

	// getters & setters

	@Column(name = "LCT_NUMERO", length = 7, nullable = false)
	public Integer getLctNumero() {
		return this.lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	@Column(name = "NUMERO", length = 3, nullable = false)
	public Short getNumero() {
		return this.numero;
	}

	public void setNumero(Short numero) {
		this.numero = numero;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("lctNumero", this.lctNumero)
				.append("numero", this.numero).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoLoteLicitacaoId)){
			return false;
		}
		ScoLoteLicitacaoId castOther = (ScoLoteLicitacaoId) other;
		return new EqualsBuilder()
				.append(this.lctNumero, castOther.getLctNumero())
				.append(this.numero, castOther.getNumero()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.lctNumero).append(this.numero)
				.toHashCode();
	}
}
