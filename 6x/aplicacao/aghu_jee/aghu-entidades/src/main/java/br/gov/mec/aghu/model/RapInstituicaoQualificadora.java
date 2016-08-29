package br.gov.mec.aghu.model;

// Generated 27/04/2010 11:43:10 by Hibernate Tools 3.3.0.GA

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * RapInstQualificadoras generated by hbm2java
 */

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@SequenceGenerator(name="rapIqlSq1", sequenceName="AGH.RAP_IQL_SQ1", allocationSize = 1)
@Table(name = "RAP_INST_QUALIFICADORAS", schema = "AGH")
public class RapInstituicaoQualificadora extends BaseEntityCodigo<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1035260644487947670L;
	private Integer codigo;
	private String descricao;
	private DominioSimNao indInterno;
	private Boolean indUsoGppg;
	private Integer version;

	public RapInstituicaoQualificadora() {
	}

	public RapInstituicaoQualificadora(Integer codigo) {
		this.codigo = codigo;
	}

	public RapInstituicaoQualificadora(Integer codigo, String descricao,
			DominioSimNao indInterno, Boolean indUsoGppg) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.indInterno = indInterno;
		this.indUsoGppg = indUsoGppg;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rapIqlSq1")
	@Column(name = "CODIGO", nullable = false, precision = 5, scale = 0)
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_INTERNO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndInterno() {
		return this.indInterno;
	}

	public void setIndInterno(DominioSimNao indInterno) {
		this.indInterno = indInterno;
	}

    @Column(name = "IND_USO_GPPG", nullable = false)
    @org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoGppg() {
		return this.indUsoGppg;
	}

	public void setIndUsoGppg(Boolean indUsoGppg) {
		this.indUsoGppg = indUsoGppg;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof RapInstituicaoQualificadora)) {
			return false;
		}
		RapInstituicaoQualificadora castOther = (RapInstituicaoQualificadora) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("codigo", this.codigo)
				.toString();
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validacoes() {
		if (indInterno == null) {
			indInterno = DominioSimNao.N;
		}
		if (indUsoGppg == null) {
			indUsoGppg = false;
		}
		descricao = StringUtils.upperCase(descricao);
	}

	public enum Fields {
		CODIGO("codigo"), DESCRICAO("descricao"), IND_INTERNO("indInterno"), IND_USO_GPPG(
				"indUsoGppg");

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