package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class ScoNomeComercialId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1216469637603496614L;
	private Integer mcmCodigo;
	private Integer numero;

	// construtores
	public ScoNomeComercialId() {
	}

	public ScoNomeComercialId(Integer mcmCodigo, Integer numero) {
		this.mcmCodigo = mcmCodigo;
		this.numero = numero;
	}

	// getters & setters

	@Column(name = "MCM_CODIGO", length = 6, nullable = false)
	public Integer getMcmCodigo() {
		return this.mcmCodigo;
	}

	public void setMcmCodigo(Integer mcmCodigo) {
		this.mcmCodigo = mcmCodigo;
	}

	@Column(name = "NUMERO", length = 5, nullable = false)
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("mcmCodigo", this.mcmCodigo)
				.append("numero", this.numero).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoNomeComercialId)){
			return false;
		}
		ScoNomeComercialId castOther = (ScoNomeComercialId) other;
		return new EqualsBuilder()
				.append(this.mcmCodigo, castOther.getMcmCodigo())
				.append(this.numero, castOther.getNumero()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.mcmCodigo).append(this.numero)
				.toHashCode();
	}
}
