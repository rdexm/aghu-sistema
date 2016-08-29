package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


@Entity
@Table(name = "FCP_MOEDAS", schema = "AGH")
@SequenceGenerator(name = "fcpMdaSq1", sequenceName = "AGH.FCP_MDA_SQ1", allocationSize = 1)
public class FcpMoeda extends BaseEntityCodigo<Short> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8509110432835322911L;
	private Short codigo;
	private String descricao;
	private String representacao;

	// construtores

	public FcpMoeda() {
	}

	// getters & setters
	@Id
	@Column(name = "CODIGO", length = 3, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fcpMdaSq1")
	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 20, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "REPRESENTACAO", length = 6, nullable = false)
	public String getRepresentacao() {
		return this.representacao;
	}

	public void setRepresentacao(String representacao) {
		this.representacao = representacao;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("codigo", this.codigo)
				.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof FcpMoeda)){
			return false;
		}
		FcpMoeda castOther = (FcpMoeda) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}

	public enum Fields {
		CODIGO("codigo"), DESCRICAO("descricao"), REPRESENTACAO("representacao");

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
