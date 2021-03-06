package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * UnidadeId generated by hbm2java
 */
@Embeddable
public class UnidadeId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6667651314083266369L;
	private Double codUni;
	private String descricao;
	private Double unidade;
	private Character ativo;
	private Double codCli;

	public UnidadeId() {
	}

	public UnidadeId(Double codUni, String descricao, Double unidade, Character ativo, Double codCli) {
		this.codUni = codUni;
		this.descricao = descricao;
		this.unidade = unidade;
		this.ativo = ativo;
		this.codCli = codCli;
	}

	@Column(name = "COD_UNI", precision = 17, scale = 17)
	public Double getCodUni() {
		return this.codUni;
	}

	public void setCodUni(Double codUni) {
		this.codUni = codUni;
	}

	@Column(name = "DESCRICAO", length = 30)
	@Length(max = 30)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "UNIDADE", precision = 17, scale = 17)
	public Double getUnidade() {
		return this.unidade;
	}

	public void setUnidade(Double unidade) {
		this.unidade = unidade;
	}

	@Column(name = "ATIVO", length = 1)
	public Character getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Character ativo) {
		this.ativo = ativo;
	}

	@Column(name = "COD_CLI", precision = 17, scale = 17)
	public Double getCodCli() {
		return this.codCli;
	}

	public void setCodCli(Double codCli) {
		this.codCli = codCli;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getDescricao());
		umHashCodeBuilder.append(this.getUnidade());
		umHashCodeBuilder.append(this.getAtivo());
		umHashCodeBuilder.append(this.getCodCli());
		umHashCodeBuilder.append(this.getCodUni());
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
		if (!(obj instanceof UnidadeId)) {
			return false;
		}
		UnidadeId other = (UnidadeId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
		umEqualsBuilder.append(this.getUnidade(), other.getUnidade());
		umEqualsBuilder.append(this.getAtivo(), other.getAtivo());
		umEqualsBuilder.append(this.getCodCli(), other.getCodCli());
		umEqualsBuilder.append(this.getCodUni(), other.getCodUni());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
