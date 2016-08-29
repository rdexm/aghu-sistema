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
@Table(name = "MAM_CONSULTORIA_AMB_EQUIPES_JN", schema = "AGH")
@Immutable
public class MamConsultoriaAmbEquipeJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 671686736442522021L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer seq;
	private Short espSeq;
	private Short eqpSeq;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String indSituacao;
	private Date alteradoEm;
	private Integer serMatriculaAltera;
	private Short serVinCodigoAltera;

	public MamConsultoriaAmbEquipeJn() {
	}

	public MamConsultoriaAmbEquipeJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	public MamConsultoriaAmbEquipeJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq, Short espSeq,
			Short eqpSeq, Date criadoEm, Integer serMatricula, Short serVinCodigo, String indSituacao, Date alteradoEm,
			Integer serMatriculaAltera, Short serVinCodigoAltera) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.espSeq = espSeq;
		this.eqpSeq = eqpSeq;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.indSituacao = indSituacao;
		this.alteradoEm = alteradoEm;
		this.serMatriculaAltera = serMatriculaAltera;
		this.serVinCodigoAltera = serVinCodigoAltera;
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
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "ESP_SEQ")
	public Short getEspSeq() {
		return this.espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	@Column(name = "EQP_SEQ")
	public Short getEqpSeq() {
		return this.eqpSeq;
	}

	public void setEqpSeq(Short eqpSeq) {
		this.eqpSeq = eqpSeq;
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

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "SER_MATRICULA_ALTERA")
	public Integer getSerMatriculaAltera() {
		return this.serMatriculaAltera;
	}

	public void setSerMatriculaAltera(Integer serMatriculaAltera) {
		this.serMatriculaAltera = serMatriculaAltera;
	}

	@Column(name = "SER_VIN_CODIGO_ALTERA")
	public Short getSerVinCodigoAltera() {
		return this.serVinCodigoAltera;
	}

	public void setSerVinCodigoAltera(Short serVinCodigoAltera) {
		this.serVinCodigoAltera = serVinCodigoAltera;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		ESP_SEQ("espSeq"),
		EQP_SEQ("eqpSeq"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		IND_SITUACAO("indSituacao"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA_ALTERA("serMatriculaAltera"),
		SER_VIN_CODIGO_ALTERA("serVinCodigoAltera");

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
