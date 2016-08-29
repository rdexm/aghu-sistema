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
@Table(name = "AEL_PROJETO_INTERC_COMPS_JN", schema = "AGH")
@Immutable
public class AelProjetoIntercCompJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 9138867357522541890L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Short seqp;
	private Date criadoEm;
	private Short qtde;
	private String justificativa;
	private String efetivado;
	private Integer ppjPjqSeq;
	private Integer ppjPacCodigo;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String csaCodigo;

	public AelProjetoIntercCompJn() {
	}

	public AelProjetoIntercCompJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seqp, Integer ppjPjqSeq,
			Integer ppjPacCodigo, String csaCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seqp = seqp;
		this.ppjPjqSeq = ppjPjqSeq;
		this.ppjPacCodigo = ppjPacCodigo;
		this.csaCodigo = csaCodigo;
	}

	public AelProjetoIntercCompJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seqp, Date criadoEm,
			Short qtde, String justificativa, String efetivado, Integer ppjPjqSeq, Integer ppjPacCodigo, Integer serMatricula,
			Short serVinCodigo, String csaCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seqp = seqp;
		this.criadoEm = criadoEm;
		this.qtde = qtde;
		this.justificativa = justificativa;
		this.efetivado = efetivado;
		this.ppjPjqSeq = ppjPjqSeq;
		this.ppjPacCodigo = ppjPacCodigo;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.csaCodigo = csaCodigo;
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
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "QTDE")
	public Short getQtde() {
		return this.qtde;
	}

	public void setQtde(Short qtde) {
		this.qtde = qtde;
	}

	@Column(name = "JUSTIFICATIVA", length = 4000)
	@Length(max = 4000)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Column(name = "EFETIVADO", length = 1)
	@Length(max = 1)
	public String getEfetivado() {
		return this.efetivado;
	}

	public void setEfetivado(String efetivado) {
		this.efetivado = efetivado;
	}

	@Column(name = "PPJ_PJQ_SEQ", nullable = false)
	public Integer getPpjPjqSeq() {
		return this.ppjPjqSeq;
	}

	public void setPpjPjqSeq(Integer ppjPjqSeq) {
		this.ppjPjqSeq = ppjPjqSeq;
	}

	@Column(name = "PPJ_PAC_CODIGO", nullable = false)
	public Integer getPpjPacCodigo() {
		return this.ppjPacCodigo;
	}

	public void setPpjPacCodigo(Integer ppjPacCodigo) {
		this.ppjPacCodigo = ppjPacCodigo;
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

	@Column(name = "CSA_CODIGO", nullable = false, length = 2)
	@Length(max = 2)
	public String getCsaCodigo() {
		return this.csaCodigo;
	}

	public void setCsaCodigo(String csaCodigo) {
		this.csaCodigo = csaCodigo;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQP("seqp"),
		CRIADO_EM("criadoEm"),
		QTDE("qtde"),
		JUSTIFICATIVA("justificativa"),
		EFETIVADO("efetivado"),
		PPJ_PJQ_SEQ("ppjPjqSeq"),
		PPJ_PAC_CODIGO("ppjPacCodigo"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		CSA_CODIGO("csaCodigo");

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
