package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Classe que popula a lista de registros da grid de User Tickets na tela de Manter Tickets de Análise Técnica.
 */
public class UserTicketVO implements Serializable {
	
	private static final long serialVersionUID = -5062977536158887771L;

	private Integer userTicketSeq;

	private Integer matricula;

	private Short vinculo;

	private Integer ticket;

	private Boolean selecionado;

	public enum Fields {

		USER_TICKET_SEQ("userTicketSeq"),
		MATRICULA("matricula"),
		VINCULO("vinculo"),
		TICKET("ticket");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getUserTicketSeq() {
		return userTicketSeq;
	}

	public void setUserTicketSeq(Integer userTicketSeq) {
		this.userTicketSeq = userTicketSeq;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public Integer getTicket() {
		return ticket;
	}

	public void setTicket(Integer ticket) {
		this.ticket = ticket;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public int hashCode() {

		HashCodeBuilder builder = new HashCodeBuilder();
		
		builder.append(userTicketSeq);
		
		return builder.build();
	}

	@Override
	public boolean equals(Object obj) {

		EqualsBuilder builder = new EqualsBuilder();

		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		UserTicketVO other = (UserTicketVO) obj;

		builder.append(userTicketSeq, other.getUserTicketSeq());

		return builder.build();
	}

}
