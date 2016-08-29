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
@Table(name = "ABS_EXAME_METODO_CONFIRMS_JN", schema = "AGH")
@Immutable
public class AbsExameMetodoConfirmJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 6918585146394943514L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Short emmMetSeq;
	private String emmUfeEmaExaSigla;
	private Integer emmUfeEmaManSeq;
	private Short emmUfeUnfSeq;
	private Short emmMetSeqFilho;
	private String emmUfeEmaExaSiglaFilho;
	private Integer emmUfeEmaManSeqFilho;
	private Short emmUfeUnfSeqFilho;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date criadoEm;

	public AbsExameMetodoConfirmJn() {
	}

	public AbsExameMetodoConfirmJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short emmMetSeq,
			String emmUfeEmaExaSigla, Integer emmUfeEmaManSeq, Short emmUfeUnfSeq, Short emmMetSeqFilho, String emmUfeEmaExaSiglaFilho,
			Integer emmUfeEmaManSeqFilho, Short emmUfeUnfSeqFilho) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.emmMetSeq = emmMetSeq;
		this.emmUfeEmaExaSigla = emmUfeEmaExaSigla;
		this.emmUfeEmaManSeq = emmUfeEmaManSeq;
		this.emmUfeUnfSeq = emmUfeUnfSeq;
		this.emmMetSeqFilho = emmMetSeqFilho;
		this.emmUfeEmaExaSiglaFilho = emmUfeEmaExaSiglaFilho;
		this.emmUfeEmaManSeqFilho = emmUfeEmaManSeqFilho;
		this.emmUfeUnfSeqFilho = emmUfeUnfSeqFilho;
	}

	public AbsExameMetodoConfirmJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short emmMetSeq,
			String emmUfeEmaExaSigla, Integer emmUfeEmaManSeq, Short emmUfeUnfSeq, Short emmMetSeqFilho, String emmUfeEmaExaSiglaFilho,
			Integer emmUfeEmaManSeqFilho, Short emmUfeUnfSeqFilho, Integer serMatricula, Short serVinCodigo, Date criadoEm) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.emmMetSeq = emmMetSeq;
		this.emmUfeEmaExaSigla = emmUfeEmaExaSigla;
		this.emmUfeEmaManSeq = emmUfeEmaManSeq;
		this.emmUfeUnfSeq = emmUfeUnfSeq;
		this.emmMetSeqFilho = emmMetSeqFilho;
		this.emmUfeEmaExaSiglaFilho = emmUfeEmaExaSiglaFilho;
		this.emmUfeEmaManSeqFilho = emmUfeEmaManSeqFilho;
		this.emmUfeUnfSeqFilho = emmUfeUnfSeqFilho;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.criadoEm = criadoEm;
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

	@Column(name = "EMM_MET_SEQ", nullable = false)
	public Short getEmmMetSeq() {
		return this.emmMetSeq;
	}

	public void setEmmMetSeq(Short emmMetSeq) {
		this.emmMetSeq = emmMetSeq;
	}

	@Column(name = "EMM_UFE_EMA_EXA_SIGLA", nullable = false, length = 5)
	@Length(max = 5)
	public String getEmmUfeEmaExaSigla() {
		return this.emmUfeEmaExaSigla;
	}

	public void setEmmUfeEmaExaSigla(String emmUfeEmaExaSigla) {
		this.emmUfeEmaExaSigla = emmUfeEmaExaSigla;
	}

	@Column(name = "EMM_UFE_EMA_MAN_SEQ", nullable = false)
	public Integer getEmmUfeEmaManSeq() {
		return this.emmUfeEmaManSeq;
	}

	public void setEmmUfeEmaManSeq(Integer emmUfeEmaManSeq) {
		this.emmUfeEmaManSeq = emmUfeEmaManSeq;
	}

	@Column(name = "EMM_UFE_UNF_SEQ", nullable = false)
	public Short getEmmUfeUnfSeq() {
		return this.emmUfeUnfSeq;
	}

	public void setEmmUfeUnfSeq(Short emmUfeUnfSeq) {
		this.emmUfeUnfSeq = emmUfeUnfSeq;
	}

	@Column(name = "EMM_MET_SEQ_FILHO", nullable = false)
	public Short getEmmMetSeqFilho() {
		return this.emmMetSeqFilho;
	}

	public void setEmmMetSeqFilho(Short emmMetSeqFilho) {
		this.emmMetSeqFilho = emmMetSeqFilho;
	}

	@Column(name = "EMM_UFE_EMA_EXA_SIGLA_FILHO", nullable = false, length = 5)
	@Length(max = 5)
	public String getEmmUfeEmaExaSiglaFilho() {
		return this.emmUfeEmaExaSiglaFilho;
	}

	public void setEmmUfeEmaExaSiglaFilho(String emmUfeEmaExaSiglaFilho) {
		this.emmUfeEmaExaSiglaFilho = emmUfeEmaExaSiglaFilho;
	}

	@Column(name = "EMM_UFE_EMA_MAN_SEQ_FILHO", nullable = false)
	public Integer getEmmUfeEmaManSeqFilho() {
		return this.emmUfeEmaManSeqFilho;
	}

	public void setEmmUfeEmaManSeqFilho(Integer emmUfeEmaManSeqFilho) {
		this.emmUfeEmaManSeqFilho = emmUfeEmaManSeqFilho;
	}

	@Column(name = "EMM_UFE_UNF_SEQ_FILHO", nullable = false)
	public Short getEmmUfeUnfSeqFilho() {
		return this.emmUfeUnfSeqFilho;
	}

	public void setEmmUfeUnfSeqFilho(Short emmUfeUnfSeqFilho) {
		this.emmUfeUnfSeqFilho = emmUfeUnfSeqFilho;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		EMM_MET_SEQ("emmMetSeq"),
		EMM_UFE_EMA_EXA_SIGLA("emmUfeEmaExaSigla"),
		EMM_UFE_EMA_MAN_SEQ("emmUfeEmaManSeq"),
		EMM_UFE_UNF_SEQ("emmUfeUnfSeq"),
		EMM_MET_SEQ_FILHO("emmMetSeqFilho"),
		EMM_UFE_EMA_EXA_SIGLA_FILHO("emmUfeEmaExaSiglaFilho"),
		EMM_UFE_EMA_MAN_SEQ_FILHO("emmUfeEmaManSeqFilho"),
		EMM_UFE_UNF_SEQ_FILHO("emmUfeUnfSeqFilho"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		CRIADO_EM("criadoEm");

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
