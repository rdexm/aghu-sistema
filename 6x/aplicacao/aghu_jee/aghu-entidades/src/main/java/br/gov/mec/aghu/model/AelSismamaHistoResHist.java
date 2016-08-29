package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "AEL_SISMAMA_HISTO_RES", schema = "HIST")
public class AelSismamaHistoResHist extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	private static final long serialVersionUID = -4998824280257672372L;
	
	private Integer seq;
	private Integer version;
	private AelSismamaHistoCad sismamaHistoCad;
	private String resposta;
	private AelItemSolicExameHist itemSolicExameHist;
	private Date criadoEm;
	private RapServidores servidor;

	public AelSismamaHistoResHist() {
	}

	public AelSismamaHistoResHist(Integer seq, AelSismamaHistoCad sismamaHistoCad,
			AelItemSolicExameHist itemSolicitacaoExame, RapServidores servidor) {
		this.seq = seq;
		this.sismamaHistoCad = sismamaHistoCad;
		this.itemSolicExameHist = itemSolicitacaoExame;
		this.servidor = servidor; 
	}

	public AelSismamaHistoResHist(Integer seq, AelSismamaHistoCad sismamaHistoCad,
			String resposta, AelItemSolicExameHist itemSolicitacaoExame, Date criadoEm,
			RapServidores servidor) {
		this.seq = seq;
		this.sismamaHistoCad = sismamaHistoCad;
		this.resposta = resposta;
		this.itemSolicExameHist = itemSolicitacaoExame;
		this.criadoEm = criadoEm;
		this.servidor = servidor; 
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "S01_CODIGO", nullable = false)
	public AelSismamaHistoCad getSismamaHistoCad() {
		return this.sismamaHistoCad;
	}

	public void setSismamaHistoCad(AelSismamaHistoCad sismamaHistoCad) {
		this.sismamaHistoCad = sismamaHistoCad;
	}

	@Column(name = "RESPOSTA", length = 1000)
	@Length(max = 1000)
	public String getResposta() {
		return this.resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "ISE_SOE_SEQ", referencedColumnName = "SOE_SEQ", nullable = false),
			@JoinColumn(name = "ISE_SEQP", referencedColumnName = "SEQP", nullable = false)})
	public AelItemSolicExameHist getItemSolicExameHist() {
		return itemSolicExameHist;
	}

	public void setItemSolicExameHist(AelItemSolicExameHist itemSolicitacaoExame) {
		this.itemSolicExameHist = itemSolicitacaoExame;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public enum Fields {

		SEQ("seq"),
		RESPOSTA("resposta"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		ITEM_SOLICITACAO_EXAME("itemSolicExameHist"),
		SISMAMA_HISTO_CAD("sismamaHistoCad")
		;

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
		if (!(obj instanceof AelSismamaHistoRes)) {
			return false;
		}
		AelSismamaHistoRes other = (AelSismamaHistoRes) obj;
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
