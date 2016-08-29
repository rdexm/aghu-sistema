package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "SCO_NOMES_COMERCIAIS", schema = "AGH")
public class ScoNomeComercial extends BaseEntityId<ScoNomeComercialId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1238563138394995817L;
	private ScoNomeComercialId id;
	private String nome;
	private DominioSituacao situacao;
	private ScoMarcaComercial marcaComercial;

	// construtores

	public ScoNomeComercial() {
	}

	public ScoNomeComercial(ScoNomeComercialId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "MCM_CODIGO", column = @Column(name = "MCM_CODIGO", nullable = false, length = 6)),
			@AttributeOverride(name = "NUMERO", column = @Column(name = "NUMERO", nullable = false, length = 5)) })
	public ScoNomeComercialId getId() {
		return this.id;
	}

	public void setId(ScoNomeComercialId id) {
		this.id = id;
	}

	@Column(name = "NOME", length = 60, nullable = false)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MCM_CODIGO", referencedColumnName = "CODIGO", insertable=false, updatable=false)
	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoNomeComercial)){
			return false;
		}
		ScoNomeComercial castOther = (ScoNomeComercial) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		ID("id"),
		NUMERO("id.numero"),
		MARCA_COMERCIAL_ID("id.mcmCodigo"),
		NOME("nome"), 
		SITUACAO("situacao"), 
		MARCA_COMERCIAL("marcaComercial");

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
