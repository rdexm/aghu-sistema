package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoControleVidaUtil;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigVitSq1", sequenceName = "SIG_VIT_SQ1", allocationSize = 1)
@Table(name = "sig_controle_vida_uteis", schema = "agh")
public class SigControleVidaUtil extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 5645466003346618293L;

	private Integer seq;
	private SigProcessamentoCusto sigProcessamentoCustos;
	private Date criadoEm;
	private RapServidores rapServidores;
	private FccCentroCustos fccCentroCustos;
	private ScoMaterial scoMaterial;
	private ScoUnidadeMedida scoUnidadeMedida;
	private SigAtividadeInsumos sigAtividadeInsumos;
	private Date dataInicio;
	private Date dataFim;
	private DominioTipoControleVidaUtil tipo;
	private BigDecimal valorMes;
	private Double qtdeSaldo;
	private BigDecimal vlrSaldo;
	private DominioSituacao indSituacao;
	private Integer version;

	private Set<SigMvtoContaMensal> sigMvtoContaMensaises = new HashSet<SigMvtoContaMensal>(0);

	public SigControleVidaUtil() {
	}

	public SigControleVidaUtil(Integer seq, SigProcessamentoCusto sigProcessamentoCustos, Date criadoEm, RapServidores rapServidores,
			FccCentroCustos fccCentroCustos, ScoMaterial scoMaterial, ScoUnidadeMedida scoUnidadeMedida, Date dataInicio, DominioTipoControleVidaUtil tipo,
			BigDecimal valorMes, DominioSituacao indSituacao) {
		this.seq = seq;
		this.sigProcessamentoCustos = sigProcessamentoCustos;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.fccCentroCustos = fccCentroCustos;
		this.scoMaterial = scoMaterial;
		this.scoUnidadeMedida = scoUnidadeMedida;
		this.dataInicio = dataInicio;
		this.tipo = tipo;
		this.valorMes = valorMes;
		this.indSituacao = indSituacao;
	}

	public SigControleVidaUtil(Integer seq, SigProcessamentoCusto sigProcessamentoCustos, Date criadoEm, RapServidores rapServidores,
			FccCentroCustos fccCentroCustos, ScoMaterial scoMaterial, ScoUnidadeMedida scoUnidadeMedida, SigAtividadeInsumos sigAtividadeInsumos,
			Date dataInicio, Date dataFim, DominioTipoControleVidaUtil tipo, BigDecimal valorMes, Double qtdeSaldo, BigDecimal vlrSaldo,
			DominioSituacao indSituacao, Set<SigMvtoContaMensal> sigMvtoContaMensaises) {
		this.seq = seq;
		this.sigProcessamentoCustos = sigProcessamentoCustos;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.fccCentroCustos = fccCentroCustos;
		this.scoMaterial = scoMaterial;
		this.scoUnidadeMedida = scoUnidadeMedida;
		this.sigAtividadeInsumos = sigAtividadeInsumos;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.tipo = tipo;
		this.valorMes = valorMes;
		this.qtdeSaldo = qtdeSaldo;
		this.vlrSaldo = vlrSaldo;
		this.indSituacao = indSituacao;
		this.sigMvtoContaMensaises = sigMvtoContaMensaises;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigVitSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pmu_seq", nullable = false, referencedColumnName = "seq")
	public SigProcessamentoCusto getSigProcessamentoCustos() {
		return this.sigProcessamentoCustos;
	}

	public void setSigProcessamentoCustos(SigProcessamentoCusto sigProcessamentoCustos) {
		this.sigProcessamentoCustos = sigProcessamentoCustos;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cct_codigo", nullable = false, referencedColumnName = "codigo")
	public FccCentroCustos getFccCentroCustos() {
		return this.fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mat_codigo", nullable = false, referencedColumnName = "codigo")
	public ScoMaterial getScoMaterial() {
		return this.scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "umd_codigo", nullable = false, referencedColumnName = "codigo")
	public ScoUnidadeMedida getScoUnidadeMedida() {
		return this.scoUnidadeMedida;
	}

	public void setScoUnidadeMedida(ScoUnidadeMedida scoUnidadeMedida) {
		this.scoUnidadeMedida = scoUnidadeMedida;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ais_seq", referencedColumnName = "seq")
	public SigAtividadeInsumos getSigAtividadeInsumos() {
		return this.sigAtividadeInsumos;
	}

	public void setSigAtividadeInsumos(SigAtividadeInsumos sigAtividadeInsumos) {
		this.sigAtividadeInsumos = sigAtividadeInsumos;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inicio", nullable = false, length = 29)
	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim", length = 13)
	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Column(name = "tipo", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoControleVidaUtil getTipo() {
		return this.tipo;
	}

	public void setTipo(DominioTipoControleVidaUtil tipo) {
		this.tipo = tipo;
	}

	@Column(name = "valor_mes", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorMes() {
		return this.valorMes;
	}

	public void setValorMes(BigDecimal valorMes) {
		this.valorMes = valorMes;
	}

	@Column(name = "qtde_saldo", precision = 9, scale = 4)
	public Double getQtdeSaldo() {
		return this.qtdeSaldo;
	}

	public void setQtdeSaldo(Double qtdeSaldo) {
		this.qtdeSaldo = qtdeSaldo;
	}

	@Column(name = "vlr_saldo", precision = 20, scale = 4)
	public BigDecimal getVlrSaldo() {
		return this.vlrSaldo;
	}

	public void setVlrSaldo(BigDecimal vlrSaldo) {
		this.vlrSaldo = vlrSaldo;
	}

	@Column(name = "ind_situacao", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigControleVidaUteis")
	public Set<SigMvtoContaMensal> getSigMvtoContaMensaises() {
		return this.sigMvtoContaMensaises;
	}

	public void setSigMvtoContaMensaises(Set<SigMvtoContaMensal> sigMvtoContaMensaises) {
		this.sigMvtoContaMensaises = sigMvtoContaMensaises;
	}

	public enum Fields {

		SEQ("seq"),
		PROCESSAMENTO_CUSTO("sigProcessamentoCustos"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		CENTRO_CUSTO("fccCentroCustos"),
		MATERIAL("scoMaterial"),
		UNIDADE_MEDIDA("scoUnidadeMedida"),
		ATIVIDADE_INSUMO("sigAtividadeInsumos"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim"),
		TIPO("tipo"),
		VALOR_MES("valorMes"),
		QUANTIDADE_SALDO("qtdeSaldo"),
		VALOR_SALDO("vlrSaldo"),
		SITUACAO("indSituacao"),
		LISTA_MVTO_CONTA_MENSAIS("sigMvtoContaMensaises");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigControleVidaUtil)) {
			return false;
		}
		SigControleVidaUtil castOther = (SigControleVidaUtil) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
