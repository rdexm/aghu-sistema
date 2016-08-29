package br.gov.mec.aghu.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MPM_MOD_BASIC_DIETAS", schema = "AGH")

public class MpmModeloBasicoDieta extends BaseEntityId<MpmModeloBasicoDietaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6142283423618556882L;
	private MpmModeloBasicoDietaId id;
	private MpmModeloBasicoPrescricao modeloBasicoPrescricao;
	private RapServidores servidor;
	private String observacao;
	private Boolean indBombaInfusao;

	private Set<MpmItemModeloBasicoDieta> itens = new HashSet<MpmItemModeloBasicoDieta>(
			0);

	@Deprecated
	private Boolean indAvalNutricionista;

	// contrutores
	public MpmModeloBasicoDieta() {
	}

	public MpmModeloBasicoDieta(MpmModeloBasicoDietaId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "modeloBasicoPrescricaoSeq", column = @Column(name = "MDB_SEQ", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false, precision = 8, scale = 0)) })
	public MpmModeloBasicoDietaId getId() {
		return this.id;
	}

	public void setId(MpmModeloBasicoDietaId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDB_SEQ", nullable = false, insertable = false, updatable = false)
	public MpmModeloBasicoPrescricao getModeloBasicoPrescricao() {
		return this.modeloBasicoPrescricao;
	}

	public void setModeloBasicoPrescricao(
			MpmModeloBasicoPrescricao modBasicPrescricao) {
		this.modeloBasicoPrescricao = modBasicPrescricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	/**
	 * 
	 * @deprecated apesar da coluna estar definida na tabela, não está mais
	 *             sendo utilizada.
	 * 
	 * @return
	 */
	@Deprecated
	@Column(name = "IND_AVAL_NUTRICIONISTA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAvalNutricionista() {
		return this.indAvalNutricionista;
	}

	@Deprecated
	public void setIndAvalNutricionista(Boolean indAvalNutricionista) {
		this.indAvalNutricionista = indAvalNutricionista;
	}

	@Column(name = "OBSERVACAO", length = 500)
	@Length(max = 500, message = "A observação possui mais de 500 caracteres.")
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	@Column(name = "IND_BOMBA_INFUSAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndBombaInfusao() {
		return this.indBombaInfusao;
	}

	public void setIndBombaInfusao(Boolean indBombaInfusao) {
		this.indBombaInfusao = indBombaInfusao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "modeloBasicoDieta")
	@Cascade( { org.hibernate.annotations.CascadeType.DELETE })
	public Set<MpmItemModeloBasicoDieta> getItens() {
		return itens;
	}

	public void setItens(Set<MpmItemModeloBasicoDieta> itens) {
		this.itens = itens;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmModeloBasicoDieta)) {
			return false;
		}
		MpmModeloBasicoDieta castOther = (MpmModeloBasicoDieta) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		ID("id"), MODELO_BASICO_PRESCRICAO("modeloBasicoPrescricao"), OBSERVACAO(
				"observacao"), ITENS("itens"), ID_SEQ_MODELO_BASICO(
				"id.modeloBasicoPrescricaoSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validarDados() {
		if (this.indAvalNutricionista == null) {
			this.indAvalNutricionista = false;
		}
	}
}
