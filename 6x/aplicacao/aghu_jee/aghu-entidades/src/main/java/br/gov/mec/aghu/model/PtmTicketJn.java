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
@Table(name="PTM_TICKET_JN", schema="AGH")
@SequenceGenerator(name="ptmTicJnSeq", sequenceName="AGH.PTM_TIC_JN_SEQ", allocationSize = 1)
@Immutable
public class PtmTicketJn extends BaseJournal{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8439066895184823499L;
	
	private Integer seq;
	private Integer status;
	private Integer tipo;
	private Date dataValidade;
	private Date dataCriacao;
	private Date dataAlteradoEm;
	private SceItemRecebProvisorio sceItemRecebProvisorio;
	private RapServidores servidor;
	
	public PtmTicketJn(){
		
	}
	
	public PtmTicketJn(Integer seq, Integer status, Integer tipo,
			Date dataValidade, Date dataCriacao, Date dataAlteradoEm,
			SceItemRecebProvisorio sceItemRecebProvisorio, RapServidores servidor) {
		super();
		this.seq = seq;
		this.status = status;
		this.tipo = tipo;
		this.dataValidade = dataValidade;
		this.dataCriacao = dataCriacao;
		this.dataAlteradoEm = dataAlteradoEm;
		this.sceItemRecebProvisorio = sceItemRecebProvisorio;
		this.servidor = servidor;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmTicJnSeq")
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
	
	@Column(name = "STATUS", nullable = false)
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Column(name = "TIPO", nullable = false)
	public Integer getTipo() {
		return tipo;
	}
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_VALIDADE")
	public Date getDataValidade() {
		return dataValidade;
	}
	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_CRIACAO")
	public Date getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ALTERADO_EM")
	public Date getDataAlteradoEm() {
		return dataAlteradoEm;
	}
	public void setDataAlteradoEm(Date dataAlteradoEm) {
		this.dataAlteradoEm = dataAlteradoEm;
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
	
	public enum Fields {
		
		SEQ("seq"),
		STATUS("status"),
		TIPO("tipo"),
		DATA_VALIDADE("dataValidade"),
		DATA_CRIACAO("dataCriacao"),
		DATA_ALTERADO_EM("dataAlteradoEm"),
		SCE_ITEM_RECEB_PROVISORIO("SceItemRecebProvisorio"),
		SERVIDOR("servidor");
		
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
