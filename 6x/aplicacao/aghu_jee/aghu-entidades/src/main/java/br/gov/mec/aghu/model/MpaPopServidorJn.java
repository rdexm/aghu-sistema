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
@Table(name = "MPA_POP_SERVIDORES_JN", schema = "AGH")
@Immutable
public class MpaPopServidorJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -7071640780266313935L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer povPopSeq;
	private Short povVersao;
	private Short seqp;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date criadoEm;
	private Date dtInicioAutorizacao;
	private Date dtFimAutorizacao;
	private Integer serMatriculaInfInicio;
	private Short serVinCodigoInfFim;
	private Short serVinCodigoInfInicio;
	private Integer serMatriculaInfFim;

	public MpaPopServidorJn() {
	}

	public MpaPopServidorJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer povPopSeq, Short povVersao,
			Short seqp) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.povPopSeq = povPopSeq;
		this.povVersao = povVersao;
		this.seqp = seqp;
	}

	public MpaPopServidorJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer povPopSeq, Short povVersao,
			Short seqp, Integer serMatricula, Short serVinCodigo, Date criadoEm, Date dtInicioAutorizacao, Date dtFimAutorizacao,
			Integer serMatriculaInfInicio, Short serVinCodigoInfFim, Short serVinCodigoInfInicio, Integer serMatriculaInfFim) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.povPopSeq = povPopSeq;
		this.povVersao = povVersao;
		this.seqp = seqp;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.criadoEm = criadoEm;
		this.dtInicioAutorizacao = dtInicioAutorizacao;
		this.dtFimAutorizacao = dtFimAutorizacao;
		this.serMatriculaInfInicio = serMatriculaInfInicio;
		this.serVinCodigoInfFim = serVinCodigoInfFim;
		this.serVinCodigoInfInicio = serVinCodigoInfInicio;
		this.serMatriculaInfFim = serMatriculaInfFim;
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

	@Column(name = "POV_POP_SEQ", nullable = false)
	public Integer getPovPopSeq() {
		return this.povPopSeq;
	}

	public void setPovPopSeq(Integer povPopSeq) {
		this.povPopSeq = povPopSeq;
	}

	@Column(name = "POV_VERSAO", nullable = false)
	public Short getPovVersao() {
		return this.povVersao;
	}

	public void setPovVersao(Short povVersao) {
		this.povVersao = povVersao;
	}

	@Column(name = "SEQP", nullable = false)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INICIO_AUTORIZACAO", length = 29)
	public Date getDtInicioAutorizacao() {
		return this.dtInicioAutorizacao;
	}

	public void setDtInicioAutorizacao(Date dtInicioAutorizacao) {
		this.dtInicioAutorizacao = dtInicioAutorizacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM_AUTORIZACAO", length = 29)
	public Date getDtFimAutorizacao() {
		return this.dtFimAutorizacao;
	}

	public void setDtFimAutorizacao(Date dtFimAutorizacao) {
		this.dtFimAutorizacao = dtFimAutorizacao;
	}

	@Column(name = "SER_MATRICULA_INF_INICIO")
	public Integer getSerMatriculaInfInicio() {
		return this.serMatriculaInfInicio;
	}

	public void setSerMatriculaInfInicio(Integer serMatriculaInfInicio) {
		this.serMatriculaInfInicio = serMatriculaInfInicio;
	}

	@Column(name = "SER_VIN_CODIGO_INF_FIM")
	public Short getSerVinCodigoInfFim() {
		return this.serVinCodigoInfFim;
	}

	public void setSerVinCodigoInfFim(Short serVinCodigoInfFim) {
		this.serVinCodigoInfFim = serVinCodigoInfFim;
	}

	@Column(name = "SER_VIN_CODIGO_INF_INICIO")
	public Short getSerVinCodigoInfInicio() {
		return this.serVinCodigoInfInicio;
	}

	public void setSerVinCodigoInfInicio(Short serVinCodigoInfInicio) {
		this.serVinCodigoInfInicio = serVinCodigoInfInicio;
	}

	@Column(name = "SER_MATRICULA_INF_FIM")
	public Integer getSerMatriculaInfFim() {
		return this.serMatriculaInfFim;
	}

	public void setSerMatriculaInfFim(Integer serMatriculaInfFim) {
		this.serMatriculaInfFim = serMatriculaInfFim;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		POV_POP_SEQ("povPopSeq"),
		POV_VERSAO("povVersao"),
		SEQP("seqp"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		CRIADO_EM("criadoEm"),
		DT_INICIO_AUTORIZACAO("dtInicioAutorizacao"),
		DT_FIM_AUTORIZACAO("dtFimAutorizacao"),
		SER_MATRICULA_INF_INICIO("serMatriculaInfInicio"),
		SER_VIN_CODIGO_INF_FIM("serVinCodigoInfFim"),
		SER_VIN_CODIGO_INF_INICIO("serVinCodigoInfInicio"),
		SER_MATRICULA_INF_FIM("serMatriculaInfFim");

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
