package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class ScoPropostaFornecedorId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5826560617052017715L;
	private Integer lctNumero;
	private Integer frnNumero;

	// construtores
	public ScoPropostaFornecedorId() {
	}

	public ScoPropostaFornecedorId(Integer lctNumero, Integer frnNumero) {
		this.lctNumero = lctNumero;
		this.frnNumero = frnNumero;
	}

	// getters & setters

	@Column(name = "LCT_NUMERO", length = 7, nullable = false)
	public Integer getLctNumero() {
		return this.lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	@Column(name = "FRN_NUMERO", length = 5, nullable = false)
	public Integer getFrnNumero() {
		return this.frnNumero;
	}

	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("lctNumero", this.lctNumero)
				.append("frnNumero", this.frnNumero).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoPropostaFornecedorId)){
			return false;
		}
		ScoPropostaFornecedorId castOther = (ScoPropostaFornecedorId) other;
		return new EqualsBuilder()
				.append(this.lctNumero, castOther.getLctNumero())
				.append(this.frnNumero, castOther.getFrnNumero()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.lctNumero)
				.append(this.frnNumero).toHashCode();
	}
}
