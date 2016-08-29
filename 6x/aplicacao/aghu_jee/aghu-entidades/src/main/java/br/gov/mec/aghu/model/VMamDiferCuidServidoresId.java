package br.gov.mec.aghu.model;


import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class VMamDiferCuidServidoresId implements EntityCompositeId {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 8147003902059777479L;
	private Short vinCodigo;	
	private Integer matricula;
	private String pesNome;		


	public VMamDiferCuidServidoresId() {
	}

	public VMamDiferCuidServidoresId(Short vinCodigo, String pesNome, Integer matricula) {
		this.vinCodigo = vinCodigo;	
		this.pesNome = pesNome;
		this.matricula = matricula;
			
	}
	

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getVinCodigo() {
		return this.vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	@Column(name = "SER_MATRICULA", nullable = false, length = 60)
	public Integer getMatricula() {
		return this.matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	


	@Column(name = "PES_NOME", length = 60)
	public String getPesNome() {
		return this.pesNome;
	}
	
	public void setPesNome(String pesNome) {
		this.pesNome = pesNome;
	}	
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();	
		umHashCodeBuilder.append(this.getVinCodigo());
		umHashCodeBuilder.append(this.getPesNome());
		umHashCodeBuilder.append(this.getMatricula());
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
		if (!(obj instanceof VMamDiferCuidServidoresId)) {
			return false;
		}
		VMamDiferCuidServidoresId other = (VMamDiferCuidServidoresId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getVinCodigo(), other.getVinCodigo());
		umEqualsBuilder.append(this.getPesNome(), other.getPesNome());
		umEqualsBuilder.append(this.getMatricula(), other.getMatricula());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
