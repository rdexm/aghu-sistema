package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * The persistent class for the sce_almoxarifados database table.
 * 
 */

@Entity
@Table(name = "SCE_ALMOXARIFADOS")
@SequenceGenerator(name="sceAlmSq1", sequenceName="AGH.SCE_ALM_SQ1", allocationSize = 1)
public class SceAlmoxarifado extends BaseEntitySeq<Short> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7241630082550267611L;
	private Short seq;
	private FccCentroCustos centroCusto;
	private String descricao;
	private Integer diasEstqMinimo;
	private Date dtAlteraSituacao;
	private Boolean indBloqEntrTransf;
	private Boolean indCalculaMediaPonderada;
	private Boolean indCentral;
	private DominioSituacao indSituacao;
	private RapServidores servidor;
	private Integer tempoReposicaoClassA;
	private Integer tempoReposicaoClassB;
	private Integer tempoReposicaoClassC;
	private Integer tempoReposicaoContrClassA;
	private Integer tempoReposicaoContrClassB;
	private Integer tempoReposicaoContrClassC;
	private Integer diasParcelaClassA;
	private Integer diasParcelaClassB;
	private Integer diasParcelaClassC;
	private Integer diasSaldoClassA;
	private Integer diasSaldoClassB;
	private Integer diasSaldoClassC;
	private Integer version;
	private Set<ScoMaterial> materiais;
	private Set<SceTransferencia> transferencias;
	private Set<SceEstoqueAlmoxarifado> estoquesAlmoxarifado;
	private Set<AghUnidadesFuncionais> unidadesFuncionais;
	private Set<SceDevolucaoAlmoxarifado> devolucoesAlmoxarifado;
	private Boolean indMaterialDireto;
	private Boolean indMultiplosGrupos;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator="sceAlmSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", nullable = false)
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "DIAS_ESTQ_MINIMO")
	public Integer getDiasEstqMinimo() {
		return this.diasEstqMinimo;
	}

	public void setDiasEstqMinimo(Integer diasEstqMinimo) {
		this.diasEstqMinimo = diasEstqMinimo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ALTERA_SITUACAO")
	public Date getDtAlteraSituacao() {
		return this.dtAlteraSituacao;
	}

	public void setDtAlteraSituacao(Date dtAlteraSituacao) {
		this.dtAlteraSituacao = dtAlteraSituacao;
	}

	@Column(name = "IND_BLOQ_ENTR_TRANSF")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndBloqEntrTransf() {
		return this.indBloqEntrTransf;
	}

	public void setIndBloqEntrTransf(Boolean indBloqEntrTransf) {
		this.indBloqEntrTransf = indBloqEntrTransf;
	}

	@Column(name = "IND_CALCULA_MEDIA_PONDERADA")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCalculaMediaPonderada() {
		return this.indCalculaMediaPonderada;
	}

	public void setIndCalculaMediaPonderada(Boolean indCalculaMediaPonderada) {
		this.indCalculaMediaPonderada = indCalculaMediaPonderada;
	}

	@Column(name = "IND_MAT_DIRETO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndMaterialDireto() {
		return this.indMaterialDireto;
	}

	public void setIndMaterialDireto(Boolean indMaterialDireto) {
		this.indMaterialDireto = indMaterialDireto;
	}

	@Column(name = "IND_MULT_GRUPO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndMultiplosGrupos() {
		return this.indMultiplosGrupos;
	}

	public void setIndMultiplosGrupos(Boolean indMultiplosGrupos) {
		this.indMultiplosGrupos = indMultiplosGrupos;
	}

	@Column(name = "IND_CENTRAL")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCentral() {
		return this.indCentral;
	}

	public void setIndCentral(Boolean indCentral) {
		this.indCentral = indCentral;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "TEMPO_REPOSICAO_CLASS_A")
	public Integer getTempoReposicaoClassA() {
		return this.tempoReposicaoClassA;
	}

	public void setTempoReposicaoClassA(Integer tempoReposicaoClassA) {
		this.tempoReposicaoClassA = tempoReposicaoClassA;
	}

	@Column(name = "TEMPO_REPOSICAO_CLASS_B")
	public Integer getTempoReposicaoClassB() {
		return this.tempoReposicaoClassB;
	}

	public void setTempoReposicaoClassB(Integer tempoReposicaoClassB) {
		this.tempoReposicaoClassB = tempoReposicaoClassB;
	}

	@Column(name = "TEMPO_REPOSICAO_CLASS_C")
	public Integer getTempoReposicaoClassC() {
		return this.tempoReposicaoClassC;
	}

	public void setTempoReposicaoClassC(Integer tempoReposicaoClassC) {
		this.tempoReposicaoClassC = tempoReposicaoClassC;
	}

	@Column(name = "TEMPO_REPOSICAO_CONTR_CLASS_A")
	public Integer getTempoReposicaoContrClassA() {
		return this.tempoReposicaoContrClassA;
	}

	public void setTempoReposicaoContrClassA(Integer tempoReposicaoContrClassA) {
		this.tempoReposicaoContrClassA = tempoReposicaoContrClassA;
	}

	@Column(name = "TEMPO_REPOSICAO_CONTR_CLASS_B")
	public Integer getTempoReposicaoContrClassB() {
		return this.tempoReposicaoContrClassB;
	}

	public void setTempoReposicaoContrClassB(Integer tempoReposicaoContrClassB) {
		this.tempoReposicaoContrClassB = tempoReposicaoContrClassB;
	}

	@Column(name = "TEMPO_REPOSICAO_CONTR_CLASS_C")
	public Integer getTempoReposicaoContrClassC() {
		return this.tempoReposicaoContrClassC;
	}

	public void setTempoReposicaoContrClassC(Integer tempoReposicaoContrClassC) {
		this.tempoReposicaoContrClassC = tempoReposicaoContrClassC;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// bi-directional many-to-one association to ScoMaterial
	@OneToMany(mappedBy = "almoxarifado")
	public Set<ScoMaterial> getMateriais() {
		return materiais;
	}

	public void setMateriais(Set<ScoMaterial> materiais) {
		this.materiais = materiais;
	}
	
	// bi-directional many-to-one association to ScoMaterial
	@OneToMany(mappedBy = "almoxarifado")
	public Set<SceTransferencia> getTransferencias() {
		return this.transferencias;
	}
	
	public void setTransferencias(Set<SceTransferencia> transferencias) {
		this.transferencias = transferencias;
	}

	@OneToMany(mappedBy = "almoxarifado")	
	public Set<SceEstoqueAlmoxarifado> getEstoquesAlmoxarifado() {
		return estoquesAlmoxarifado;
	}

	public void setEstoquesAlmoxarifado(
			Set<SceEstoqueAlmoxarifado> estoquesAlmoxarifado) {
		this.estoquesAlmoxarifado = estoquesAlmoxarifado;
	}

	@OneToMany(mappedBy = "almoxarifado")	
	public Set<AghUnidadesFuncionais> getUnidadesFuncionais() {
		return unidadesFuncionais;
	}
	
	public void setUnidadesFuncionais(Set<AghUnidadesFuncionais> unidadesFuncionais) {
		this.unidadesFuncionais = unidadesFuncionais;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "almoxarifado")
	public Set<SceDevolucaoAlmoxarifado> getDevolucoesAlmoxarifado() {
		return devolucoesAlmoxarifado;
	}

	public void setDevolucoesAlmoxarifado(
			Set<SceDevolucaoAlmoxarifado> devolucoesAlmoxarifado) {
		this.devolucoesAlmoxarifado = devolucoesAlmoxarifado;
	}	

	@Transient
	public String getSeqDescricao() {
		final String seq = getSeq() == null ? "" : getSeq() + " - ";
		final String descricao = getDescricao() == null ? "" : getDescricao();
		return seq + descricao;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof SceAlmoxarifado)) {
			return false;
		}
		
		SceAlmoxarifado castOther = (SceAlmoxarifado) other;
		
		return new EqualsBuilder().append(this.getSeq(), castOther.getSeq()).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getSeq()).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), CCT_CODIGO("centroCusto"), DESCRICAO("descricao"), DIAS_ESTOQUE_MINIMO(
				"diasEstqMinimo"), DT_ALTERA_SITUACAO("dtAlteraSituacao"), IND_BLOQ_ENTR_TRANSF(
				"indBloqEntrTransf"), IND_CALCULA_MEDIA_PONDERADA(
				"indCalculaMediaPonderada"), IND_CENTRAL("indCentral"), IND_SITUACAO(
				"indSituacao"), TEMPO_REPOSICAO_CLASS_A("tempoReposicaoClassA"), TEMPO_REPOSICAO_CLASS_B(
				"tempoReposicaoClassB"), TEMPO_REPOSICAO_CLASS_C(
				"tempoReposicaoClassC"), TEMPO_REPOSICAO_CONTR_CLASS_A(
				"tempoReposicaoContrClassA"), TEMPO_REPOSICAO_CONTR_CLASS_B(
				"tempoReposicaoContrClassB"), TEMPO_REPOSICAO_CONTR_CLASS_C(
				"tempoReposicaoContrClassC"), VERSION("version"), SCO_MATERIAIS(
				"materiais"), SCE_TRANSFERECIA("transferencias"), SCE_ESTOQUE_ALMOXARIFADO("estoquesAlmoxarifado"),
				AGH_UNIDADES_FUNCIONAIS("unidadesFuncionais"),
				SERVIDOR("servidor"),
				DEVOLUCOES_ALMOXARIFADO("devolucoesAlmoxarifado");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@SuppressWarnings({"PMD.NPathComplexity"})
	@Transient
	public String getDescricaoTempoReposicaoTempoReposicaoContrato() {
		StringBuilder sb = new StringBuilder(120);
		
		sb.append(getSeqDescricao())
		.append(StringUtil.NOVA_LINHA_HTML)
		.append("Tempo Reposi\u00E7\u00E3o:")
		.append(StringUtil.NOVA_LINHA_HTML)
		.append(" - Classe A: ").append(
				getTempoReposicaoClassA() != null ? getTempoReposicaoClassA() : "")
		.append(StringUtil.NOVA_LINHA_HTML)
		.append(" - Classe B: ").append(
				getTempoReposicaoClassB() != null ? getTempoReposicaoClassB() : "")
		.append(StringUtil.NOVA_LINHA_HTML)
		.append(" - Classe C: ").append(
			getTempoReposicaoClassC() != null ? getTempoReposicaoClassC() : "")
		.append(StringUtil.NOVA_LINHA_HTML)
		.append("Tempo Reposi\u00E7\u00E3o Contrato:")
		.append(StringUtil.NOVA_LINHA_HTML)
		.append(" - Classe A: ").append(
				getTempoReposicaoContrClassA() != null ? getTempoReposicaoContrClassA() : "")
		.append(StringUtil.NOVA_LINHA_HTML)
		.append(" - Classe B: ").append(
				getTempoReposicaoContrClassB() != null ? getTempoReposicaoContrClassB() : "")
		.append(StringUtil.NOVA_LINHA_HTML)
		.append(" - Classe C: ").append(
				getTempoReposicaoContrClassC() != null ? getTempoReposicaoContrClassC() : "");
		
		return sb.toString();
	}

	@Column(name="DIAS_PARCELA_CLASS_A")
	public Integer getDiasParcelaClassA() {
		return diasParcelaClassA;
	}

	public void setDiasParcelaClassA(Integer diasParcelaClassA) {
		this.diasParcelaClassA = diasParcelaClassA;
	}

	@Column(name="DIAS_PARCELA_CLASS_B")
	public Integer getDiasParcelaClassB() {
		return diasParcelaClassB;
	}

	public void setDiasParcelaClassB(Integer diasParcelaClassB) {
		this.diasParcelaClassB = diasParcelaClassB;
	}

	@Column(name="DIAS_PARCELA_CLASS_C")
	public Integer getDiasParcelaClassC() {
		return diasParcelaClassC;
	}

	public void setDiasParcelaClassC(Integer diasParcelaClassC) {
		this.diasParcelaClassC = diasParcelaClassC;
	}

	@Column(name="DIAS_SALDO_CLASS_A")
	public Integer getDiasSaldoClassA() {
		return diasSaldoClassA;
	}

	public void setDiasSaldoClassA(Integer diasSaldoClassA) {
		this.diasSaldoClassA = diasSaldoClassA;
	}

	@Column(name="DIAS_SALDO_CLASS_B")
	public Integer getDiasSaldoClassB() {
		return diasSaldoClassB;
	}

	public void setDiasSaldoClassB(Integer diasSaldoClassB) {
		this.diasSaldoClassB = diasSaldoClassB;
	}

	@Column(name="DIAS_SALDO_CLASS_C")
	public Integer getDiasSaldoClassC() {
		return diasSaldoClassC;
	}

	public void setDiasSaldoClassC(Integer diasSaldoClassC) {
		this.diasSaldoClassC = diasSaldoClassC;
	}
}