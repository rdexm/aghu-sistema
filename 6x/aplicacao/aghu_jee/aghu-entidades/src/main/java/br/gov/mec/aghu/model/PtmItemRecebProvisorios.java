package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name="PTM_ITEM_RECEB_PROVISORIOS", schema = "AGH")
@SequenceGenerator(name="ptmItemRecebProvisoriosSeq", sequenceName="AGH.PTM_ITEM_RECEB_PROVISORIOS_SEQ", allocationSize = 1)
public class PtmItemRecebProvisorios extends BaseEntitySeq<Long> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4909063804727952563L;
	
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
	private Long quantidadeDisp;
	private SceNotaRecebProvisorio sceNotaRecebProvisorio;
	private List<PtmTecnicoItemRecebimento> listaTecnicoItemRecebimento;
	
	private Integer nrpSeq;
	private Integer nroItem;
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmItemRecebProvisoriosSeq")
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
	
	@Column(name="SCE_NRP_SEQ", insertable = false, updatable = false)
	public Integer getNrpSeq() {
		return this.nrpSeq;
	}
	public void setNrpSeq(Integer nrpSeq) {
		this.nrpSeq = nrpSeq;
	}

	@Column(name="SCE_NRO_ITEM", insertable = false, updatable = false)
	public Integer getNroItem() {
		return this.nroItem;
	}
	public void setNroItem(Integer nroItem) {
		this.nroItem = nroItem;
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

	@Column(name = "QUANTIDADE_DISP", precision = 10, scale = 0)
	public Long getQuantidadeDisp() {
		return quantidadeDisp;
	}

	public void setQuantidadeDisp(Long quantidadeDisp) {
		this.quantidadeDisp = quantidadeDisp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SCE_NRP_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false)
	public SceNotaRecebProvisorio getSceNotaRecebProvisorio() {
		return sceNotaRecebProvisorio;
	}

	public void setSceNotaRecebProvisorio(SceNotaRecebProvisorio sceNotaRecebProvisorio) {
		this.sceNotaRecebProvisorio = sceNotaRecebProvisorio;
	}
	
	@OneToMany(mappedBy = "ptmItemRecebProvisorio", fetch = FetchType.LAZY)
	public List<PtmTecnicoItemRecebimento> getListaTecnicoItemRecebimento() {
		return listaTecnicoItemRecebimento;
	}

	public void setListaTecnicoItemRecebimento(
			List<PtmTecnicoItemRecebimento> listaTecnicoItemRecebimento) {
		this.listaTecnicoItemRecebimento = listaTecnicoItemRecebimento;
	}

	public enum Fields {

		SEQ("seq"),
		SCE_ITEM_RECEB_PROVISORIO("sceItemRecebProvisorio"),
		SCE_ITEM_RECEB_PROVISORIO_NRP_SEQ("sceItemRecebProvisorio.id.nrpSeq"),
		SCE_ITEM_RECEB_PROVISORIO_NRO_ITEM("sceItemRecebProvisorio.id.nroItem"),
		SCE_NRP_SEQ("nrpSeq"),
		SCE_NRO_ITEM("nroItem"),
		STATUS("status"),
		DATA_RECEBIMENTO("dataRecebimento"),
		DATA_ULTIMA_ALTERACAO("dataUltimaAlteracao"),
		SERVIDOR("servidor"),
		SERVIDOR_TEC_PADRAO("servidorTecPadrao"),
		AREA_TECNICA_AVALIACAO("areaTecnicaAvaliacao"),
		SER_MATRICULA_TEC_PADRAO("servidorTecPadrao.id.matricula"),
		SER_VIN_CODIGO_TEC_PADRAO("servidorTecPadrao.id.vinCodigo"),
		ATA_SEQ("ataSeq"),
		PAG_PARCIAL("pagamentoParcial"),
		QUANTIDADE_DISP("quantidadeDisp"),
		SCE_NOTA_RECEB_PROVISORIOS("sceNotaRecebProvisorio"),
		TECNICO_ITEM_RECEBIMENTO("listaTecnicoItemRecebimento");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	// ##### GeradorEqualsHashCodeMain #####
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof PtmItemRecebProvisorios)) {
				return false;
			}
			PtmItemRecebProvisorios other = (PtmItemRecebProvisorios) obj;
			if (getSeq() == null) {
				if (other.getSeq() != null) {
					return false;
				}
			} else if (!getSeq().equals(other.getSeq())) {
				return false;
			}
			return true;
		}
		// ##### GeradorEqualsHashCodeMain #####



}
