package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
@Table(name="PTM_TICKET", schema = "AGH")
@SequenceGenerator(name="ptmTicketSeq", sequenceName="AGH.PTM_TIC_SQ1", allocationSize = 1)
public class PtmTicket extends BaseEntitySeq<Integer> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8940519422560971219L;
	
	private Integer seq;
	private Integer status;
	private Integer tipo;
	private Date dataValidade;
	private Date dataCriacao;
	private Date dataAlteradoEm;
	private PtmItemRecebProvisorios itemRecebProvisorios;
	private RapServidores servidor;
	private RapServidores servidorAlteracao;
	
	private Set<PtmStatusTicket> ptmStatusTicket = new HashSet<PtmStatusTicket>(0);
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmTicketSeq")
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
	@JoinColumn(name = "PTM_IRP_SEQ")
	public PtmItemRecebProvisorios getItemRecebProvisorios() {
		return itemRecebProvisorios;
	}
	public void setItemRecebProvisorios(PtmItemRecebProvisorios itemRecebProvisorios) {
		this.itemRecebProvisorios = itemRecebProvisorios;
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
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ticket")
	public Set<PtmStatusTicket> getPtmStatusTicket() {
		return ptmStatusTicket;
	}
	public void setPtmStatusTicket(Set<PtmStatusTicket> ptmStatusTicket) {
		this.ptmStatusTicket = ptmStatusTicket;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA_ALTERACAO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERACAO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlteracao() {
		return servidorAlteracao;
	}
	public void setServidorAlteracao(RapServidores servidorAlteracao) {
		this.servidorAlteracao = servidorAlteracao;
	}

	public enum Fields {
		
		SEQ("seq"),
		STATUS("status"),
		TIPO("tipo"),
		DATA_VALIDADE("dataValidade"),
		DATA_CRIACAO("dataCriacao"),
		DATA_ALTERADO_EM("dataAlteradoEm"),
		ITEM_RECEB_PROVISORIO("itemRecebProvisorios"),
		ITEM_RECEB_PROVISORIO_SEQ("itemRecebProvisorios.seq"),
		SERVIDOR("servidor"),
		PTMSTATUSTICKET("ptmStatusTicket");
		
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
