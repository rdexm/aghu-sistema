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

/**
 * FsoPrevisaoOrcamentariaJn generated by hbm2java
 */
@Entity
@Table(name = "FSO_PREVISOES_ORCAMENTARIAS_JN", schema = "AGH")
@Immutable
public class FsoPrevisaoOrcamentariaJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -4412771275389552249L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Short cvfCodigo;
	private Date exoExercicio;
	private Short seqp;
	private Integer gndCodigo;
	private Integer ntdGndCodigo;
	private Short ntdCodigo;
	private Double valorOrcado;
	private Double valorComprometido;
	private Double valorRealizado;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer serMatriculaAlterado;
	private Short serVinCodigoAlterado;
	private Long ptbCodigo;

	public FsoPrevisaoOrcamentariaJn() {
	}

	public FsoPrevisaoOrcamentariaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Date exoExercicio, Short seqp) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.exoExercicio = exoExercicio;
		this.seqp = seqp;
	}

	public FsoPrevisaoOrcamentariaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short cvfCodigo,
			Date exoExercicio, Short seqp, Integer gndCodigo, Integer ntdGndCodigo, Short ntdCodigo, Double valorOrcado,
			Double valorComprometido, Double valorRealizado, Date criadoEm, Date alteradoEm, Integer serMatricula, Short serVinCodigo,
			Integer serMatriculaAlterado, Short serVinCodigoAlterado, Long ptbCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.cvfCodigo = cvfCodigo;
		this.exoExercicio = exoExercicio;
		this.seqp = seqp;
		this.gndCodigo = gndCodigo;
		this.ntdGndCodigo = ntdGndCodigo;
		this.ntdCodigo = ntdCodigo;
		this.valorOrcado = valorOrcado;
		this.valorComprometido = valorComprometido;
		this.valorRealizado = valorRealizado;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.serMatriculaAlterado = serMatriculaAlterado;
		this.serVinCodigoAlterado = serVinCodigoAlterado;
		this.ptbCodigo = ptbCodigo;
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

	@Column(name = "CVF_CODIGO")
	public Short getCvfCodigo() {
		return this.cvfCodigo;
	}

	public void setCvfCodigo(Short cvfCodigo) {
		this.cvfCodigo = cvfCodigo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EXO_EXERCICIO", nullable = false, length = 29)
	public Date getExoExercicio() {
		return this.exoExercicio;
	}

	public void setExoExercicio(Date exoExercicio) {
		this.exoExercicio = exoExercicio;
	}

	@Column(name = "SEQP", nullable = false)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Column(name = "GND_CODIGO")
	public Integer getGndCodigo() {
		return this.gndCodigo;
	}

	public void setGndCodigo(Integer gndCodigo) {
		this.gndCodigo = gndCodigo;
	}

	@Column(name = "NTD_GND_CODIGO")
	public Integer getNtdGndCodigo() {
		return this.ntdGndCodigo;
	}

	public void setNtdGndCodigo(Integer ntdGndCodigo) {
		this.ntdGndCodigo = ntdGndCodigo;
	}

	@Column(name = "NTD_CODIGO")
	public Short getNtdCodigo() {
		return this.ntdCodigo;
	}

	public void setNtdCodigo(Short ntdCodigo) {
		this.ntdCodigo = ntdCodigo;
	}

	@Column(name = "VALOR_ORCADO", precision = 17, scale = 17)
	public Double getValorOrcado() {
		return this.valorOrcado;
	}

	public void setValorOrcado(Double valorOrcado) {
		this.valorOrcado = valorOrcado;
	}

	@Column(name = "VALOR_COMPROMETIDO", precision = 17, scale = 17)
	public Double getValorComprometido() {
		return this.valorComprometido;
	}

	public void setValorComprometido(Double valorComprometido) {
		this.valorComprometido = valorComprometido;
	}

	@Column(name = "VALOR_REALIZADO", precision = 17, scale = 17)
	public Double getValorRealizado() {
		return this.valorRealizado;
	}

	public void setValorRealizado(Double valorRealizado) {
		this.valorRealizado = valorRealizado;
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
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
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

	@Column(name = "PTB_CODIGO")
	public Long getPtbCodigo() {
		return this.ptbCodigo;
	}

	public void setPtbCodigo(Long ptbCodigo) {
		this.ptbCodigo = ptbCodigo;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		CVF_CODIGO("cvfCodigo"),
		EXO_EXERCICIO("exoExercicio"),
		SEQP("seqp"),
		GND_CODIGO("gndCodigo"),
		NTD_GND_CODIGO("ntdGndCodigo"),
		NTD_CODIGO("ntdCodigo"),
		VALOR_ORCADO("valorOrcado"),
		VALOR_COMPROMETIDO("valorComprometido"),
		VALOR_REALIZADO("valorRealizado"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA_ALTERADO("serMatriculaAlterado"),
		SER_VIN_CODIGO_ALTERADO("serVinCodigoAlterado"),
		PTB_CODIGO("ptbCodigo");

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
