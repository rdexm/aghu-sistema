package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "PTM_BEM_PERMANENTES_JN", schema = "AGH")
@SequenceGenerator(name = "ptmBemPermanentesJnSeq", sequenceName = "AGH.PTM_BEM_JN_SEQ", allocationSize = 1)
public class PtmBemPermanentesJn extends BaseEntitySeq<Integer> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4557470408518252550L;

	private Integer seq;
	private String numeroSerie;
	private Long numeroBem;
	private String detalhamento;
	private String tipo;
	private String situacao;
	private Date DataAquisicao;
	private Date DataCriacao;
	private Date DataAlteracao;
	private Integer valorInicial;
	private Integer valorAtual;
	private String bemPenhora;
	private String numeroProcesso;
	private Integer ataSeq;
	private Integer irpSeq;
	private Integer ccSeq;
	private Integer forSeq;
	private Integer proSeq;
	private Integer matSeq;
	private Integer vgeSeq;
	private Integer gndSeq;
	private Integer serMatricula;
	private Integer serVinCodigo;
	private RapServidores servidor;
	private PtmItemRecebProvisorios ptmItemRecebProvisorios;
	private FccCentroCustos fccCentroCustos;
	private PtmAreaTecAvaliacao ptmAreaTecAvaliacao;
	private ScoFornecedor scoFornecedor;
	private ScoMaterial scoMaterial;
	private FsoVerbaGestao fsoVerbaGestao;
	private FsoGrupoNaturezaDespesa fsoGrupoNaturezaDespesa;
	private Integer version;
	private Date jnData;
	private DominioOperacaoBanco jnOperacao;
	private String jnUsuario;
	private Integer jnSeq;
	private PtmAvaliacaoTecnica avaliacaoTecnica;
	private Short vidaUtil;
	private Date dataInicioGarantia;
	private Short tempoGarantia;
	private DominioAceiteTecnico statusAceiteTecnico;

	@Version
	@Column(name = "VERSION", length = 9, nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "JN_DATE_TIME", nullable = false)
	public Date getJnData() {
		return jnData;
	}

	public void setJnData(Date jnData) {
		this.jnData = jnData;
	}

	@Column(name = "JN_OPERATION", length = 3, nullable = false)
	public DominioOperacaoBanco getJnOperacao() {
		return jnOperacao;
	}

	public void setJnOperacao(DominioOperacaoBanco jnOperacao) {
		this.jnOperacao = jnOperacao;
	}

	@Column(name = "JN_USER", length = 30, nullable = false)
	public String getJnUsuario() {
		return jnUsuario;
	}

	public void setJnUsuario(String jnUsuario) {
		this.jnUsuario = jnUsuario;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmBemPermanentesJnSeq")
	@Column(name = "SEQ_JN", length = 14, nullable = false)
	public Integer getJnSeq() {
		return jnSeq;
	}

	public void setJnSeq(Integer jnSeq) {
		this.jnSeq = jnSeq;
	}

	@Column(name = "SEQ", length = 15, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NR_SERIE", length = 20)
	public String getNumeroSerie() {
		return numeroSerie;
	}

	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}

	@Column(name = "NR_BEM", unique = true, length = 15)
	public Long getNumeroBem() {
		return numeroBem;
	}

	public void setNumeroBem(Long numeroBem) {
		this.numeroBem = numeroBem;
	}

	@Column(name = "DETALHAMENTO", length = 4000)
	public String getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}

	@Column(name = "IRP_SEQ", nullable = false)
	public Integer getIrpSeq() {
		return irpSeq;
	}

	public void setIrpSeq(Integer irpSeq) {
		this.irpSeq = irpSeq;
	}

	@Column(name = "CC_SEQ", nullable = false, length = 6)
	public Integer getCcSeq() {
		return ccSeq;
	}

	public void setCcSeq(Integer ccSeq) {
		this.ccSeq = ccSeq;
	}

	@Column(name = "TIPO", nullable = false, length = 1)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Column(name = "SITUACAO", nullable = false, length = 2)
	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	@Column(name = "ATA_SEQ", length = 6)
	public Integer getAtaSeq() {
		return ataSeq;
	}

	public void setAtaSeq(Integer ataSeq) {
		this.ataSeq = ataSeq;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, length = 6)
	public Integer getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_MATRICULA", nullable = false, length = 6)
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerVinCodigo(Integer serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}


	@Column(name = "DATA_AQUISICAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataAquisicao() {
		return DataAquisicao;
	}

	public void setDataAquisicao(Date dataAquisicao) {
		DataAquisicao = dataAquisicao;
	}

	@Column(name = "DATA_CRIACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataCriacao() {
		return DataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		DataCriacao = dataCriacao;
	}

	@Column(name = "DATA_ALTERACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataAlteracao() {
		return DataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		DataAlteracao = dataAlteracao;
	}

	@Column(name = "VALOR_INICIAL")
	public Integer getValorInicial() {
		return valorInicial;
	}

	public void setValorInicial(Integer valorInicial) {
		this.valorInicial = valorInicial;
	}

	@Column(name = "VALOR_ATUAL")
	public Integer getValorAtual() {
		return valorAtual;
	}

	public void setValorAtual(Integer valorAtual) {
		this.valorAtual = valorAtual;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", insertable = false, updatable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", insertable = false, updatable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "BEM_PENHORA", length = 1)
	public String getBemPenhora() {
		return bemPenhora;
	}

	public void setBemPenhora(String bemPenhora) {
		this.bemPenhora = bemPenhora;
	}

	@Column(name = "NR_PROCESSO", length = 20)
	public String getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	@Column(name = "FOR_SEQ", length = 5)
	public Integer getForSeq() {
		return forSeq;
	}

	public void setForSeq(Integer forSeq) {
		this.forSeq = forSeq;
	}

	@Column(name = "PRO_SEQ", length = 19)
	public Integer getProSeq() {
		return proSeq;
	}

	public void setProSeq(Integer proSeq) {
		this.proSeq = proSeq;
	}

	@Column(name = "MAT_SEQ", length = 6)
	public Integer getMatSeq() {
		return matSeq;
	}

	public void setMatSeq(Integer matSeq) {
		this.matSeq = matSeq;
	}

	@Column(name = "VGE_SEQ", length = 6)
	public Integer getVgeSeq() {
		return vgeSeq;
	}

	public void setVgeSeq(Integer vgeSeq) {
		this.vgeSeq = vgeSeq;
	}

	@Column(name = "GND_SEQ", length = 6)
	public Integer getGndSeq() {
		return gndSeq;
	}

	public void setGndSeq(Integer gndSeq) {
		this.gndSeq = gndSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CC_SEQ", insertable = false, updatable = false)
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATA_SEQ", insertable = false, updatable = false)
	public PtmAreaTecAvaliacao getPtmAreaTecAvaliacao() {
		return ptmAreaTecAvaliacao;
	}

	public void setPtmAreaTecAvaliacao(PtmAreaTecAvaliacao ptmAreaTecAvaliacao) {
		this.ptmAreaTecAvaliacao = ptmAreaTecAvaliacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VGE_SEQ", insertable = false, updatable = false)
	public FsoVerbaGestao getFsoVerbaGestao() {
		return fsoVerbaGestao;
	}

	public void setFsoVerbaGestao(FsoVerbaGestao fsoVerbaGestao) {
		this.fsoVerbaGestao = fsoVerbaGestao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_SEQ", insertable = false, updatable = false)
	public ScoMaterial getScoMaterial() {
		return scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FOR_SEQ", insertable = false, updatable = false)
	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IRP_SEQ", insertable = false, updatable = false)
	public PtmItemRecebProvisorios getPtmItemRecebProvisorios() {
		return ptmItemRecebProvisorios;
	}

	public void setPtmItemRecebProvisorios(
			PtmItemRecebProvisorios ptmItemRecebProvisorios) {
		this.ptmItemRecebProvisorios = ptmItemRecebProvisorios;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GND_SEQ", insertable = false, updatable = false)
	public FsoGrupoNaturezaDespesa getFsoGrupoNaturezaDespesa() {
		return fsoGrupoNaturezaDespesa;
	}

	public void setFsoGrupoNaturezaDespesa(
			FsoGrupoNaturezaDespesa fsoGrupoNaturezaDespesa) {
		this.fsoGrupoNaturezaDespesa = fsoGrupoNaturezaDespesa;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AVT_SEQ")
	public PtmAvaliacaoTecnica getAvaliacaoTecnica() {
		return avaliacaoTecnica;
	}

	public void setAvaliacaoTecnica(PtmAvaliacaoTecnica avaliacaoTecnica) {
		this.avaliacaoTecnica = avaliacaoTecnica;
	}
	
	@Column(name = "VIDA_UTIL", precision = 3, scale = 0)
	public Short getVidaUtil() {
		return vidaUtil;
	}

	public void setVidaUtil(Short vidaUtil) {
		this.vidaUtil = vidaUtil;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INICIO_GARANTIA")
	public Date getDataInicioGarantia() {
		return dataInicioGarantia;
	}

	public void setDataInicioGarantia(Date dataInicioGarantia) {
		this.dataInicioGarantia = dataInicioGarantia;
	}

	@Column(name = "TEMPO_GARANTIA", precision = 3, scale = 0)
	public Short getTempoGarantia() {
		return tempoGarantia;
	}

	public void setTempoGarantia(Short tempoGarantia) {
		this.tempoGarantia = tempoGarantia;
	}
	
	@Column(name = "STATUS_ACEITE_TECNICO")
	@Enumerated(EnumType.STRING)
	public DominioAceiteTecnico getStatusAceiteTecnico() {
		return statusAceiteTecnico;
	}

	public void setStatusAceiteTecnico(DominioAceiteTecnico statusAceiteTecnico) {
		this.statusAceiteTecnico = statusAceiteTecnico;
	}

	public enum Fields {

		SEQ("seq"), IRP_SEQ("irpSeq"), CC_SEQ("ccSeq"), MAT_SEQ("matSeq"), FOR_SEQ(
				"forSeq"), PRO_SEQ("proSeq"), VGE_SEQ("vgeSeq"), GND_SEQ(
				"gndSeq"), SER_VIN_CODIGO("serVinCodigo"), SER_MATRICULA(
				"serMatricula"), NR_SERIE("numeroSerie"), NR_BEM("numeroBem"), DETALHAMENTO(
				"detalhamento"), TIPO("tipo"), SITUACAO("situacao"), ATA_SEQ(
				"ataSeq"), DATA_AQUISICAO("dataAquisicao"), DATA_CRIACAO(
				"dataCriacao"), DATA_ALTERACAO("dataAlteracao"), VALOR_INICIAL(
				"valorInicial"), VALOR_ATUAL("valorAtual"), SERVIDOR("servidor"), BEM_PENHORA(
				"bemPenhora"), NR_PROCESSO("numeroProcesso"), PTM_AREA_TEC_AVALIACAO(
				"areaTecnicaAvaliacao"), SCO_MATERIAIS("scoMaterial"), FCC_CENTRO_CUSTOS(
				"fccCentroCustos"), SCO_FORNECEDORES("scoFornecedor"), FSO_VERBAS_GESTAO(
				"fsoVerbaGestao"), FSO_GRUPOS_NATUREZA_DESPESA("fsoGrupoNaturezaDespesa"), 
				PIRP_SEQ("ptmItemRecebProvisorios"), VERSION("version"), JN_DATE_TIME("jnData"), 
				JN_OPERATION("jnOperacao"), JN_USER("jnUsuario"), SEQ_JN("jnSeq"), STATUS_ACEITE_TECNICO("statusAceiteTecnico"),
				AVALIACAO_TECNICA("avaliacaoTecnica"), AVT_SEQ("avaliacaoTecnica.seq"),
				VID_AUTIL("vidaUtil"), DATA_INICIO_GARANTIA("dataInicioGarantia"),
				TEMPO_GARANTIA("tempoGarantia");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
