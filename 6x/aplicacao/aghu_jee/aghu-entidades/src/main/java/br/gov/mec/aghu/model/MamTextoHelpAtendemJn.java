package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * MamTextoHelpAtendemJn generated by hbm2java
 */
@Entity
@Table(name = "MAM_TEXTO_HELP_ATENDEM_JN", schema = "AGH")
@Immutable
public class MamTextoHelpAtendemJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 3242101115405204162L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer txhSeq;
	private String seqp;
	private Date criadoEm;
	private String indSituacao;
	private Integer qusQutSeq;
	private Short qusSeqp;
	private Integer qutSeq;
	private Integer serMatricula;
	private Short serVinCodigo;

	public MamTextoHelpAtendemJn() {
	}

	public MamTextoHelpAtendemJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer txhSeq, String seqp) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.txhSeq = txhSeq;
		this.seqp = seqp;
	}

	public MamTextoHelpAtendemJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer txhSeq, String seqp,
			Date criadoEm, String indSituacao, Integer qusQutSeq, Short qusSeqp, Integer qutSeq, Integer serMatricula,
			Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.txhSeq = txhSeq;
		this.seqp = seqp;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.qusQutSeq = qusQutSeq;
		this.qusSeqp = qusSeqp;
		this.qutSeq = qutSeq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	//@GeneratedValue(strategy = GenerationType.AUTO, generator = "")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	// ATUALIZADOR JOURNALS - ID
	
/* ATUALIZADOR JOURNALS - Get / Set	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Long getSeqJn() {
		return this.seqJn;
	}

	public void setSeqJn(Long seqJn) {
		this.seqJn = seqJn;
	}

	@Column(name = "JN_USER", nullable = false, length = 30)
	@Length(max = 30)
	public String getJnUser() {
		return this.jnUser;
	}

	public void setJnUser(String jnUser) {
		this.jnUser = jnUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "JN_DATE_TIME", nullable = false, length = 29)
	public Date getJnDateTime() {
		return this.jnDateTime;
	}

	public void setJnDateTime(Date jnDateTime) {
		this.jnDateTime = jnDateTime;
	}

	@Column(name = "JN_OPERATION", nullable = false, length = 3)
	@Length(max = 3)
	public String getJnOperation() {
		return this.jnOperation;
	}

	public void setJnOperation(String jnOperation) {
		this.jnOperation = jnOperation;
	}*/

	@Column(name = "TXH_SEQ", nullable = false)
	public Integer getTxhSeq() {
		return this.txhSeq;
	}

	public void setTxhSeq(Integer txhSeq) {
		this.txhSeq = txhSeq;
	}

	@Column(name = "SEQP", nullable = false, length = 4)
	@Length(max = 4)
	public String getSeqp() {
		return this.seqp;
	}

	public void setSeqp(String seqp) {
		this.seqp = seqp;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "QUS_QUT_SEQ")
	public Integer getQusQutSeq() {
		return this.qusQutSeq;
	}

	public void setQusQutSeq(Integer qusQutSeq) {
		this.qusQutSeq = qusQutSeq;
	}

	@Column(name = "QUS_SEQP")
	public Short getQusSeqp() {
		return this.qusSeqp;
	}

	public void setQusSeqp(Short qusSeqp) {
		this.qusSeqp = qusSeqp;
	}

	@Column(name = "QUT_SEQ")
	public Integer getQutSeq() {
		return this.qutSeq;
	}

	public void setQutSeq(Integer qutSeq) {
		this.qutSeq = qutSeq;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		TXH_SEQ("txhSeq"),
		SEQP("seqp"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		QUS_QUT_SEQ("qusQutSeq"),
		QUS_SEQP("qusSeqp"),
		QUT_SEQ("qutSeq"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");

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
