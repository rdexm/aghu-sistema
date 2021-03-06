package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * AghTesteId generated by hbm2java
 */
@Embeddable
public class AghTesteId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4954656987676560113L;
	private Integer codigo;
	private String nome;
	private Integer version;

	public AghTesteId() {
	}

	public AghTesteId(Integer version) {
		this.version = version;
	}

	public AghTesteId(Integer codigo, String nome, Integer version) {
		this.codigo = codigo;
		this.nome = nome;
		this.version = version;
	}

	@Column(name = "CODIGO")
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "NOME", length = 10)
	@Length(max = 10)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getVersion());
		umHashCodeBuilder.append(this.getCodigo());
		umHashCodeBuilder.append(this.getNome());
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
		if (!(obj instanceof AghTesteId)) {
			return false;
		}
		AghTesteId other = (AghTesteId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getVersion(), other.getVersion());
		umEqualsBuilder.append(this.getCodigo(), other.getCodigo());
		umEqualsBuilder.append(this.getNome(), other.getNome());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
