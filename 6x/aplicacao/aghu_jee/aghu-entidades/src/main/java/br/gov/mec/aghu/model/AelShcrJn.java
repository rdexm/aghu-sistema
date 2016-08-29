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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;



/**
 * The persistent class for the AEL_SHCR_JN database table.
 * 
 */
@Entity
@Table(name="AEL_SHCR_JN")
@SequenceGenerator(name="aelShcrJnSeq", sequenceName="AEL_SHCR_JN_SEQ")
public class AelShcrJn implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2006688013994004387L;
	private Integer seqJn;
	private Date criadoEm;
	private Short iseSeqp;
	private Integer iseSoeSeq;
	private String resposta;

	private String shccCodigo;
	private Long shcrSeq;
	private Integer version;
	private RapServidores servidor;

    public AelShcrJn() {
    }


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="aelShcrJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Integer getSeqJn() {
		return this.seqJn;
	}

	public void setSeqJn(Integer seqJn) {
		this.seqJn = seqJn;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	@Column(name="ISE_SEQP", nullable=false, precision=3)
	public Short getIseSeqp() {
		return this.iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}


	@Column(name="ISE_SOE_SEQ", nullable=false, precision=8)
	public Integer getIseSoeSeq() {
		return this.iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}


	@Column(name="RESPOSTA")
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


	@Column(name="SHCC_CODIGO", nullable=false, length=60)
	public String getShccCodigo() {
		return this.shccCodigo;
	}

	public void setShccCodigo(String shccCodigo) {
		this.shccCodigo = shccCodigo;
	}


	@Column(name="SHCR_SEQ", nullable=false, precision=9)
	public Long getShcrSeq() {
		return this.shcrSeq;
	}

	public void setShcrSeq(Long shcrSeq) {
		this.shcrSeq = shcrSeq;
	}


	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {

		SEQ_JN("seqJn"),
		CRIADO_EM("criadoEm"),
		ISE_SOE_SEQ("iseSoeSeq"),
		ISE_SEQP("iseSeqp"),
		RESPOSTA("resposta"),
		SERVIDOR("servidor"),
		SHCC_CODIGO("shccCodigo"),
		SHCR_SEQ("shcrSeq");
		
			
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
		result = prime * result + ((getSeqJn() == null) ? 0 : getSeqJn().hashCode());
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
		if (!(obj instanceof AelShcrJn)) {
			return false;
		}
		AelShcrJn other = (AelShcrJn) obj;
		if (getSeqJn() == null) {
			if (other.getSeqJn() != null) {
				return false;
			}
		} else if (!getSeqJn().equals(other.getSeqJn())) {
			return false;
		}
		return true;
	}

}