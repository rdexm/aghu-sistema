package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;



@Entity
@Table(name = "SCO_GRUPOS_SERVICOS", schema = "AGH")
@SequenceGenerator(name = "scoGsvSq1", sequenceName = "AGH.SCO_GSV_SQ1", allocationSize = 1)
public class ScoGrupoServico extends BaseEntityCodigo<Integer> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2048012071281126940L;
	private Integer codigo;
	private String descricao;
	private Boolean indEngenharia;
	private Boolean indGeraTituloAvulso;
	private Integer version;

	

	// construtores
	public ScoGrupoServico() {
	}

	// getters & setters
	@Id
	@Column(name = "CODIGO", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoGsvSq1")
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 60, nullable = false)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_ENGENHARIA", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEngenharia() {
		return indEngenharia;
	}

	public void setIndEngenharia(Boolean indEngenharia) {
		this.indEngenharia = indEngenharia;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "IND_GERA_TITULO_AVULSO", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndGeraTituloAvulso() {
		return indGeraTituloAvulso;
	}

	public void setIndGeraTituloAvulso(Boolean indGeraTituloAvulso) {
		this.indGeraTituloAvulso = indGeraTituloAvulso;
	}
	
	
	

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("codigo",this.codigo)
		.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoGrupoServico)){
			return false;
		}
		ScoGrupoServico castOther = (ScoGrupoServico) other;
		return new EqualsBuilder()
			.append(this.codigo, castOther.getCodigo())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.codigo)
		.toHashCode();
	}
	
	public enum Fields {
		CODIGO("codigo"), 
		DESCRICAO("descricao"),
		IND_ENGENHARIA("indEngenharia"),
		IND_GERA_TITULO_AVULSO("indGeraTituloAvulso"),
		VERSION("version");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}
