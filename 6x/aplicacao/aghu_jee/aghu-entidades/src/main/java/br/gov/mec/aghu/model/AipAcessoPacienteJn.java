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
@SequenceGenerator(name="aipAcxJnSeq", sequenceName="AGH.AIP_ACX_JN_SEQ", allocationSize = 1)
@Table(name = "AIP_ACESSO_PACIENTES_JN", schema = "AGH")
@Immutable
public class AipAcessoPacienteJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 3499329659070167318L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer seq;
	private String indAcessoSemAtend;
	private String indAcessoComAtend;
	private String indSituacao;
	private Date criadoEm;
	private Integer pacCodigo;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer serMatriculaAcessado;
	private Short serVinCodigoAcessado;

	public AipAcessoPacienteJn() {
	}

	public AipAcessoPacienteJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	public AipAcessoPacienteJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq, String indAcessoSemAtend,
			String indAcessoComAtend, String indSituacao, Date criadoEm, Integer pacCodigo, Integer serMatricula, Short serVinCodigo,
			Integer serMatriculaAcessado, Short serVinCodigoAcessado) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.indAcessoSemAtend = indAcessoSemAtend;
		this.indAcessoComAtend = indAcessoComAtend;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.pacCodigo = pacCodigo;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.serMatriculaAcessado = serMatriculaAcessado;
		this.serVinCodigoAcessado = serVinCodigoAcessado;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aipAcxJnSeq")
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

	@Column(name = "IND_ACESSO_SEM_ATEND", length = 1)
	@Length(max = 1)
	public String getIndAcessoSemAtend() {
		return this.indAcessoSemAtend;
	}

	public void setIndAcessoSemAtend(String indAcessoSemAtend) {
		this.indAcessoSemAtend = indAcessoSemAtend;
	}

	@Column(name = "IND_ACESSO_COM_ATEND", length = 1)
	@Length(max = 1)
	public String getIndAcessoComAtend() {
		return this.indAcessoComAtend;
	}

	public void setIndAcessoComAtend(String indAcessoComAtend) {
		this.indAcessoComAtend = indAcessoComAtend;
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

	@Column(name = "PAC_CODIGO")
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
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

	@Column(name = "SER_MATRICULA_ACESSADO")
	public Integer getSerMatriculaAcessado() {
		return this.serMatriculaAcessado;
	}

	public void setSerMatriculaAcessado(Integer serMatriculaAcessado) {
		this.serMatriculaAcessado = serMatriculaAcessado;
	}

	@Column(name = "SER_VIN_CODIGO_ACESSADO")
	public Short getSerVinCodigoAcessado() {
		return this.serVinCodigoAcessado;
	}

	public void setSerVinCodigoAcessado(Short serVinCodigoAcessado) {
		this.serVinCodigoAcessado = serVinCodigoAcessado;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		IND_ACESSO_SEM_ATEND("indAcessoSemAtend"),
		IND_ACESSO_COM_ATEND("indAcessoComAtend"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		PAC_CODIGO("pacCodigo"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA_ACESSADO("serMatriculaAcessado"),
		SER_VIN_CODIGO_ACESSADO("serVinCodigoAcessado");

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
