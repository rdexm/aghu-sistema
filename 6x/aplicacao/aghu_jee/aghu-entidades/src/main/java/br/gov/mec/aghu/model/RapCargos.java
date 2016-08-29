package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


@Entity
@Table(name = "RAP_CARGOS", schema = "AGH")
public class RapCargos extends BaseEntityCodigo<String> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2437828906791709261L;

	private String codigo;

	private String descricao;
	private DominioSituacao situacao;
	private String codFolhaAmiga;
	
	private Integer version;

	// getters & setters

	public RapCargos() {
	}

	public RapCargos(String codigo) {
		this.codigo = codigo;
	}

	@Id
	@Column(name = "CODIGO", length = 10, nullable = false)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 100, nullable = false)
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

	@Column(name = "COD_FOLHA_AMIGA", length = 10)
	public String getCodFolhaAmiga() {
		return this.codFolhaAmiga;
	}

	public void setCodFolhaAmiga(String codFolhaAmiga) {
		this.codFolhaAmiga = codFolhaAmiga;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
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
		if (!(other instanceof RapCargos)) {
			return false;
		}
		RapCargos castOther = (RapCargos) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}

	public enum Fields {
		CODIGO("codigo"), DESCRICAO("descricao"), SITUACAO("situacao"), COD_FOLHA_AMIGA(
				"codFolhaAmiga");

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