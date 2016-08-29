package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MAM_EMG_DEST_X_LOCAIS_JN", schema = "AGH")
@Immutable
public class MamEmgDestXLocalJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 8316341785109636467L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private String eipSeq;
	private String edeSeq;
	private String seq;
	private String criadoEm;
	private String indSituacao;
	private String serMatricula;
	private String serVinCodigo;

	public MamEmgDestXLocalJn() {
	}

	public MamEmgDestXLocalJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
	}

	public MamEmgDestXLocalJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, String eipSeq, String edeSeq,
			String seq, String criadoEm, String indSituacao, String serMatricula, String serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.eipSeq = eipSeq;
		this.edeSeq = edeSeq;
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
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

	@Column(name = "EIP_SEQ", length = 240)
	@Length(max = 240)
	public String getEipSeq() {
		return this.eipSeq;
	}

	public void setEipSeq(String eipSeq) {
		this.eipSeq = eipSeq;
	}

	@Column(name = "EDE_SEQ", length = 240)
	@Length(max = 240)
	public String getEdeSeq() {
		return this.edeSeq;
	}

	public void setEdeSeq(String edeSeq) {
		this.edeSeq = edeSeq;
	}

	@Column(name = "SEQ", length = 240)
	@Length(max = 240)
	public String getSeq() {
		return this.seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	@Column(name = "CRIADO_EM", length = 240)
	@Length(max = 240)
	public String getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", length = 240)
	@Length(max = 240)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "SER_MATRICULA", length = 240)
	@Length(max = 240)
	public String getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(String serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", length = 240)
	@Length(max = 240)
	public String getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(String serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		EIP_SEQ("eipSeq"),
		EDE_SEQ("edeSeq"),
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
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
