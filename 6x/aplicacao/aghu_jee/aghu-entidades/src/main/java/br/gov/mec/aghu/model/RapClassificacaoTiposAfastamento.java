package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


@Entity
@Table(name = "RAP_CLASSIF_TIPOS_AFASTAMENTO", schema = "AGH")
public class RapClassificacaoTiposAfastamento extends BaseEntityCodigo<String> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1066239914797508039L;

	private String codigo;

	private String descricao;

	// getters & setters
	@Id
	@Column(name = "CODIGO", length = 2, nullable = false)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 45, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("codigo", this.codigo)
				.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof RapClassificacaoTiposAfastamento)) {
			return false;
		}
		RapClassificacaoTiposAfastamento castOther = (RapClassificacaoTiposAfastamento) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}

	public enum Fields {
		CODIGO("codigo"), DESCRICAO("descricao")

		;

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