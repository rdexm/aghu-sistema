package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class ScoSociosFornecedoresId implements EntityCompositeId {
	private static final long serialVersionUID = 165756754723454L;

	private Integer socioSeq;
	private Integer fornecedorNumero;
	
	public ScoSociosFornecedoresId(){
	}
	
	public ScoSociosFornecedoresId(Integer socioSeq, Integer fornecedorNumero){
		this.socioSeq = socioSeq;
		this.fornecedorNumero = fornecedorNumero;
	}
	
	@Column(name = "SOC_SEQ", nullable = false)
	public Integer getSocioSeq() {
		return socioSeq;
	}
	
	public void setSocioSeq(Integer socioSeq) {
		this.socioSeq = socioSeq;
	}
	
	@Column(name = "FRN_NUMERO", nullable = false)
	public Integer getFornecedorNumero() {
		return fornecedorNumero;
	}
	
	public void setFornecedorNumero(Integer fornecedorNumero) {
		this.fornecedorNumero = fornecedorNumero;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSocioSeq());
		umHashCodeBuilder.append(this.getFornecedorNumero());
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ScoSociosFornecedoresId)) {
			return false;
		}
		ScoSociosFornecedoresId other = (ScoSociosFornecedoresId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSocioSeq(), other.getSocioSeq());
		umEqualsBuilder.append(this.getFornecedorNumero(), other.getFornecedorNumero());
		return umEqualsBuilder.isEquals();
	}
	
}
