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

import br.gov.mec.aghu.dominio.DominioAceiteTecnico;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name="PTM_BEM_PERMANENTES", schema = "AGH")
@SequenceGenerator(name="ptmBemPermanentesSeq", sequenceName="AGH.PTM_BPE_SQ1", allocationSize = 1)
public class PtmBemPermanentes extends BaseEntitySeq<Long> implements Serializable {

	private static final long serialVersionUID = -3479097547773137617L;

	private Long seq;
	private String numeroSerie;
	private Long numeroBem;
	private String detalhamento;
	private Long irpSeq;
	private Integer ccSeq;
	private String tipo;
	private String situacao;
	private Integer ataSeq;
	private PtmAreaTecAvaliacao areaTecnicaAvaliacao;
	private Date DataAquisicao;
	private Date DataCriacao;
	private Date DataAlteracao;
	private Integer valorInicial;
	private Integer valorAtual;
	private RapServidores servidor;
	private String bemPenhora;
	private String numeroProcesso;
	private Integer forSeq;
	private Integer proSeq;
	private Integer matSeq;
	private Integer vgeSeq;
	private Integer gndSeq;
	private PtmItemRecebProvisorios ptmItemRecebProvisorios;
	private ScoMaterial material;
	private DominioAceiteTecnico statusAceiteTecnico;
	private PtmAvaliacaoTecnica avaliacaoTecnica;
	private Short vidaUtil;
	private Date dataInicioGarantia;
	private Short tempoGarantia;

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmBemPermanentesSeq")
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Column(name = "NR_SERIE", length = 20)
	public String getNumeroSerie() {
		return numeroSerie;
	}

	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}

	@Column(name = "NR_BEM")
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

	@Column(name = "IRP_SEQ", nullable = false, insertable = false, updatable = false)
	public Long getIrpSeq() {
		return irpSeq;
	}

	public void setIrpSeq(Long irpSeq) {
		this.irpSeq = irpSeq;
	}

	@Column(name = "CC_SEQ", nullable = false)
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

	@Column(name = "ATA_SEQ")
	public Integer getAtaSeq() {
		return ataSeq;
	}

	public void setAtaSeq(Integer ataSeq) {
		this.ataSeq = ataSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATA_SEQ", insertable = false, updatable = false)
	public PtmAreaTecAvaliacao getAreaTecnicaAvaliacao() {
		return areaTecnicaAvaliacao;
	}

	public void setAreaTecnicaAvaliacao(PtmAreaTecAvaliacao areaTecnicaAvaliacao) {
		this.areaTecnicaAvaliacao = areaTecnicaAvaliacao;
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
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
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

	@Column(name = "FOR_SEQ")
	public Integer getForSeq() {
		return forSeq;
	}

	public void setForSeq(Integer forSeq) {
		this.forSeq = forSeq;
	}

	@Column(name = "PRO_SEQ")
	public Integer getProSeq() {
		return proSeq;
	}

	public void setProSeq(Integer proSeq) {
		this.proSeq = proSeq;
	}
	
	@Column(name = "MAT_SEQ")
	public Integer getMatSeq() {
		return matSeq;
	}

	public void setMatSeq(Integer matSeq) {
		this.matSeq = matSeq;
	}

	@Column(name = "VGE_SEQ")
	public Integer getVgeSeq() {
		return vgeSeq;
	}

	public void setVgeSeq(Integer vgeSeq) {
		this.vgeSeq = vgeSeq;
	}

	@Column(name = "GND_SEQ")
	public Integer getGndSeq() {
		return gndSeq;
	}

	public void setGndSeq(Integer gndSeq) {
		this.gndSeq = gndSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IRP_SEQ")
	public PtmItemRecebProvisorios getPtmItemRecebProvisorios() {
		return ptmItemRecebProvisorios;
	}

	public void setPtmItemRecebProvisorios(PtmItemRecebProvisorios ptmItemRecebProvisorios) {
		this.ptmItemRecebProvisorios = ptmItemRecebProvisorios;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_SEQ", insertable = false, updatable = false)
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	
	@Column(name = "STATUS_ACEITE_TECNICO")
	@Enumerated(EnumType.STRING)
	public DominioAceiteTecnico getStatusAceiteTecnico() {
		return statusAceiteTecnico;
	}

	public void setStatusAceiteTecnico(DominioAceiteTecnico statusAceiteTecnico) {
		this.statusAceiteTecnico = statusAceiteTecnico;
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

	public enum Fields {

		SEQ("seq"),
		NR_SERIE("numeroSerie"),
		NR_BEM("numeroBem"),
		DETALHAMENTO("detalhamento"), 
		IRP_SEQ("irpSeq"), 
		CC_SEQ("ccSeq"),
		TIPO("tipo"),
		SITUACAO("situacao"),
		ATA_SEQ("ataSeq"),
		AREA_TECNICA_AVALIACAO("areaTecnicaAvaliacao"),
		DATA_AQUISICAO("dataAquisicao"),
		DATA_CRIACAO("dataCriacao"),
		DATA_ALTERACAO("dataAlteracao"), 
		VALOR_INICIAL("valorInicial"),
		VALOR_ATUAL("valorAtual"),
		SERVIDOR("servidor"),
		BEM_PENHORA("bemPenhora"),
		NR_PROCESSO("numeroProcesso"),
		MAT_SEQ("matSeq"),
		FOR_SEQ("forSeq"), 
		PRO_SEQ("proSeq"),
		VGE_SEQ("vgeSeq"), 
		GND_SEQ("gndSeq"),
		MATERIAL("material"),
		PIRP_SEQ("ptmItemRecebProvisorios"),
		STATUS_ACEITE_TECNICO("statusAceiteTecnico"),
		AVALIACAO_TECNICA("avaliacaoTecnica"),
		AVT_SEQ("avaliacaoTecnica.seq"),
		VID_AUTIL("vidaUtil"),
		DATA_INICIO_GARANTIA("dataInicioGarantia"),
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
