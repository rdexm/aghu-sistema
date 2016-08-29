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

@Entity
@Table(name = "MCI_ITEM_CONDICAO_CLINICAS_JN", schema = "AGH")
@Immutable
public class MciItemCondicaoClinicaJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 3473919477212638392L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Short seq;
	private String indSituacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short cclSeq;
	private Short fpdSeq;
	private Integer cifSeq;
	private Short porSeq;
	private Short topSeq;

	public MciItemCondicaoClinicaJn() {
	}

	public MciItemCondicaoClinicaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	public MciItemCondicaoClinicaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq, String indSituacao,
			Date criadoEm, Integer serMatricula, Short serVinCodigo, Short cclSeq, Short fpdSeq, Integer cifSeq, Short porSeq,
			Short topSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.cclSeq = cclSeq;
		this.fpdSeq = fpdSeq;
		this.cifSeq = cifSeq;
		this.porSeq = porSeq;
		this.topSeq = topSeq;
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

	@Column(name = "SEQ", nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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

	@Column(name = "CCL_SEQ")
	public Short getCclSeq() {
		return this.cclSeq;
	}

	public void setCclSeq(Short cclSeq) {
		this.cclSeq = cclSeq;
	}

	@Column(name = "FPD_SEQ")
	public Short getFpdSeq() {
		return this.fpdSeq;
	}

	public void setFpdSeq(Short fpdSeq) {
		this.fpdSeq = fpdSeq;
	}

	@Column(name = "CIF_SEQ")
	public Integer getCifSeq() {
		return this.cifSeq;
	}

	public void setCifSeq(Integer cifSeq) {
		this.cifSeq = cifSeq;
	}

	@Column(name = "POR_SEQ")
	public Short getPorSeq() {
		return this.porSeq;
	}

	public void setPorSeq(Short porSeq) {
		this.porSeq = porSeq;
	}

	@Column(name = "TOP_SEQ")
	public Short getTopSeq() {
		return this.topSeq;
	}

	public void setTopSeq(Short topSeq) {
		this.topSeq = topSeq;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		CCL_SEQ("cclSeq"),
		FPD_SEQ("fpdSeq"),
		CIF_SEQ("cifSeq"),
		POR_SEQ("porSeq"),
		TOP_SEQ("topSeq");

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
