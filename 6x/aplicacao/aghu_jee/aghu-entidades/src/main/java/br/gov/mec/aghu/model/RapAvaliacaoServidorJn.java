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

/**
 * RapAvaliacaoServidorJn generated by hbm2java
 */
@Entity
@Table(name = "RAP_AVALIACAO_SERVIDORES_JN", schema = "AGH")
@Immutable
public class RapAvaliacaoServidorJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -4852687196829586868L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer seq;
	private Integer prfCodigo;
	private Integer serMatriculaAvaliado;
	private Short serVinCodigoAvaliado;
	private Integer serMatriculaAvaliador;
	private Short serVinCodigoAvaliador;
	private Date dtPrevAvaliacao;
	private Date dtAutoavaliacao;
	private Date dtAvaliacao;
	private Date dtResultado;
	private String indConsenso;
	private String indAaConcluida;
	private String indConcluida;
	private String ravCodigo;
	private String registroAvaliador;
	private String registroAvaliado;
	private Date criadoEm;
	private Integer serMatriculaCriado;
	private Short serVinCodigoCriado;
	private Date alteradoEm;
	private Integer serMatriculaAlterado;
	private Short serVinCodigoAlterado;
	private Integer cctCodigo;

	public RapAvaliacaoServidorJn() {
	}

	public RapAvaliacaoServidorJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public RapAvaliacaoServidorJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq, Integer prfCodigo,
			Integer serMatriculaAvaliado, Short serVinCodigoAvaliado, Integer serMatriculaAvaliador, Short serVinCodigoAvaliador,
			Date dtPrevAvaliacao, Date dtAutoavaliacao, Date dtAvaliacao, Date dtResultado, String indConsenso, String indAaConcluida,
			String indConcluida, String ravCodigo, String registroAvaliador, String registroAvaliado, Date criadoEm,
			Integer serMatriculaCriado, Short serVinCodigoCriado, Date alteradoEm, Integer serMatriculaAlterado,
			Short serVinCodigoAlterado, Integer cctCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.prfCodigo = prfCodigo;
		this.serMatriculaAvaliado = serMatriculaAvaliado;
		this.serVinCodigoAvaliado = serVinCodigoAvaliado;
		this.serMatriculaAvaliador = serMatriculaAvaliador;
		this.serVinCodigoAvaliador = serVinCodigoAvaliador;
		this.dtPrevAvaliacao = dtPrevAvaliacao;
		this.dtAutoavaliacao = dtAutoavaliacao;
		this.dtAvaliacao = dtAvaliacao;
		this.dtResultado = dtResultado;
		this.indConsenso = indConsenso;
		this.indAaConcluida = indAaConcluida;
		this.indConcluida = indConcluida;
		this.ravCodigo = ravCodigo;
		this.registroAvaliador = registroAvaliador;
		this.registroAvaliado = registroAvaliado;
		this.criadoEm = criadoEm;
		this.serMatriculaCriado = serMatriculaCriado;
		this.serVinCodigoCriado = serVinCodigoCriado;
		this.alteradoEm = alteradoEm;
		this.serMatriculaAlterado = serMatriculaAlterado;
		this.serVinCodigoAlterado = serVinCodigoAlterado;
		this.cctCodigo = cctCodigo;
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

	@Column(name = "PRF_CODIGO")
	public Integer getPrfCodigo() {
		return this.prfCodigo;
	}

	public void setPrfCodigo(Integer prfCodigo) {
		this.prfCodigo = prfCodigo;
	}

	@Column(name = "SER_MATRICULA_AVALIADO")
	public Integer getSerMatriculaAvaliado() {
		return this.serMatriculaAvaliado;
	}

	public void setSerMatriculaAvaliado(Integer serMatriculaAvaliado) {
		this.serMatriculaAvaliado = serMatriculaAvaliado;
	}

	@Column(name = "SER_VIN_CODIGO_AVALIADO")
	public Short getSerVinCodigoAvaliado() {
		return this.serVinCodigoAvaliado;
	}

	public void setSerVinCodigoAvaliado(Short serVinCodigoAvaliado) {
		this.serVinCodigoAvaliado = serVinCodigoAvaliado;
	}

	@Column(name = "SER_MATRICULA_AVALIADOR")
	public Integer getSerMatriculaAvaliador() {
		return this.serMatriculaAvaliador;
	}

	public void setSerMatriculaAvaliador(Integer serMatriculaAvaliador) {
		this.serMatriculaAvaliador = serMatriculaAvaliador;
	}

	@Column(name = "SER_VIN_CODIGO_AVALIADOR")
	public Short getSerVinCodigoAvaliador() {
		return this.serVinCodigoAvaliador;
	}

	public void setSerVinCodigoAvaliador(Short serVinCodigoAvaliador) {
		this.serVinCodigoAvaliador = serVinCodigoAvaliador;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_PREV_AVALIACAO", length = 29)
	public Date getDtPrevAvaliacao() {
		return this.dtPrevAvaliacao;
	}

	public void setDtPrevAvaliacao(Date dtPrevAvaliacao) {
		this.dtPrevAvaliacao = dtPrevAvaliacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_AUTOAVALIACAO", length = 29)
	public Date getDtAutoavaliacao() {
		return this.dtAutoavaliacao;
	}

	public void setDtAutoavaliacao(Date dtAutoavaliacao) {
		this.dtAutoavaliacao = dtAutoavaliacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_AVALIACAO", length = 29)
	public Date getDtAvaliacao() {
		return this.dtAvaliacao;
	}

	public void setDtAvaliacao(Date dtAvaliacao) {
		this.dtAvaliacao = dtAvaliacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_RESULTADO", length = 29)
	public Date getDtResultado() {
		return this.dtResultado;
	}

	public void setDtResultado(Date dtResultado) {
		this.dtResultado = dtResultado;
	}

	@Column(name = "IND_CONSENSO", length = 1)
	@Length(max = 1)
	public String getIndConsenso() {
		return this.indConsenso;
	}

	public void setIndConsenso(String indConsenso) {
		this.indConsenso = indConsenso;
	}

	@Column(name = "IND_AA_CONCLUIDA", length = 1)
	@Length(max = 1)
	public String getIndAaConcluida() {
		return this.indAaConcluida;
	}

	public void setIndAaConcluida(String indAaConcluida) {
		this.indAaConcluida = indAaConcluida;
	}

	@Column(name = "IND_CONCLUIDA", length = 1)
	@Length(max = 1)
	public String getIndConcluida() {
		return this.indConcluida;
	}

	public void setIndConcluida(String indConcluida) {
		this.indConcluida = indConcluida;
	}

	@Column(name = "RAV_CODIGO", length = 2)
	@Length(max = 2)
	public String getRavCodigo() {
		return this.ravCodigo;
	}

	public void setRavCodigo(String ravCodigo) {
		this.ravCodigo = ravCodigo;
	}

	@Column(name = "REGISTRO_AVALIADOR", length = 2000)
	@Length(max = 2000)
	public String getRegistroAvaliador() {
		return this.registroAvaliador;
	}

	public void setRegistroAvaliador(String registroAvaliador) {
		this.registroAvaliador = registroAvaliador;
	}

	@Column(name = "REGISTRO_AVALIADO", length = 2000)
	@Length(max = 2000)
	public String getRegistroAvaliado() {
		return this.registroAvaliado;
	}

	public void setRegistroAvaliado(String registroAvaliado) {
		this.registroAvaliado = registroAvaliado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA_CRIADO")
	public Integer getSerMatriculaCriado() {
		return this.serMatriculaCriado;
	}

	public void setSerMatriculaCriado(Integer serMatriculaCriado) {
		this.serMatriculaCriado = serMatriculaCriado;
	}

	@Column(name = "SER_VIN_CODIGO_CRIADO")
	public Short getSerVinCodigoCriado() {
		return this.serVinCodigoCriado;
	}

	public void setSerVinCodigoCriado(Short serVinCodigoCriado) {
		this.serVinCodigoCriado = serVinCodigoCriado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "SER_MATRICULA_ALTERADO")
	public Integer getSerMatriculaAlterado() {
		return this.serMatriculaAlterado;
	}

	public void setSerMatriculaAlterado(Integer serMatriculaAlterado) {
		this.serMatriculaAlterado = serMatriculaAlterado;
	}

	@Column(name = "SER_VIN_CODIGO_ALTERADO")
	public Short getSerVinCodigoAlterado() {
		return this.serVinCodigoAlterado;
	}

	public void setSerVinCodigoAlterado(Short serVinCodigoAlterado) {
		this.serVinCodigoAlterado = serVinCodigoAlterado;
	}

	@Column(name = "CCT_CODIGO")
	public Integer getCctCodigo() {
		return this.cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		PRF_CODIGO("prfCodigo"),
		SER_MATRICULA_AVALIADO("serMatriculaAvaliado"),
		SER_VIN_CODIGO_AVALIADO("serVinCodigoAvaliado"),
		SER_MATRICULA_AVALIADOR("serMatriculaAvaliador"),
		SER_VIN_CODIGO_AVALIADOR("serVinCodigoAvaliador"),
		DT_PREV_AVALIACAO("dtPrevAvaliacao"),
		DT_AUTOAVALIACAO("dtAutoavaliacao"),
		DT_AVALIACAO("dtAvaliacao"),
		DT_RESULTADO("dtResultado"),
		IND_CONSENSO("indConsenso"),
		IND_AA_CONCLUIDA("indAaConcluida"),
		IND_CONCLUIDA("indConcluida"),
		RAV_CODIGO("ravCodigo"),
		REGISTRO_AVALIADOR("registroAvaliador"),
		REGISTRO_AVALIADO("registroAvaliado"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA_CRIADO("serMatriculaCriado"),
		SER_VIN_CODIGO_CRIADO("serVinCodigoCriado"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA_ALTERADO("serMatriculaAlterado"),
		SER_VIN_CODIGO_ALTERADO("serVinCodigoAlterado"),
		CCT_CODIGO("cctCodigo");

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
