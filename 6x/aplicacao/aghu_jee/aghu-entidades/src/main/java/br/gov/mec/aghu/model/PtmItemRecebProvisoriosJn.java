package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name="PTM_ITEM_RECEB_PROVISORIOS_JN", schema="AGH")
@SequenceGenerator(name="ptmIrpJnSeq", sequenceName="AGH.PTM_IRP_JN_SEQ", allocationSize = 1)
@Immutable
public class PtmItemRecebProvisoriosJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2701514750871369700L;
	
	private Long seq;
	private SceItemRecebProvisorio sceItemRecebProvisorio;
	private Integer status;
	private Date dataRecebimento;
	private Date dataUltimaAlteracao;
	private RapServidores servidor;
	private RapServidores servidorTecPadrao;
	private Integer ataSeq;
	private PtmAreaTecAvaliacao areaTecnicaAvaliacao;
	private Integer pagamentoParcial;
	
	public PtmItemRecebProvisoriosJn(){
		
	}
	
	public PtmItemRecebProvisoriosJn(Long seq,
			SceItemRecebProvisorio sceItemRecebProvisorio, Integer status,
			Date dataRecebimento, Date dataUltimaAlteracao,
			RapServidores servidor, RapServidores servidorTecPadrao,
			Integer ataSeq, PtmAreaTecAvaliacao areaTecnicaAvaliacao,
			Integer pagamentoParcial) {
		super();
		this.seq = seq;
		this.sceItemRecebProvisorio = sceItemRecebProvisorio;
		this.status = status;
		this.dataRecebimento = dataRecebimento;
		this.dataUltimaAlteracao = dataUltimaAlteracao;
		this.servidor = servidor;
		this.servidorTecPadrao = servidorTecPadrao;
		this.ataSeq = ataSeq;
		this.areaTecnicaAvaliacao = areaTecnicaAvaliacao;
		this.pagamentoParcial = pagamentoParcial;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmIrpJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", nullable = false)
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SCE_NRP_SEQ", referencedColumnName = "NRP_SEQ"),
			@JoinColumn(name = "SCE_NRO_ITEM", referencedColumnName = "NRO_ITEM") })
	public SceItemRecebProvisorio getSceItemRecebProvisorio() {
		return sceItemRecebProvisorio;
	}

	public void setSceItemRecebProvisorio(
			SceItemRecebProvisorio sceItemRecebProvisorio) {
		this.sceItemRecebProvisorio = sceItemRecebProvisorio;
	}

	@Column(name = "STATUS", nullable = false)
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_RECEBIMENTO")
	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULTIMA_ALTERACAO")
	public Date getDataUltimaAlteracao() {
		return dataUltimaAlteracao;
	}

	public void setDataUltimaAlteracao(Date dataUltimaAlteracao) {
		this.dataUltimaAlteracao = dataUltimaAlteracao;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA_TEC_PADRAO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_TEC_PADRAO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorTecPadrao() {
		return servidorTecPadrao;
	}

	public void setServidorTecPadrao(RapServidores servidorTecPadrao) {
		this.servidorTecPadrao = servidorTecPadrao;
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

	@Column(name = "PAG_PARCIAL", precision = 2, scale = 0)
	public Integer getPagamentoParcial() {
		return pagamentoParcial;
	}

	public void setPagamentoParcial(Integer pagamentoParcial) {
		this.pagamentoParcial = pagamentoParcial;
	}
	
	public enum Fields {

		SEQ("seq"),
		SCE_ITEM_RECEB_PROVISORIO("sceItemRecebProvisorio"),
		SCE_NRP_SEQ("sceItemRecebProvisorio.id.nrpSeq"),
		SCE_NRO_ITEM("sceItemRecebProvisorio.id.nroItem"),
		STATUS("status"),
		DATA_RECEBIMENTO("dataRecebimento"),
		DATA_ULTIMA_ALTERACAO("dataUltimaAlteracao"),
		SERVIDOR("servidor"),
		SERVIDOR_TEC_PADRAO("servidorTecPadrao"),
		AREA_TECNICA_AVALIACAO("areaTecnicaAvaliacao"),
		SER_MATRICULA_TEC_PADRAO("servidorTecPadrao.id.matricula"),
		SER_VIN_CODIGO_TEC_PADRAO("servidorTecPadrao.id.vinCodigo"),
		ATA_SEQ("ataSeq"),
		PAG_PARCIAL("pagamentoParcial");

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
