package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mtxTrpjSq1", sequenceName="AGH.MTX_TRPJ_SQ1", allocationSize = 1)
@Table(name = "MTX_TRANSPLANTES_JN", schema = "AGH")
@Immutable
public class MtxTransplantesJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2447137170782270453L;

	private Integer seq;
	private Integer pacCodigoReceptor;
	private Integer pacCodigoDoador;
	private String codDoador;
	private Date dataIngresso;
	private Integer cidSeq;
	private Date dataDiagnostico;
	private Integer orgSeq;
	private Integer dobSeq;
	private Date dataDialise;
	private Date dataFistula;
	private String tipoTmo;
	private String tipoOrgao;
	private String situacao;
	private Short nroGestacoes;
	private Date dataUltimaGestacao;
	private Short nroTransfusoes;
	private Date dataUltimaTransfusao;
	private Short nroTransplantes;
	private Date dataUltimoTransplante;
	private String observacoes;
	private Long rgct;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date criadoEm;
	private Integer version;
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxTrpjSq1")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "PAC_CODIGO_RECEPTOR", nullable = false)
	public Integer getPacCodigoReceptor() {
		return pacCodigoReceptor;
	}

	public void setPacCodigoReceptor(Integer pacCodigoReceptor) {
		this.pacCodigoReceptor = pacCodigoReceptor;
	}

	@Column(name = "PAC_CODIGO_DOADOR")
	public Integer getPacCodigoDoador() {
		return pacCodigoDoador;
	}

	public void setPacCodigoDoador(Integer pacCodigoDoador) {
		this.pacCodigoDoador = pacCodigoDoador;
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

	@Column(name = "CID_SEQ", precision = 5, scale = 0)
	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_DIAGNOSTICO")
	public Date getDataDiagnostico() {
		return dataDiagnostico;
	}

	public void setDataDiagnostico(Date dataDiagnostico) {
		this.dataDiagnostico = dataDiagnostico;
	}

	@Column(name = "ORG_SEQ", precision = 5, scale = 0)
	public Integer getOrgSeq() {
		return orgSeq;
	}

	public void setOrgSeq(Integer orgSeq) {
		this.orgSeq = orgSeq;
	}

	@Column(name = "DOB_SEQ", precision = 5, scale = 0)
	public Integer getDobSeq() {
		return dobSeq;
	}

	public void setDobSeq(Integer dobSeq) {
		this.dobSeq = dobSeq;
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
	public String getTipoTmo() {
		return tipoTmo;
	}

	public void setTipoTmo(String tipoTmo) {
		this.tipoTmo = tipoTmo;
	}

	@Column(name = "TIPO_ORGAO", length = 1)
	public String getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(String tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
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

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
}
