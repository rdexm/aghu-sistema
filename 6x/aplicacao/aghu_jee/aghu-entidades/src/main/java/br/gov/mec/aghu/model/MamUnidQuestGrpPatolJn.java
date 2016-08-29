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
@Table(name = "MAM_UNID_QUEST_GRP_PATOLS_JN", schema = "AGH")
@Immutable
public class MamUnidQuestGrpPatolJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -5144477259551384846L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Short unqUnfSeq;
	private Integer unqQutSeq;
	private Short eplSeq;
	private Date criadoEm;
	private String indSituacao;
	private Integer serMatricula;
	private Short serVinCodigo;

	public MamUnidQuestGrpPatolJn() {
	}

	public MamUnidQuestGrpPatolJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short unqUnfSeq, Integer unqQutSeq,
			Short eplSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.unqUnfSeq = unqUnfSeq;
		this.unqQutSeq = unqQutSeq;
		this.eplSeq = eplSeq;
	}

	public MamUnidQuestGrpPatolJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short unqUnfSeq, Integer unqQutSeq,
			Short eplSeq, Date criadoEm, String indSituacao, Integer serMatricula, Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.unqUnfSeq = unqUnfSeq;
		this.unqQutSeq = unqQutSeq;
		this.eplSeq = eplSeq;
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

	@Column(name = "UNQ_UNF_SEQ", nullable = false)
	public Short getUnqUnfSeq() {
		return this.unqUnfSeq;
	}

	public void setUnqUnfSeq(Short unqUnfSeq) {
		this.unqUnfSeq = unqUnfSeq;
	}

	@Column(name = "UNQ_QUT_SEQ", nullable = false)
	public Integer getUnqQutSeq() {
		return this.unqQutSeq;
	}

	public void setUnqQutSeq(Integer unqQutSeq) {
		this.unqQutSeq = unqQutSeq;
	}

	@Column(name = "EPL_SEQ", nullable = false)
	public Short getEplSeq() {
		return this.eplSeq;
	}

	public void setEplSeq(Short eplSeq) {
		this.eplSeq = eplSeq;
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
		UNQ_UNF_SEQ("unqUnfSeq"),
		UNQ_QUT_SEQ("unqQutSeq"),
		EPL_SEQ("eplSeq"),
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
