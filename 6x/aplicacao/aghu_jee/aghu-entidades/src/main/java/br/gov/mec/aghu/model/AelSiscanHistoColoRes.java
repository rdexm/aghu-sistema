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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;



/**
 * The persistent class for the AEL_SISCAN_HISTO_COLO_RES database table.
 * 
 */
@Entity
@Table(name="AEL_SISCAN_HISTO_COLO_RES")
@SequenceGenerator(name="aelSiscanHistoColoResSeq", sequenceName="AEL_SHCR_SQ1")
public class AelSiscanHistoColoRes implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6286763344470216157L;
	private Long seq;
	private Date criadoEm;
	private AelItemSolicitacaoExames aelItemSolicitacaoExames;
	private String resposta;
	private RapServidores servidor;
	private Integer version;
	private AelSiscanHistoColoCad aelSiscanHistoColoCad;

    public AelSiscanHistoColoRes() {
    }


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="aelSiscanHistoColoResSeq")
	@Column(unique=true, nullable=false, precision=9)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "ISE_SOE_SEQ", referencedColumnName = "SOE_SEQ", nullable = false, insertable = false, updatable = false),
		@JoinColumn(name = "ISE_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public AelItemSolicitacaoExames getAelItemSolicitacaoExames() {
		return this.aelItemSolicitacaoExames;
	}


	public void setAelItemSolicitacaoExames(AelItemSolicitacaoExames aelItemSolicitacaoExames) {
		this.aelItemSolicitacaoExames = aelItemSolicitacaoExames;
	}


	@Column(length=1000)
	public String getResposta() {
		return this.resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}


	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}


	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	//bi-directional many-to-one association to AelSiscanHistoColoCad
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SHCC_CODIGO", nullable=false)
	public AelSiscanHistoColoCad getAelSiscanHistoColoCad() {
		return this.aelSiscanHistoColoCad;
	}

	public void setAelSiscanHistoColoCad(AelSiscanHistoColoCad aelSiscanHistoColoCad) {
		this.aelSiscanHistoColoCad = aelSiscanHistoColoCad;
	}
	
	public enum Fields {

		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		ISE_SOE_SEQ("aelItemSolicitacaoExames.id.soeSeq"),
		ISE_SEQP("aelItemSolicitacaoExames.id.seqp"),
		RESPOSTA("resposta"),
		SERVIDOR("servidor"),
		SHCC_CODIGO("aelSiscanHistoColoCad.codigo"), 
		ITEM_SOLICITACAO_EXAME("aelItemSolicitacaoExames"), 
		SISCAN_HISTO_COLO_CAD("aelSiscanHistoColoCad");
		
			
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
		if (!(obj instanceof AelSiscanHistoColoRes)) {
			return false;
		}
		AelSiscanHistoColoRes other = (AelSiscanHistoColoRes) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	
	
}