package br.gov.mec.aghu.model;

import java.io.Serializable;
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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name="PTM_NOTIFICACAO_TECNICA", schema = "AGH")
@SequenceGenerator(name="ptmNotificacaoTecnicaSq1", sequenceName="AGH.PTM_NOT_SQ1", allocationSize = 1)
public class PtmNotificacaoTecnica extends BaseEntitySeq<Long> implements Serializable{	

	private static final long serialVersionUID = -6899423877812461228L;
	
	private Long seq;
	private PtmItemRecebProvisorios irpSeq;
	private Date data;
	private RapServidores servidor;
	private int status;
	private String descricao;
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmNotificacaoTecnicaSq1")
	public Long getSeq() {		
		return seq;
	}
	
	public void setSeq(Long seq) {
		this.seq = seq;		
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IRP_SEQ")
	public PtmItemRecebProvisorios getIrpSeq() {
		return irpSeq;
	}

	public void setIrpSeq(PtmItemRecebProvisorios irpSeq) {
		this.irpSeq = irpSeq;
	}

	@Column(name = "DATA")
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
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

	@Column(name = "STATUS", length = 2)	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "DESCRICAO")
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public enum Fields {

		SEQ("seq"),
		IRP_SEQ("irpSeq.seq"),
		DATA("data"),	
		PTM_ITEM_RECEBIMENTO_PROVISORIO("irpSeq"),
		STATUS("status"),
		RAP_SERVIDORES("servidor"),
		DESCRICAO("descricao"),
		SER_MATRICULA("servidor.id.matricula");

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
