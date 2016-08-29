package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.List;

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
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mtxTrpSq1", sequenceName="AGH.MTX_TRP_SQ1", allocationSize = 1)
@Table(name = "MTX_TRANSPLANTES", schema = "AGH")
public class MtxTransplantes extends BaseEntitySeq<Integer> implements Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6596161241132156462L;

	private Integer seq;
	private AipPacientes receptor;
	private AipPacientes doador;
	private String codDoador;
	private Date dataIngresso;
	private AghCid cid;
	private Date dataDiagnostico;
	private MtxOrigens origem;
	private MtxDoencaBases doencaBase;
	private Date dataDialise;
	private Date dataFistula;
	private DominioSituacaoTmo tipoTmo;
	private DominioTipoAlogenico tipoAlogenico;
	private DominioTipoOrgao tipoOrgao;
	private DominioSituacaoTransplante situacao;
	private Short nroGestacoes;
	private Date dataUltimaGestacao;
	private Short nroTransfusoes;
	private Date dataUltimaTransfusao;
	private Short nroTransplantes;
	private Date dataUltimoTransplante;
	private String observacoes;
	private Long rgct;
	private RapServidores servidor;
	private Date criadoEm;
	private Integer version;
	private MtxCriterioPriorizacaoTmo criterioPriorizacao;
	private List<MtxExtratoTransplantes> extratoTransplantes;
	
	
	private Integer pacCodReceptor;
	private Integer pacCodDoador;
	private Integer origemSeq;
	private Integer doencasBaseSeq;
	private Integer cidSeq;
	
	
	public MtxTransplantes() {
	}
	
	public MtxTransplantes(Integer trpSeq) {
		this.seq = trpSeq;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxTrpSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO_RECEPTOR", nullable = false)
	public AipPacientes getReceptor() {
		return receptor;
	}

	public void setReceptor(AipPacientes receptor) {
		this.receptor = receptor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO_DOADOR")
	public AipPacientes getDoador() {
		return doador;
	}

	public void setDoador(AipPacientes doador) {
		this.doador = doador;
	}

	@Column(name = "COD_DOADOR", length = 12)
	@Length(max = 12)
	public String getCodDoador() {
		return codDoador;
	}

	public void setCodDoador(String codDoador) {
		this.codDoador = codDoador;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INGRESSO", nullable = false)
	public Date getDataIngresso() {
		return dataIngresso;
	}

	public void setDataIngresso(Date dataIngresso) {
		this.dataIngresso = dataIngresso;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CID_SEQ")
	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_DIAGNOSTICO")
	public Date getDataDiagnostico() {
		return dataDiagnostico;
	}

	public void setDataDiagnostico(Date dataDiagnostico) {
		this.dataDiagnostico = dataDiagnostico;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORG_SEQ")
	public MtxOrigens getOrigem() {
		return origem;
	}

	public void setOrigem(MtxOrigens origem) {
		this.origem = origem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOB_SEQ")
	public MtxDoencaBases getDoencaBase() {
		return doencaBase;
	}

	public void setDoencaBase(MtxDoencaBases doencaBase) {
		this.doencaBase = doencaBase;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_DIALISE")
	public Date getDataDialise() {
		return dataDialise;
	}

	public void setDataDialise(Date dataDialise) {
		this.dataDialise = dataDialise;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_FISTULA")
	public Date getDataFistula() {
		return dataFistula;
	}

	public void setDataFistula(Date dataFistula) {
		this.dataFistula = dataFistula;
	}

	@Column(name = "TIPO_TMO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoTmo getTipoTmo() {
		return tipoTmo;
	}

	public void setTipoTmo(DominioSituacaoTmo tipoTmo) {
		this.tipoTmo = tipoTmo;
	}
	
	@Column(name = "IND_TIPO_ALOGENICO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoAlogenico getTipoAlogenico() {
		return tipoAlogenico;
	}
	
	public void setTipoAlogenico(DominioTipoAlogenico tipoAlogenico) {
		this.tipoAlogenico = tipoAlogenico;
	}

	@Column(name = "TIPO_ORGAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(DominioTipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoTransplante getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoTransplante situacao) {
		this.situacao = situacao;
	}

	@Column(name = "NRO_GESTACOES", precision = 2, scale = 0)
	public Short getNroGestacoes() {
		return nroGestacoes;
	}

	public void setNroGestacoes(Short nroGestacoes) {
		this.nroGestacoes = nroGestacoes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULTIMA_GESTACAO")
	public Date getDataUltimaGestacao() {
		return dataUltimaGestacao;
	}

	public void setDataUltimaGestacao(Date dataUltimaGestacao) {
		this.dataUltimaGestacao = dataUltimaGestacao;
	}

	@Column(name = "NRO_TRANSFUSOES", precision = 2, scale = 0)
	public Short getNroTransfusoes() {
		return nroTransfusoes;
	}

	public void setNroTransfusoes(Short nroTransfusoes) {
		this.nroTransfusoes = nroTransfusoes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULTIMA_TRANSFUSAO")
	public Date getDataUltimaTransfusao() {
		return dataUltimaTransfusao;
	}

	public void setDataUltimaTransfusao(Date dataUltimaTransfusao) {
		this.dataUltimaTransfusao = dataUltimaTransfusao;
	}

	@Column(name = "NRO_TRANSPLANTES", precision = 2, scale = 0)
	public Short getNroTransplantes() {
		return nroTransplantes;
	}

	public void setNroTransplantes(Short nroTransplantes) {
		this.nroTransplantes = nroTransplantes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULTIMO_TRANSPLANTE")
	public Date getDataUltimoTransplante() {
		return dataUltimoTransplante;
	}

	public void setDataUltimoTransplante(Date dataUltimoTransplante) {
		this.dataUltimoTransplante = dataUltimoTransplante;
	}

	@Column(name = "OBSERVACOES", length = 2000)
	@Length(max = 2000)
	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	@Column(name = "RGCT", precision = 10, scale = 0)
	public Long getRgct() {
		return rgct;
	}

	public void setRgct(Long rgct) {
		this.rgct = rgct;
	}

	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable=false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable=false) })
	@ManyToOne(fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CPT_SEQ")
	public MtxCriterioPriorizacaoTmo getCriterioPriorizacao() {
		return criterioPriorizacao;
	}

	public void setCriterioPriorizacao(MtxCriterioPriorizacaoTmo criterioPriorizacao) {
		this.criterioPriorizacao = criterioPriorizacao;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mtxTransplante")
	public List<MtxExtratoTransplantes> getExtratoTransplantes() {
		return extratoTransplantes;
	}

	public void setExtratoTransplantes(List<MtxExtratoTransplantes> extratoTransplantes) {
		this.extratoTransplantes = extratoTransplantes;
	}	

	public enum Fields {
		
		SEQ("seq"),
		RECEPTOR("receptor"),
		EXTRATO_TRANSPLANTE("extratoTransplantes"),
		DOADOR("doador"),
		RECEPTOR_CODIGO("receptor.codigo"),
		DOADOR_CODIGO("doador.codigo"),
		COD_DOADOR("codDoador"),
		DATA_INGRESSO("dataIngresso"),
		CID("cid"),
		DATA_DIAGNOSTICO("dataDiagnostico"),
		ORIGEM("origem"),
		DOENCA_BASE("doencaBase"),
		DATA_DIALISE("dataDialise"),
		DATA_FISTULA("dataFistula"),
		TIPO_TMO("tipoTmo"),
		TIPO_ALOGENICO("tipoAlogenico"),
		TIPO_ORGAO("tipoOrgao"),
		SITUACAO("situacao"),
		NRO_GESTACOES("nroGestacoes"),
		DATA_ULTIMA_GESTACAO("dataUltimaGestacao"),
		NRO_TRANSFUSOES("nroTransfusoes"),
		DATA_ULTIMA_TRANSFUSAO("dataUltimaTransfusao"),
		NRO_TRANSPLANTES("nroTransplantes"),
		DATA_ULTIMO_TRANSPLANTE("dataUltimoTransplante"),
		OBSERVACOES("observacoes"),
		RGCT("rgct"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		CRITERIO_PRIORIZACAO("criterioPriorizacao"),
		CRITERIO_PRIORIZACAO_SEQ("criterioPriorizacao.seq"),
		CODIGO_RECEPTOR("pacCodReceptor"),
		;

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
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeq());
		umHashCodeBuilder.append(this.getCodDoador());
		umHashCodeBuilder.append(this.getCriadoEm());
		umHashCodeBuilder.append(this.getDataDiagnostico());
		umHashCodeBuilder.append(this.getDataDialise());
		umHashCodeBuilder.append(this.getDataFistula());
		umHashCodeBuilder.append(this.getDataIngresso());
		umHashCodeBuilder.append(this.getDataUltimaGestacao());
		umHashCodeBuilder.append(this.getDataUltimaTransfusao());
		umHashCodeBuilder.append(this.getDataUltimoTransplante());
		umHashCodeBuilder.append(this.getNroGestacoes());
		umHashCodeBuilder.append(this.getNroTransfusoes());
        umHashCodeBuilder.append(this.getNroTransplantes());
        umHashCodeBuilder.append(this.getObservacoes());
        umHashCodeBuilder.append(this.getSituacao());
        umHashCodeBuilder.append(this.getTipoOrgao());
        umHashCodeBuilder.append(this.getTipoTmo());
        return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MtxTransplantes other = (MtxTransplantes) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
		umEqualsBuilder.append(this.getCodDoador(), other.getCodDoador());
		umEqualsBuilder.append(this.getCriadoEm(), other.getCriadoEm());
		umEqualsBuilder.append(this.getDataDiagnostico(), other.getDataDiagnostico());
		umEqualsBuilder.append(this.getDataDialise(), other.getDataDialise());
		umEqualsBuilder.append(this.getDataFistula(), other.getDataFistula());
		umEqualsBuilder.append(this.getDataIngresso(), other.getDataIngresso());
		umEqualsBuilder.append(this.getDataUltimaGestacao(), other.getDataUltimaGestacao());
		umEqualsBuilder.append(this.getDataUltimaTransfusao(), other.getDataUltimaTransfusao());
		umEqualsBuilder.append(this.getDataUltimoTransplante(), other.getDataUltimoTransplante());
		umEqualsBuilder.append(this.getNroGestacoes(), other.getNroGestacoes());
		umEqualsBuilder.append(this.getNroTransfusoes(), other.getNroTransfusoes());
		umEqualsBuilder.append(this.getNroTransplantes(), other.getNroTransplantes());
		umEqualsBuilder.append(this.getObservacoes(), other.getObservacoes());
		umEqualsBuilder.append(this.getSituacao(), other.getSituacao());
		umEqualsBuilder.append(this.getTipoOrgao(), other.getTipoOrgao());
		umEqualsBuilder.append(this.getTipoTmo(), other.getTipoTmo());
        return umEqualsBuilder.isEquals();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Column(name="ORG_SEQ", insertable=false, updatable=false)
	public Integer getOrigemSeq() {
		return origemSeq;
	}

	public void setOrigemSeq(Integer origemSeq) {
		this.origemSeq = origemSeq;
	}

	@Column(name="DOB_SEQ", insertable=false, updatable=false)
	public Integer getDoencasBaseSeq() {
		return doencasBaseSeq;
	}

	public void setDoencasBaseSeq(Integer doencasBaseSeq) {
		this.doencasBaseSeq = doencasBaseSeq;
	}

	@Column(name="CID_SEQ", insertable=false, updatable=false)
	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	@Column(name="PAC_CODIGO_RECEPTOR", insertable=false, updatable=false)
	public Integer getPacCodReceptor() {
		return pacCodReceptor;
	}

	public void setPacCodReceptor(Integer pacCodReceptor) {
		this.pacCodReceptor = pacCodReceptor;
	}

	@Column(name="PAC_CODIGO_DOADOR", insertable=false, updatable=false)
	public Integer getPacCodDoador() {
		return pacCodDoador;
	}

	public void setPacCodDoador(Integer pacCodDoador) {
		this.pacCodDoador = pacCodDoador;
	}
}
