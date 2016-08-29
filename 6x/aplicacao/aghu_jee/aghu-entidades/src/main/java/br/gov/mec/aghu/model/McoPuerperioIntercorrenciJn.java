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

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MCO_PUERPERIO_INTERCORRENCI_JN", schema = "AGH")
@Immutable
public class McoPuerperioIntercorrenciJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -6823937118799341982L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Short seqp;
	private Date dthrIntercorrencia;
	private Date criadoEm;
	private Short pueGsoSeqp;
	private Integer pueGsoPacCodigo;
	private Short icrSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short obsSeq;

	public McoPuerperioIntercorrenciJn() {
	}

	public McoPuerperioIntercorrenciJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seqp, Short pueGsoSeqp,
			Integer pueGsoPacCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seqp = seqp;
		this.pueGsoSeqp = pueGsoSeqp;
		this.pueGsoPacCodigo = pueGsoPacCodigo;
	}

	public McoPuerperioIntercorrenciJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seqp,
			Date dthrIntercorrencia, Date criadoEm, Short pueGsoSeqp, Integer pueGsoPacCodigo, Short icrSeq, Integer serMatricula,
			Short serVinCodigo, Short obsSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seqp = seqp;
		this.dthrIntercorrencia = dthrIntercorrencia;
		this.criadoEm = criadoEm;
		this.pueGsoSeqp = pueGsoSeqp;
		this.pueGsoPacCodigo = pueGsoPacCodigo;
		this.icrSeq = icrSeq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.obsSeq = obsSeq;
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

	@Column(name = "SEQP", nullable = false)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INTERCORRENCIA", length = 29)
	public Date getDthrIntercorrencia() {
		return this.dthrIntercorrencia;
	}

	public void setDthrIntercorrencia(Date dthrIntercorrencia) {
		this.dthrIntercorrencia = dthrIntercorrencia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "PUE_GSO_SEQP", nullable = false)
	public Short getPueGsoSeqp() {
		return this.pueGsoSeqp;
	}

	public void setPueGsoSeqp(Short pueGsoSeqp) {
		this.pueGsoSeqp = pueGsoSeqp;
	}

	@Column(name = "PUE_GSO_PAC_CODIGO", nullable = false)
	public Integer getPueGsoPacCodigo() {
		return this.pueGsoPacCodigo;
	}

	public void setPueGsoPacCodigo(Integer pueGsoPacCodigo) {
		this.pueGsoPacCodigo = pueGsoPacCodigo;
	}

	@Column(name = "ICR_SEQ")
	public Short getIcrSeq() {
		return this.icrSeq;
	}

	public void setIcrSeq(Short icrSeq) {
		this.icrSeq = icrSeq;
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

	@Column(name = "OBS_SEQ")
	public Short getObsSeq() {
		return this.obsSeq;
	}

	public void setObsSeq(Short obsSeq) {
		this.obsSeq = obsSeq;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQP("seqp"),
		DTHR_INTERCORRENCIA("dthrIntercorrencia"),
		CRIADO_EM("criadoEm"),
		PUE_GSO_SEQP("pueGsoSeqp"),
		PUE_GSO_PAC_CODIGO("pueGsoPacCodigo"),
		ICR_SEQ("icrSeq"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		OBS_SEQ("obsSeq");

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
