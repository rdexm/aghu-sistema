package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class VAelSerSismamaId implements EntityCompositeId {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3203901506402298368L;
	private Integer vinCodigo;
	private Integer matricula;
	private String nome;
	private String sigla;
	private String nroRegConselho;
	
	public VAelSerSismamaId() {
	}
	
	public VAelSerSismamaId(Integer vinCodigo, Integer matricula, 
			String nome, String sigla, String nroRegConselho) {
		this.vinCodigo = vinCodigo; 
		this.matricula = matricula; 
		this.nome = nome;
		this.sigla = sigla; 
		this.nroRegConselho = nroRegConselho;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	@Column(name = "NOME", length = 50)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "SIGLA_CONSELHO", length = 15)
	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "NRO_REG_CONSELHO", length = 9)
	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getNome());
		umHashCodeBuilder.append(this.getSigla());
		umHashCodeBuilder.append(this.getMatricula());
		umHashCodeBuilder.append(this.getVinCodigo());
		umHashCodeBuilder.append(this.getNroRegConselho());
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
		if (!(obj instanceof VAelSerSismamaId)) {
			return false;
		}
		VAelSerSismamaId other = (VAelSerSismamaId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getNome(), other.getNome());
		umEqualsBuilder.append(this.getSigla(), other.getSigla());
		umEqualsBuilder.append(this.getMatricula(), other.getMatricula());
		umEqualsBuilder.append(this.getVinCodigo(), other.getVinCodigo());
		umEqualsBuilder.append(this.getNroRegConselho(), other.getNroRegConselho());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
