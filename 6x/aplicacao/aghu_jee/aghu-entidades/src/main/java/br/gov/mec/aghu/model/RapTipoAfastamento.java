package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSexoAfastamento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name = "RAP_TIPOS_AFASTAMENTO", schema = "AGH")
public class RapTipoAfastamento extends BaseEntityCodigo<String> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7531987555163227109L;

	private String codigo;

	private String descricao;
	private DominioSituacao indSituacao;
	private Boolean indBloqAcesso;
	private DominioSimNao indExigeConsulta;
	private String codFolhaAmiga;
	private DominioSimNao indHorarioInicio;
	private DominioSimNao indHorarioFim;
	private Integer qtdeDias;
	private DominioSexoAfastamento sexoAfast;
	private String tipoFolhaAmiga;
	private Boolean indPerfilEpidemiologico;
	private Boolean indAbsentGeral;
	private DominioSimNao indPermiteCid;
	private Integer codCf;
	private Integer codCausaStarh;
	private String codMotivoStarh;

	private RapClassificacaoTiposAfastamento classificacaoTipoAfastamento;

	private Integer version;

	// getters & setters
	@Id
	@Column(name = "CODIGO", length = 2, nullable = false)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = StringUtils.upperCase(codigo);
	}

	@Column(name = "DESCRICAO", length = 45, nullable = false)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

    @Column(name = "IND_BLOQ_ACESSO")
    @org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndBloqAcesso() {
		return this.indBloqAcesso;
	}

	public void setIndBloqAcesso(Boolean indBloqAcesso) {
		this.indBloqAcesso = indBloqAcesso;
	}

	@Column(name = "IND_EXIGE_CONSULTA", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndExigeConsulta() {
		return this.indExigeConsulta;
	}

	public void setIndExigeConsulta(DominioSimNao indExigeConsulta) {
		this.indExigeConsulta = indExigeConsulta;
	}

	@Column(name = "COD_FOLHA_AMIGA", length = 2)
	public String getCodFolhaAmiga() {
		return this.codFolhaAmiga;
	}

	public void setCodFolhaAmiga(String codFolhaAmiga) {
		this.codFolhaAmiga = codFolhaAmiga;
	}

	@Column(name = "IND_HORARIO_INICIO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndHorarioInicio() {
		return this.indHorarioInicio;
	}

	public void setIndHorarioInicio(DominioSimNao indHorarioInicio) {
		this.indHorarioInicio = indHorarioInicio;
	}

	@Column(name = "IND_HORARIO_FIM", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndHorarioFim() {
		return this.indHorarioFim;
	}

	public void setIndHorarioFim(DominioSimNao indHorarioFim) {
		this.indHorarioFim = indHorarioFim;
	}

	@Column(name = "QTDE_DIAS", length = 3)
	public Integer getQtdeDias() {
		return this.qtdeDias;
	}

	public void setQtdeDias(Integer qtdeDias) {
		this.qtdeDias = qtdeDias;
	}

	@Column(name = "SEXO_AFAST", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSexoAfastamento getSexoAfast() {
		return this.sexoAfast;
	}

	public void setSexoAfast(DominioSexoAfastamento sexoAfast) {
		this.sexoAfast = sexoAfast;
	}

	@Column(name = "TIPO_FOLHA_AMIGA", length = 3)
	public String getTipoFolhaAmiga() {
		return this.tipoFolhaAmiga;
	}

	public void setTipoFolhaAmiga(String tipoFolhaAmiga) {
		this.tipoFolhaAmiga = tipoFolhaAmiga;
	}
	
    @Column(name = "IND_PERFIL_EPIDEMIOLOGICO", nullable = false)
    @org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPerfilEpidemiologico() {
		return this.indPerfilEpidemiologico;
	}

	public void setIndPerfilEpidemiologico(Boolean indPerfilEpidemiologico) {
		this.indPerfilEpidemiologico = indPerfilEpidemiologico;
	}

	
    @Column(name = "IND_ABSENT_GERAL", nullable = false)
    @org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAbsentGeral() {
		return this.indAbsentGeral;
	}

	public void setIndAbsentGeral(Boolean indAbsentGeral) {
		this.indAbsentGeral = indAbsentGeral;
	}

	@Column(name = "IND_PERMITE_CID", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndPermiteCid() {
		return this.indPermiteCid;
	}

	public void setIndPermiteCid(DominioSimNao indPermiteCid) {
		this.indPermiteCid = indPermiteCid;
	}

	@Column(name = "COD_CF", length = 3)
	public Integer getCodCf() {
		return this.codCf;
	}

	public void setCodCf(Integer codCf) {
		this.codCf = codCf;
	}

	@Column(name = "COD_CAUSA_STARH", length = 5)
	public Integer getCodCausaStarh() {
		return this.codCausaStarh;
	}

	public void setCodCausaStarh(Integer codCausaStarh) {
		this.codCausaStarh = codCausaStarh;
	}

	@Column(name = "COD_MOTIVO_STARH", length = 5)
	public String getCodMotivoStarh() {
		return this.codMotivoStarh;
	}

	public void setCodMotivoStarh(String codMotivoStarh) {
		this.codMotivoStarh = codMotivoStarh;
	}

	@ManyToOne
	@JoinColumn(name = "CTA_CODIGO", referencedColumnName = "CODIGO")
	public RapClassificacaoTiposAfastamento getClassificacaoTipoAfastamento() {
		return classificacaoTipoAfastamento;
	}

	public void setClassificacaoTipoAfastamento(
			RapClassificacaoTiposAfastamento classificacaoTipoAfastamento) {
		this.classificacaoTipoAfastamento = classificacaoTipoAfastamento;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Transient
	public boolean isSlcIndExigeConsulta() {
		return indExigeConsulta != null ? indExigeConsulta.isSim() : false;
	}

	public void setSlcIndExigeConsulta(boolean valor) {
		indExigeConsulta = DominioSimNao.getInstance(valor);
	}

	@Transient
	public boolean isSlcIndHorarioInicio() {
		return indHorarioInicio != null ? indHorarioInicio.isSim() : false;
	}

	public void setSlcIndHorarioInicio(boolean valor) {
		indHorarioInicio = DominioSimNao.getInstance(valor);
	}
	
	@Transient
	public boolean isSlcIndHorarioFim() {
		return indHorarioFim != null ? indHorarioFim.isSim() : false;
	}

	public void setSlcIndHorarioFim(boolean valor) {
		indHorarioFim = DominioSimNao.getInstance(valor);
	}
	
	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(Fields.CODIGO.toString(),
				this.codigo)
				.append(Fields.DESCRICAO.toString(), this.descricao).append(
						Fields.SEXO_AFAST.toString(), this.sexoAfast).append(
						Fields.IND_SITUACAO.toString(), this.indSituacao)
				.append(Fields.IND_EXIGE_CONSULTA.toString(),
						this.indExigeConsulta).append(
						Fields.IND_PERMITE_CID.toString(), this.indPermiteCid)
				.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof RapTipoAfastamento)) {
			return false;
		}
		RapTipoAfastamento castOther = (RapTipoAfastamento) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}
	
	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validacoes() {
		if (sexoAfast == null) {
			sexoAfast = DominioSexoAfastamento.A;
		}
		if (indExigeConsulta == null) {
			indExigeConsulta = DominioSimNao.S;
		}
		if (indHorarioInicio == null) {
			indHorarioInicio = DominioSimNao.N;
		}
		if (indHorarioFim == null) {
			indHorarioFim = DominioSimNao.N;
		}
		if (indSituacao == null) {
			indSituacao = DominioSituacao.A;
		}
		if (indPerfilEpidemiologico == null) {
			indPerfilEpidemiologico = false;
		}
		if (indAbsentGeral == null) {
			indAbsentGeral = false;
		}
		if (indBloqAcesso == null) {
			indBloqAcesso = false;
		}
		codMotivoStarh = StringUtils.trimToNull(codMotivoStarh);
		codigo = StringUtils.upperCase(codigo);
		descricao = StringUtils.upperCase(descricao);
	}
	
	

	public enum Fields {
		CODIGO("codigo"), DESCRICAO("descricao"), IND_SITUACAO("indSituacao"), IND_BLOQ_ACESSO(
				"indBloqAcesso"), IND_EXIGE_CONSULTA("indExigeConsulta"), COD_FOLHA_AMIGA(
				"codFolhaAmiga"), IND_HORARIO_INICIO("indHorarioInicio"), IND_HORARIO_FIM(
				"indHorarioFim"), QTDE_DIAS("qtdeDias"), SEXO_AFAST("sexoAfast"), TIPO_FOLHA_AMIGA(
				"tipoFolhaAmiga"), IND_PERFIL_EPIDEMIOLOGICO(
				"indPerfilEpidemiologico"), IND_ABSENT_GERAL("indAbsentGeral"), IND_PERMITE_CID(
				"indPermiteCid"), COD_CF("codCf"), COD_CAUSA_STARH(
				"codCausaStarh"), COD_MOTIVO_STARH("codMotivoStarh"),

		CLASSIFICACAO_TIPOS_AFASTAMENTO("classificacaoTipoAfastamento");

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