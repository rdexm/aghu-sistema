package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


@Entity
@Table(name = "SCO_CRITERIO_ESCOLHA_PROPOSTAS", schema = "AGH")
@SequenceGenerator(name = "scoCepSq1", sequenceName = "AGH.SCO_CEP_SQ1", allocationSize = 1)
public class ScoCriterioEscolhaProposta extends BaseEntityCodigo<Short> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7916920649986215973L;
	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;
	private Integer version;

	// construtores

	public ScoCriterioEscolhaProposta() {
	}

	// getters & setters
	@Id
	@Column(name = "CODIGO", length = 3, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoCepSq1")
	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 60, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false) 
	public Integer getVersion(){
		return this.version;
	}
	
	public void setVersion(Integer version){
		this.version = version;
	}
	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("codigo", this.codigo)
				.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoCriterioEscolhaProposta)){
			return false;
		}
		ScoCriterioEscolhaProposta castOther = (ScoCriterioEscolhaProposta) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}

	public enum Fields {
		CODIGO("codigo"), DESCRICAO("descricao"), SITUACAO("situacao");

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
