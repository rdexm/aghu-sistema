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

import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioTipoProducao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigDhpSq1", sequenceName = "SIG_DHP_SQ1", allocationSize = 1)
@Table(name = "sig_detalhe_producoes", schema = "agh")
public class SigDetalheProducao extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -282215650472989384L;

	private Integer seq;
	private SigProcessamentoCusto sigProcessamentoCustos;
	private SigObjetoCustoVersoes sigObjetoCustoVersoes;
	private Date criadoEm;
	private RapServidores rapServidores;
	private FccCentroCustos fccCentroCustos;
	private SigDirecionadores sigDirecionadores;
	private FatProcedHospInternos fatProcedHospInternos;
	private DominioGrupoDetalheProducao grupo;
	private DominioTipoProducao tipoProducao;
	private BigDecimal qtde;
	private Integer nroDiasProducao;
	private AacPagador pagador;
	private Integer version;

	private Set<SigProducaoObjetoCusto> sigProducaoObjetoCustoses = new HashSet<SigProducaoObjetoCusto>(0);

	public SigDetalheProducao() {
	}
	
	public SigDetalheProducao(Integer seq) {
		this.setSeq(seq);
	}

	public SigDetalheProducao(Integer seq, SigProcessamentoCusto sigProcessamentoCustos, Date criadoEm, RapServidores rapServidores,
			FccCentroCustos fccCentroCustos, DominioGrupoDetalheProducao grupo, BigDecimal qtde) {
		this.seq = seq;
		this.sigProcessamentoCustos = sigProcessamentoCustos;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.fccCentroCustos = fccCentroCustos;
		this.grupo = grupo;
		this.qtde = qtde;
	}

	public SigDetalheProducao(Integer seq, SigProcessamentoCusto sigProcessamentoCustos, Date criadoEm, RapServidores rapServidores,
			FccCentroCustos fccCentroCustos, FatProcedHospInternos fatProcedHospInternos, DominioGrupoDetalheProducao grupo, DominioTipoProducao tipoProducao,
			BigDecimal qtde, Set<SigProducaoObjetoCusto> sigProducaoObjetoCustoses) {
		this.seq = seq;
		this.sigProcessamentoCustos = sigProcessamentoCustos;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.fccCentroCustos = fccCentroCustos;
		this.fatProcedHospInternos = fatProcedHospInternos;
		this.grupo = grupo;
		this.tipoProducao = tipoProducao;
		this.qtde = qtde;
		this.sigProducaoObjetoCustoses = sigProducaoObjetoCustoses;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigDhpSq1")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCV_SEQ", referencedColumnName = "SEQ", nullable = true)
	public SigObjetoCustoVersoes getSigObjetoCustoVersoes() {
		return sigObjetoCustoVersoes;
	}

	public void setSigObjetoCustoVersoes(SigObjetoCustoVersoes sigObjetoCustoVersoes) {
		this.sigObjetoCustoVersoes = sigObjetoCustoVersoes;
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
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dir_seq", nullable = true, referencedColumnName = "seq")
	public SigDirecionadores getSigDirecionadores() {
		return sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "phi_seq", referencedColumnName = "seq")
	public FatProcedHospInternos getFatProcedHospInternos() {
		return fatProcedHospInternos;
	}

	public void setFatProcedHospInternos(FatProcedHospInternos fatProcedHospInternos) {
		this.fatProcedHospInternos = fatProcedHospInternos;
	}

	@Column(name = "grupo", nullable = false, length = 4)
	@Enumerated(EnumType.STRING)
	public DominioGrupoDetalheProducao getGrupo() {
		return this.grupo;
	}

	public void setGrupo(DominioGrupoDetalheProducao grupo) {
		this.grupo = grupo;
	}

	@Column(name = "tipo_producao", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoProducao getTipoProducao() {
		return this.tipoProducao;
	}

	public void setTipoProducao(DominioTipoProducao tipoProducao) {
		this.tipoProducao = tipoProducao;
	}

	@Column(name = "qtde", nullable = false, precision = 10, scale = 4)
	public BigDecimal getQtde() {
		return this.qtde;
	}

	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}

	@Column(name = "nro_dias_producao")
	public Integer getNroDiasProducao() {
		return nroDiasProducao;
	}

	public void setNroDiasProducao(Integer nroDiasProducao) {
		this.nroDiasProducao = nroDiasProducao;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigDetalheProducoes")
	public Set<SigProducaoObjetoCusto> getSigProducaoObjetoCustoses() {
		return this.sigProducaoObjetoCustoses;
	}

	public void setSigProducaoObjetoCustoses(Set<SigProducaoObjetoCusto> sigProducaoObjetoCustoses) {
		this.sigProducaoObjetoCustoses = sigProducaoObjetoCustoses;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PGD_SEQ", nullable = true)
	public AacPagador getPagador() {
		return this.pagador;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}


	public enum Fields {

		SEQ("seq"),
		PROCESSAMENTO_CUSTO("sigProcessamentoCustos"),
		PROCESSAMENTO_CUSTO_SEQ("sigProcessamentoCustos.seq"),
		OBJETO_CUSTO_VERSAO("sigObjetoCustoVersoes"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		CENTRO_CUSTO("fccCentroCustos"),
		DIRECIONADOR("sigDirecionadores"),
		CENTRO_CUSTO_CODIGO("fccCentroCustos.codigo"),
		FAT_PHI("fatProcedHospInternos"),
		GRUPO("grupo"),
		TIPO_PRODUCAO("tipoProducao"),
		QTDE("qtde"),
		NRO_DIAS_PRODUCAO("nroDiasProducao"),
		PAGADOR("pagador");

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
		if (!(other instanceof SigDetalheProducao)) {
			return false;
		}
		SigDetalheProducao castOther = (SigDetalheProducao) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
