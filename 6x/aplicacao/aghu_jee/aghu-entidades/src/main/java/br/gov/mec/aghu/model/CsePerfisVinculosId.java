package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable()
public class CsePerfisVinculosId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 81437263378838240L;
	private String perNome;
	private Short vinCodigo;

	// construtores
	public CsePerfisVinculosId() {
	}

	public CsePerfisVinculosId(String perNome, Short vinCodigo) {
		this.perNome = perNome;
		this.vinCodigo = vinCodigo;
	}
	
	@Column(name = "PER_NOME", length = 30, nullable = false)
	public String getPerNome() {
		return this.perNome;
	}
	
	public void setPerNome(String perNome) {
		this.perNome = perNome;
	}
		
	@Column(name = "VIN_CODIGO", length = 3, nullable = false)
	public Short getVinCodigo() {
		return this.vinCodigo;
	}
	
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}		

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("perNome", this.perNome)
				.append("vinCodigo", this.vinCodigo).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CsePerfisVinculosId)) {
			return false;
		}
		CsePerfisVinculosId castOther = (CsePerfisVinculosId) other;
		return new EqualsBuilder().append(this.perNome, castOther.getPerNome())
				.append(this.vinCodigo, castOther.getVinCodigo()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.perNome)
				.append(this.vinCodigo).toHashCode();
	}
}
