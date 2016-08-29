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

import br.gov.mec.aghu.dominio.DominioControleFrequenciaSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptCfrSQ1", sequenceName="AGH.MPT_CFR_SQ1", allocationSize = 1)
@Table(name = "MPT_CONTROLE_FREQUENCIA", schema = "AGH")
public class MptControleFrequencia extends BaseEntitySeq<Integer> implements Serializable{

	private static final long serialVersionUID = 8622634388591748186L;

	private Integer seq;
	private AipPacientes pacCodigo;
	private MptSessao sesSeq;
	private FatContaApac fatContaApac;
	private DominioControleFrequenciaSituacao situacao;
	private Integer mesReferencia;
	private Integer anoReferencia;
	private RapServidores servidor;
	private Date criadoEm;
	private Integer version;
	private Integer serMatricula;
	private Short serVinCodigo;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptCfrSQ1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	@JoinColumn(name = "PAC_CODIGO")
	@ManyToOne(fetch = FetchType.LAZY)
	public AipPacientes getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(AipPacientes pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	@JoinColumn(name = "SES_SEQ", referencedColumnName = "SEQ", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	public MptSessao getSesSeq() {
		return sesSeq;
	}

	public void setSesSeq(MptSessao sesSeq) {
		this.sesSeq = sesSeq;
	}
	
	@JoinColumns({
		@JoinColumn(name = "CAP_ATM_NUMERO", referencedColumnName = "ATM_NUMERO", nullable = true),
		@JoinColumn(name = "CAP_SEQP", referencedColumnName = "SEQP", nullable = true) })
	@ManyToOne(fetch = FetchType.LAZY)
	public FatContaApac getFatContaApac() {
		return fatContaApac;
	}

	public void setFatContaApac(FatContaApac fatContaApac) {
		this.fatContaApac = fatContaApac;
	}

	@Column(name = "MES_REFERENCIA", unique = true, nullable = false, precision = 2, scale = 0)
	public Integer getMesReferencia() {
		return mesReferencia;
	}

	public void setMesReferencia(Integer mesReferencia) {
		this.mesReferencia = mesReferencia;
	}

	@Column(name = "ANO_REFERENCIA", unique = true, nullable = false, precision = 4, scale = 0)
	public Integer getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(Integer anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioControleFrequenciaSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioControleFrequenciaSituacao situacao) {
		this.situacao = situacao;
	}

	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
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
	
	@Column(name="SER_MATRICULA", insertable=false, updatable=false)
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name="SER_VIN_CODIGO", insertable=false, updatable=false)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public static enum Fields {
		
		SEQ("seq"),
		PAC_CODIGO("pacCodigo"),
		SES_SEQ("sesSeq"),
		FAT_CONTA_APAC("fatContaApac"),
		MES_REFERENCIA("mesReferencia"),
		ANO_REFERENCIA("anoReferencia"),
		SITUACAO("situacao"), 
		SERVIDOR("servidor"), 
		CRIADO_EM ("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

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
