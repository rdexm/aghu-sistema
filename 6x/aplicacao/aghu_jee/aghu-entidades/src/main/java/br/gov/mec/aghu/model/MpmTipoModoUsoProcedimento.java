package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "MPM_TIPO_MOD_USO_PROCEDIMENTOS", schema = "AGH")
public class MpmTipoModoUsoProcedimento extends BaseEntityId<MpmTipoModoUsoProcedimentoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8845354884680555355L;
	private MpmTipoModoUsoProcedimentoId id;
	private MpmProcedEspecialDiversos procedimentoEspecialDiverso;
	private MpmUnidadeMedidaMedica unidadeMedidaMedica;
	private RapServidores servidor;
	private String descricao;
	private Boolean indExigeQuantidade;
	private Date criadoEm;
	private DominioSituacao indSituacao;

	private Set<MpmItemProcedimentoSumario> itemProcedimentoSumarios = new HashSet<MpmItemProcedimentoSumario>(
			0);
	private Set<MpmModoUsoPrescProced> modoUsoPrescricaoProcedimentos = new HashSet<MpmModoUsoPrescProced>(
			0);
	private Set<MpmModeloBasicoModoUsoProcedimento> modeloBasicoModoUsoProcedimentos = new HashSet<MpmModeloBasicoModoUsoProcedimento>(
			0);

	public MpmTipoModoUsoProcedimento() {
	}

	public MpmTipoModoUsoProcedimento(MpmTipoModoUsoProcedimentoId id,
			MpmProcedEspecialDiversos procedimentoEspecialDiverso,
			RapServidores servidor, String descricao,
			Boolean indExigeQuantidade, Date criadoEm,
			DominioSituacao indSituacao) {
		this.id = id;
		this.procedimentoEspecialDiverso = procedimentoEspecialDiverso;
		this.servidor = servidor;
		this.descricao = descricao;
		this.indExigeQuantidade = indExigeQuantidade;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	public MpmTipoModoUsoProcedimento(
			MpmTipoModoUsoProcedimentoId id,
			MpmProcedEspecialDiversos procedimentoEspecialDiverso,
			MpmUnidadeMedidaMedica unidadeMedidaMedica,
			RapServidores servidor,
			String descricao,
			Boolean indExigeQuantidade,
			Date criadoEm,
			DominioSituacao indSituacao,
			Set<MpmItemProcedimentoSumario> itemProcedimentoSumarios,
			Set<MpmModoUsoPrescProced> modoUsoPrescricaoProcedimentos,
			Set<MpmModeloBasicoModoUsoProcedimento> modeloBasicoModoUsoProcedimentos) {
		this.id = id;
		this.procedimentoEspecialDiverso = procedimentoEspecialDiverso;
		this.unidadeMedidaMedica = unidadeMedidaMedica;
		this.servidor = servidor;
		this.descricao = descricao;
		this.indExigeQuantidade = indExigeQuantidade;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.itemProcedimentoSumarios = itemProcedimentoSumarios;
		this.modoUsoPrescricaoProcedimentos = modoUsoPrescricaoProcedimentos;
		this.modeloBasicoModoUsoProcedimentos = modeloBasicoModoUsoProcedimentos;
	}

	// getters and setters

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pedSeq", column = @Column(name = "PED_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 3, scale = 0)) })
	public MpmTipoModoUsoProcedimentoId getId() {
		return this.id;
	}

	public void setId(MpmTipoModoUsoProcedimentoId id) {
		this.id = id;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PED_SEQ", nullable = false, insertable = false, updatable = false)	
	public MpmProcedEspecialDiversos getProcedimentoEspecialDiverso() {
		return procedimentoEspecialDiverso;
	}

	public void setProcedimentoEspecialDiverso(
			MpmProcedEspecialDiversos procedimentoEspecialDiverso) {
		this.procedimentoEspecialDiverso = procedimentoEspecialDiverso;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMM_SEQ")
	public MpmUnidadeMedidaMedica getUnidadeMedidaMedica() {
		return unidadeMedidaMedica;
	}

	public void setUnidadeMedidaMedica(
			MpmUnidadeMedidaMedica unidadeMedidaMedica) {
		this.unidadeMedidaMedica = unidadeMedidaMedica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "IND_EXIGE_QUANTIDADE", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExigeQuantidade() {
		return indExigeQuantidade;
	}

	public void setIndExigeQuantidade(Boolean indExigeQuantidade) {
		this.indExigeQuantidade = indExigeQuantidade;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpmTipoModUsoProcedimentos")
	public Set<MpmItemProcedimentoSumario> getItemProcedimentoSumarios() {
		return itemProcedimentoSumarios;
	}

	public void setItemProcedimentoSumarios(
			Set<MpmItemProcedimentoSumario> itemProcedimentoSumarios) {
		this.itemProcedimentoSumarios = itemProcedimentoSumarios;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoModUsoProcedimento")
	public Set<MpmModoUsoPrescProced> getModoUsoPrescricaoProcedimentos() {
		return modoUsoPrescricaoProcedimentos;
	}

	public void setModoUsoPrescricaoProcedimentos(
			Set<MpmModoUsoPrescProced> modoUsoPrescricaoProcedimentos) {
		this.modoUsoPrescricaoProcedimentos = modoUsoPrescricaoProcedimentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoModoUsoProcedimento")
	public Set<MpmModeloBasicoModoUsoProcedimento> getModeloBasicoModoUsoProcedimentos() {
		return modeloBasicoModoUsoProcedimentos;
	}

	public void setModeloBasicoModoUsoProcedimentos(
			Set<MpmModeloBasicoModoUsoProcedimento> modeloBasicoModoUsoProcedimentos) {
		this.modeloBasicoModoUsoProcedimentos = modeloBasicoModoUsoProcedimentos;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmTipoModoUsoProcedimento)) {
			return false;
		}
		MpmTipoModoUsoProcedimento castOther = (MpmTipoModoUsoProcedimento) other;
		return new EqualsBuilder().append(this.id, castOther.getId())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	public enum Fields {
		SEQP("id.seqp"), DESCRICAO("descricao"), IND_EXIGE_QUANTIDADE(
				"indExigeQuantidade"), CRIADO_EM("criadoEm"), IND_SITUACAO(
				"indSituacao"), PROCEDIMENTO_ESPECIAL_DIVERSO(
				"procedimentoEspecialDiverso"), UNIDADE_MEDIDA_MEDICA(
				"unidadeMedidaMedica"), SERVIDOR("servidor"),ID("id"),PED_SEQ("id.pedSeq");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
	@Transient
	public String getDescricaoUnidadeMedidaMedica() {
		String str = null;
		if (this.getUnidadeMedidaMedica() != null) {
			str = this.getUnidadeMedidaMedica().getDescricao();
		}
		return str;
	}
}
