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
@Table(name = "AFA_ESCALA_PRODUCAO_FARMACI_JN", schema = "AGH")
@Immutable
public class AfaEscalaProducaoFarmaciJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -908796751768572012L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Short unfSeq;
	private String tipo;
	private Date data;
	private String turno;
	private Date criadoEm;
	private Integer serMatriculaResponsavel;
	private Short serVinCodigoResponsavel;
	private Integer serMatricula;
	private Short serVinCodigo;

	public AfaEscalaProducaoFarmaciJn() {
	}

	public AfaEscalaProducaoFarmaciJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short unfSeq, String tipo,
			Date data, String turno) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.unfSeq = unfSeq;
		this.tipo = tipo;
		this.data = data;
		this.turno = turno;
	}

	public AfaEscalaProducaoFarmaciJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short unfSeq, String tipo,
			Date data, String turno, Date criadoEm, Integer serMatriculaResponsavel, Short serVinCodigoResponsavel,
			Integer serMatricula, Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.unfSeq = unfSeq;
		this.tipo = tipo;
		this.data = data;
		this.turno = turno;
		this.criadoEm = criadoEm;
		this.serMatriculaResponsavel = serMatriculaResponsavel;
		this.serVinCodigoResponsavel = serVinCodigoResponsavel;
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

	@Column(name = "UNF_SEQ", nullable = false)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "TIPO", nullable = false, length = 1)
	@Length(max = 1)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA", nullable = false, length = 29)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Column(name = "TURNO", nullable = false, length = 1)
	@Length(max = 1)
	public String getTurno() {
		return this.turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA_RESPONSAVEL")
	public Integer getSerMatriculaResponsavel() {
		return this.serMatriculaResponsavel;
	}

	public void setSerMatriculaResponsavel(Integer serMatriculaResponsavel) {
		this.serMatriculaResponsavel = serMatriculaResponsavel;
	}

	@Column(name = "SER_VIN_CODIGO_RESPONSAVEL")
	public Short getSerVinCodigoResponsavel() {
		return this.serVinCodigoResponsavel;
	}

	public void setSerVinCodigoResponsavel(Short serVinCodigoResponsavel) {
		this.serVinCodigoResponsavel = serVinCodigoResponsavel;
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
		UNF_SEQ("unfSeq"),
		TIPO("tipo"),
		DATA("data"),
		TURNO("turno"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA_RESPONSAVEL("serMatriculaResponsavel"),
		SER_VIN_CODIGO_RESPONSAVEL("serVinCodigoResponsavel"),
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
