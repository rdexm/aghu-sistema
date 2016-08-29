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
@Table(name = "AEL_GRUPO_EXAME_PRIORIZAS_JN", schema = "AGH")
@Immutable
public class AelGrupoExamePriorizaJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -8046946699824705819L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer grxGrmSeq;
	private String grxEmaExaSigla;
	private Integer grxEmaManSeq;
	private String emaExaSigla;
	private Integer emaManSeq;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public AelGrupoExamePriorizaJn() {
	}

	public AelGrupoExamePriorizaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer grxGrmSeq,
			String grxEmaExaSigla, Integer grxEmaManSeq, String emaExaSigla, Integer emaManSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.grxGrmSeq = grxGrmSeq;
		this.grxEmaExaSigla = grxEmaExaSigla;
		this.grxEmaManSeq = grxEmaManSeq;
		this.emaExaSigla = emaExaSigla;
		this.emaManSeq = emaManSeq;
	}

	public AelGrupoExamePriorizaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer grxGrmSeq,
			String grxEmaExaSigla, Integer grxEmaManSeq, String emaExaSigla, Integer emaManSeq, Date criadoEm, Integer serMatricula,
			Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.grxGrmSeq = grxGrmSeq;
		this.grxEmaExaSigla = grxEmaExaSigla;
		this.grxEmaManSeq = grxEmaManSeq;
		this.emaExaSigla = emaExaSigla;
		this.emaManSeq = emaManSeq;
		this.criadoEm = criadoEm;
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

	@Column(name = "GRX_GRM_SEQ", nullable = false)
	public Integer getGrxGrmSeq() {
		return this.grxGrmSeq;
	}

	public void setGrxGrmSeq(Integer grxGrmSeq) {
		this.grxGrmSeq = grxGrmSeq;
	}

	@Column(name = "GRX_EMA_EXA_SIGLA", nullable = false, length = 5)
	@Length(max = 5)
	public String getGrxEmaExaSigla() {
		return this.grxEmaExaSigla;
	}

	public void setGrxEmaExaSigla(String grxEmaExaSigla) {
		this.grxEmaExaSigla = grxEmaExaSigla;
	}

	@Column(name = "GRX_EMA_MAN_SEQ", nullable = false)
	public Integer getGrxEmaManSeq() {
		return this.grxEmaManSeq;
	}

	public void setGrxEmaManSeq(Integer grxEmaManSeq) {
		this.grxEmaManSeq = grxEmaManSeq;
	}

	@Column(name = "EMA_EXA_SIGLA", nullable = false, length = 5)
	@Length(max = 5)
	public String getEmaExaSigla() {
		return this.emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	@Column(name = "EMA_MAN_SEQ", nullable = false)
	public Integer getEmaManSeq() {
		return this.emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
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

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		GRX_GRM_SEQ("grxGrmSeq"),
		GRX_EMA_EXA_SIGLA("grxEmaExaSigla"),
		GRX_EMA_MAN_SEQ("grxEmaManSeq"),
		EMA_EXA_SIGLA("emaExaSigla"),
		EMA_MAN_SEQ("emaManSeq"),
		CRIADO_EM("criadoEm"),
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
