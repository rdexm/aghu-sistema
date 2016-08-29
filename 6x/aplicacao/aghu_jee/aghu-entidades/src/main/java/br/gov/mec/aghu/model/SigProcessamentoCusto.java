package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigPmuSq1", sequenceName = "SIG_PMU_SQ1", allocationSize = 1)
@Table(name = "sig_processamento_custos", schema = "agh")
public class SigProcessamentoCusto extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -9103483792182556315L;
	private Integer seq;
	private Date criadoEm;
	private Date competencia;
	private DominioSituacaoProcessamentoCusto indSituacao;
	private RapServidores rapServidores;
	private Integer version;
	private Date dataInicio;
	private Date dataFim;

	private Set<SigControleVidaUtil> sigControleVidaUteises = new HashSet<SigControleVidaUtil>(0);
	private Set<SigCalculoObjetoCusto> sigCalculoObjetoCustoses = new HashSet<SigCalculoObjetoCusto>(0);
	private Set<SigCalculoComponente> sigCalculoComponenteses = new HashSet<SigCalculoComponente>(0);
	private Set<SigProcessamentoCustoLog> sigProcessamentoCustoLogses = new HashSet<SigProcessamentoCustoLog>(0);
	private Set<SigMvtoContaMensal> sigMvtoContaMensaises = new HashSet<SigMvtoContaMensal>(0);
	private Set<SigDetalheProducao> sigDetalheProducoeses = new HashSet<SigDetalheProducao>(0);

	public SigProcessamentoCusto() {
	}

	public SigProcessamentoCusto(Integer seq) {
		this.setSeq(seq);
	}

	public SigProcessamentoCusto(Integer seq, Date criadoEm, Date competencia, DominioSituacaoProcessamentoCusto indSituacao, RapServidores rapServidores) {
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.competencia = competencia;
		this.indSituacao = indSituacao;
		this.rapServidores = rapServidores;
	}

	public SigProcessamentoCusto(Integer seq, Date criadoEm, Date competencia, DominioSituacaoProcessamentoCusto indSituacao, RapServidores rapServidores,
			Set<SigControleVidaUtil> sigControleVidaUteises, Set<SigCalculoObjetoCusto> sigCalculoObjetoCustoses,
			Set<SigCalculoComponente> sigCalculoComponenteses, Set<SigProcessamentoCustoLog> sigProcessamentoCustoLogses,
			Set<SigMvtoContaMensal> sigMvtoContaMensaises, Set<SigDetalheProducao> sigDetalheProducoeses) {
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.competencia = competencia;
		this.indSituacao = indSituacao;
		this.rapServidores = rapServidores;
		this.sigControleVidaUteises = sigControleVidaUteises;
		this.sigCalculoObjetoCustoses = sigCalculoObjetoCustoses;
		this.sigCalculoComponenteses = sigCalculoComponenteses;
		this.sigProcessamentoCustoLogses = sigProcessamentoCustoLogses;
		this.sigMvtoContaMensaises = sigMvtoContaMensaises;
		this.sigDetalheProducoeses = sigDetalheProducoeses;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigPmuSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "competencia", nullable = false, length = 29)
	public Date getCompetencia() {
		return this.competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	@Column(name = "ind_situacao", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoProcessamentoCusto getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoProcessamentoCusto indSituacao) {
		this.indSituacao = indSituacao;
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

	@Column(name = "VERSION")
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inicio", length = 29)
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fim", length = 29)
	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigProcessamentoCustos")
	public Set<SigControleVidaUtil> getSigControleVidaUteises() {
		return this.sigControleVidaUteises;
	}

	public void setSigControleVidaUteises(Set<SigControleVidaUtil> sigControleVidaUteises) {
		this.sigControleVidaUteises = sigControleVidaUteises;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigProcessamentoCustos")
	public Set<SigCalculoObjetoCusto> getSigCalculoObjetoCustoses() {
		return this.sigCalculoObjetoCustoses;
	}

	public void setSigCalculoObjetoCustoses(Set<SigCalculoObjetoCusto> sigCalculoObjetoCustoses) {
		this.sigCalculoObjetoCustoses = sigCalculoObjetoCustoses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigProcessamentoCustos")
	public Set<SigCalculoComponente> getSigCalculoComponenteses() {
		return this.sigCalculoComponenteses;
	}

	public void setSigCalculoComponenteses(Set<SigCalculoComponente> sigCalculoComponenteses) {
		this.sigCalculoComponenteses = sigCalculoComponenteses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigProcessamentoCustos")
	public Set<SigProcessamentoCustoLog> getSigProcessamentoCustoLogses() {
		return this.sigProcessamentoCustoLogses;
	}

	public void setSigProcessamentoCustoLogses(Set<SigProcessamentoCustoLog> sigProcessamentoCustoLogses) {
		this.sigProcessamentoCustoLogses = sigProcessamentoCustoLogses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigProcessamentoCustos")
	public Set<SigMvtoContaMensal> getSigMvtoContaMensaises() {
		return this.sigMvtoContaMensaises;
	}

	public void setSigMvtoContaMensaises(Set<SigMvtoContaMensal> sigMvtoContaMensaises) {
		this.sigMvtoContaMensaises = sigMvtoContaMensaises;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigProcessamentoCustos")
	public Set<SigDetalheProducao> getSigDetalheProducoeses() {
		return this.sigDetalheProducoeses;
	}

	public void setSigDetalheProducoeses(Set<SigDetalheProducao> sigDetalheProducoeses) {
		this.sigDetalheProducoeses = sigDetalheProducoeses;
	}

	public enum Fields {

		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		COMPETENCIA("competencia"),
		SITUACAO_PROCESSAMENTO_CUSTO("indSituacao"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim"),
		LISTA_CONTROLE_VIDA_UTIL("sigControleVidaUteises"),
		LISTA_CALCULO_OBJETO_CUSTO("sigCalculoObjetoCustoses"),
		LISTA_CALCULO_COMPONENTE("sigCalculoComponenteses"),
		LISTA_PROCESSAMENTO_CUSTO_LOG("sigProcessamentoCustoLogses"),
		LISTA_MVTO_CONTA_MENSAL("sigMvtoContaMensaises"),
		LISTA_DETALHE_PRODUCAO("sigDetalheProducoeses");

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
		if (!(other instanceof SigProcessamentoCusto)) {
			return false;
		}
		SigProcessamentoCusto castOther = (SigProcessamentoCusto) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	@Transient
	public String getCompetenciaMesAno() {		
		return new SimpleDateFormat("MM/yyyy").format(this.competencia);
	}
}
