package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="aelTexJnSeq", sequenceName="AGH.AEL_TEX_JN_SEQ", allocationSize = 1)
@Table(name = "AEL_TIPOS_AMO_EXAME_CONV_JN", schema = "AGH")
@Immutable
public class AelTipoAmoExameConvJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -3061512392499181370L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private String taeEmaExaSigla;
	private Integer taeEmaManSeq;
	private Integer taeManSeq;
	private String taeOrigemAtendimento;
	private Short cspCnvCodigo;
	private Short cspSeq;
	private String responsavelColeta;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public AelTipoAmoExameConvJn() {
	}

	public AelTipoAmoExameConvJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, String taeEmaExaSigla,
			Integer taeEmaManSeq, Integer taeManSeq, String taeOrigemAtendimento, Short cspCnvCodigo, Short cspSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.taeEmaExaSigla = taeEmaExaSigla;
		this.taeEmaManSeq = taeEmaManSeq;
		this.taeManSeq = taeManSeq;
		this.taeOrigemAtendimento = taeOrigemAtendimento;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
	}

	public AelTipoAmoExameConvJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, String taeEmaExaSigla,
			Integer taeEmaManSeq, Integer taeManSeq, String taeOrigemAtendimento, Short cspCnvCodigo, Short cspSeq,
			String responsavelColeta, Date criadoEm, Integer serMatricula, Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.taeEmaExaSigla = taeEmaExaSigla;
		this.taeEmaManSeq = taeEmaManSeq;
		this.taeManSeq = taeManSeq;
		this.taeOrigemAtendimento = taeOrigemAtendimento;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
		this.responsavelColeta = responsavelColeta;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelTexJnSeq")
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

	@Column(name = "TAE_EMA_EXA_SIGLA", nullable = false, length = 5)
	@Length(max = 5)
	public String getTaeEmaExaSigla() {
		return this.taeEmaExaSigla;
	}

	public void setTaeEmaExaSigla(String taeEmaExaSigla) {
		this.taeEmaExaSigla = taeEmaExaSigla;
	}

	@Column(name = "TAE_EMA_MAN_SEQ", nullable = false)
	public Integer getTaeEmaManSeq() {
		return this.taeEmaManSeq;
	}

	public void setTaeEmaManSeq(Integer taeEmaManSeq) {
		this.taeEmaManSeq = taeEmaManSeq;
	}

	@Column(name = "TAE_MAN_SEQ", nullable = false)
	public Integer getTaeManSeq() {
		return this.taeManSeq;
	}

	public void setTaeManSeq(Integer taeManSeq) {
		this.taeManSeq = taeManSeq;
	}

	@Column(name = "TAE_ORIGEM_ATENDIMENTO", nullable = false, length = 1)
	@Length(max = 1)
	public String getTaeOrigemAtendimento() {
		return this.taeOrigemAtendimento;
	}

	public void setTaeOrigemAtendimento(String taeOrigemAtendimento) {
		this.taeOrigemAtendimento = taeOrigemAtendimento;
	}

	@Column(name = "CSP_CNV_CODIGO", nullable = false)
	public Short getCspCnvCodigo() {
		return this.cspCnvCodigo;
	}

	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	@Column(name = "CSP_SEQ", nullable = false)
	public Short getCspSeq() {
		return this.cspSeq;
	}

	public void setCspSeq(Short cspSeq) {
		this.cspSeq = cspSeq;
	}

	@Column(name = "RESPONSAVEL_COLETA", length = 1)
	public String getResponsavelColeta() {
		return this.responsavelColeta;
	}

	public void setResponsavelColeta(String responsavelColeta) {
		this.responsavelColeta = responsavelColeta;
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
		TAE_EMA_EXA_SIGLA("taeEmaExaSigla"),
		TAE_EMA_MAN_SEQ("taeEmaManSeq"),
		TAE_MAN_SEQ("taeManSeq"),
		TAE_ORIGEM_ATENDIMENTO("taeOrigemAtendimento"),
		CSP_CNV_CODIGO("cspCnvCodigo"),
		CSP_SEQ("cspSeq"),
		RESPONSAVEL_COLETA("responsavelColeta"),
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
