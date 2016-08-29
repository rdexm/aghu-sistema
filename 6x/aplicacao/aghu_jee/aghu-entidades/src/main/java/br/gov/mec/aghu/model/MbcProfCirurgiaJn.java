package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mbcPcgJnSeq", sequenceName="AGH.MBC_PCG_JN_SEQ", allocationSize = 1)
@Table(name = "MBC_PROF_CIRURGIAS_JN", schema = "AGH")
@Immutable
public class MbcProfCirurgiaJn extends BaseJournal {

	private static final long serialVersionUID = 2324853623283123459L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer crgSeq;
	private Integer pucSerMatricula;
	private Short pucSerVinCodigo;
	private Short pucUnfSeq;
	private DominioFuncaoProfissional pucIndFuncaoProf;
	private Boolean indResponsavel;
	private Boolean indRealizou;
	private Boolean indInclEscala;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer pucSerMatriculaVinc;
	private Short pucSerVinCodigoVinc;
	private Short pucUnfSeqVinc;
	private DominioFuncaoProfissional pucIndFuncaoProfVinc;

	public MbcProfCirurgiaJn() {
	}

	public MbcProfCirurgiaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer crgSeq, Integer pucSerMatricula,
			Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.crgSeq = crgSeq;
		this.pucSerMatricula = pucSerMatricula;
		this.pucSerVinCodigo = pucSerVinCodigo;
		this.pucUnfSeq = pucUnfSeq;
		this.pucIndFuncaoProf = pucIndFuncaoProf;
	}

	public MbcProfCirurgiaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer crgSeq, Integer pucSerMatricula,
			Short pucSerVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf, Boolean indResponsavel, Boolean indRealizou,
			Boolean indInclEscala, Date criadoEm, Integer serMatricula, Short serVinCodigo, Integer pucSerMatriculaVinc,
			Short pucSerVinCodigoVinc, Short pucUnfSeqVinc, DominioFuncaoProfissional pucIndFuncaoProfVinc) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.crgSeq = crgSeq;
		this.pucSerMatricula = pucSerMatricula;
		this.pucSerVinCodigo = pucSerVinCodigo;
		this.pucUnfSeq = pucUnfSeq;
		this.pucIndFuncaoProf = pucIndFuncaoProf;
		this.indResponsavel = indResponsavel;
		this.indRealizou = indRealizou;
		this.indInclEscala = indInclEscala;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.pucSerMatriculaVinc = pucSerMatriculaVinc;
		this.pucSerVinCodigoVinc = pucSerVinCodigoVinc;
		this.pucUnfSeqVinc = pucUnfSeqVinc;
		this.pucIndFuncaoProfVinc = pucIndFuncaoProfVinc;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcPcgJnSeq")
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

	@Column(name = "CRG_SEQ", nullable = false)
	public Integer getCrgSeq() {
		return this.crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	@Column(name = "PUC_SER_MATRICULA", nullable = false)
	public Integer getPucSerMatricula() {
		return this.pucSerMatricula;
	}

	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}

	@Column(name = "PUC_SER_VIN_CODIGO", nullable = false)
	public Short getPucSerVinCodigo() {
		return this.pucSerVinCodigo;
	}

	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}

	@Column(name = "PUC_UNF_SEQ", nullable = false)
	public Short getPucUnfSeq() {
		return this.pucUnfSeq;
	}

	public void setPucUnfSeq(Short pucUnfSeq) {
		this.pucUnfSeq = pucUnfSeq;
	}

	@Column(name = "PUC_IND_FUNCAO_PROF", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioFuncaoProfissional getPucIndFuncaoProf() {
		return this.pucIndFuncaoProf;
	}

	public void setPucIndFuncaoProf(DominioFuncaoProfissional pucIndFuncaoProf) {
		this.pucIndFuncaoProf = pucIndFuncaoProf;
	}

	@Column(name = "IND_RESPONSAVEL", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndResponsavel() {
		return this.indResponsavel;
	}

	public void setIndResponsavel(Boolean indResponsavel) {
		this.indResponsavel = indResponsavel;
	}

	@Column(name = "IND_REALIZOU", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRealizou() {
		return this.indRealizou;
	}

	public void setIndRealizou(Boolean indRealizou) {
		this.indRealizou = indRealizou;
	}

	@Column(name = "IND_INCL_ESCALA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInclEscala() {
		return this.indInclEscala;
	}

	public void setIndInclEscala(Boolean indInclEscala) {
		this.indInclEscala = indInclEscala;
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

	@Column(name = "PUC_SER_MATRICULA_VINC")
	public Integer getPucSerMatriculaVinc() {
		return this.pucSerMatriculaVinc;
	}

	public void setPucSerMatriculaVinc(Integer pucSerMatriculaVinc) {
		this.pucSerMatriculaVinc = pucSerMatriculaVinc;
	}

	@Column(name = "PUC_SER_VIN_CODIGO_VINC")
	public Short getPucSerVinCodigoVinc() {
		return this.pucSerVinCodigoVinc;
	}

	public void setPucSerVinCodigoVinc(Short pucSerVinCodigoVinc) {
		this.pucSerVinCodigoVinc = pucSerVinCodigoVinc;
	}

	@Column(name = "PUC_UNF_SEQ_VINC")
	public Short getPucUnfSeqVinc() {
		return this.pucUnfSeqVinc;
	}

	public void setPucUnfSeqVinc(Short pucUnfSeqVinc) {
		this.pucUnfSeqVinc = pucUnfSeqVinc;
	}

	@Column(name = "PUC_IND_FUNCAO_PROF_VINC", length = 3)
	@Enumerated(EnumType.STRING)
	public DominioFuncaoProfissional getPucIndFuncaoProfVinc() {
		return this.pucIndFuncaoProfVinc;
	}

	public void setPucIndFuncaoProfVinc(DominioFuncaoProfissional pucIndFuncaoProfVinc) {
		this.pucIndFuncaoProfVinc = pucIndFuncaoProfVinc;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		CRG_SEQ("crgSeq"),
		PUC_SER_MATRICULA("pucSerMatricula"),
		PUC_SER_VIN_CODIGO("pucSerVinCodigo"),
		PUC_UNF_SEQ("pucUnfSeq"),
		PUC_IND_FUNCAO_PROF("pucIndFuncaoProf"),
		IND_RESPONSAVEL("indResponsavel"),
		IND_REALIZOU("indRealizou"),
		IND_INCL_ESCALA("indInclEscala"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		PUC_SER_MATRICULA_VINC("pucSerMatriculaVinc"),
		PUC_SER_VIN_CODIGO_VINC("pucSerVinCodigoVinc"),
		PUC_UNF_SEQ_VINC("pucUnfSeqVinc"),
		PUC_IND_FUNCAO_PROF_VINC("pucIndFuncaoProfVinc");

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
