package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name="PTM_STATUS_TICKET", schema = "AGH")
@SequenceGenerator(name="ptmStatusTicketSeq", sequenceName="AGH.PTM_STT_SQ1", allocationSize = 1)
public class PtmStatusTicket extends BaseEntitySeq<Integer> implements Serializable{

	private static final long serialVersionUID = 4900945257069182686L;
	
	private Integer seq;
	private PtmTicket ticket;
	private Integer status;
	private Date dataCriacao;
	
	private Set<PtmUserTicket> userTickets;
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmStatusTicketSeq")
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TIC_SEQ")
	public PtmTicket getTicket() {
		return ticket;
	}
	public void setTicket(PtmTicket ticket) {
		this.ticket = ticket;
	}
	
	@Column(name = "STATUS", nullable = false)
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_CRIACAO")
	public Date getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "statusTicket")
	public Set<PtmUserTicket> getUserTickets() {
		return userTickets;
	}
	public void setUserTickets(Set<PtmUserTicket> userTickets) {
		this.userTickets = userTickets;
	}
	
	public enum Fields{
		
		SEQ("seq"),
		TICKET("ticket"),
		STATUS("status"),
		DATA_CRIACAO("dataCriacao"),
		USER_TICKETS("userTickets"),
		TICKET_SEQ("ticket.seq");
		
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
