package br.gov.mec.aghu.model;

import java.io.Serializable;

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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name="PTM_USER_TICKET", schema = "AGH")
@SequenceGenerator(name="ptmUserTicketSeq", sequenceName="AGH.PTM_UTI_SQ1", allocationSize = 1)
public class PtmUserTicket extends BaseEntitySeq<Integer> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4460450754681948667L;
	
	private Integer seq;
	private RapServidores servidor;
	private AghCaixaPostal caixaPostal;
	private PtmStatusTicket statusTicket;
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmUserTicketSeq")
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
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
	@JoinColumn(name = "CAP_SEQ")
	public AghCaixaPostal getCaixaPostal() {
		return caixaPostal;
	}
	public void setCaixaPostal(AghCaixaPostal caixaPostal) {
		this.caixaPostal = caixaPostal;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STT_SEQ")
	public PtmStatusTicket getStatusTicket() {
		return statusTicket;
	}
	public void setStatusTicket(PtmStatusTicket statusTicket) {
		this.statusTicket = statusTicket;
	}
	
	public enum Fields{
		
		SEQ("seq"),
		SERVIDOR("servidor"),
		CAIXA_POSTAL("caixaPostal"),
		STATUS_TICKET("statusTicket"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VINCULO("servidor.id.vinCodigo"),
		STT_SEQ("statusTicket.seq");
		
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
