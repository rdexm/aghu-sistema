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
@Table(name = "MBC_PROF_ATUA_UNID_CIRGS_JN", schema = "AGH")
@Immutable
public class MbcProfAtuaUnidCirgJn extends BaseJournal {

	private static final long serialVersionUID = -3553756530036798828L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short unfSeq;
	private Integer serMatriculaECadastrado;
	private Short serVinCodigoECadastrado;
	private Integer serMatriculaAlterado;
	private Short serVinCodigoAlterado;
	private String indFuncaoProf;
	private String situacao;
	private Date criadoEm;
	private Integer pucSerMatricula;
	private Short pucSerVinCodigo;
	private Short pucUnfSeq;
	private String pucIndFuncaoProf;

	public MbcProfAtuaUnidCirgJn() {
	}

	public MbcProfAtuaUnidCirgJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer serMatricula,
			Short serVinCodigo, Short unfSeq, String indFuncaoProf) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.unfSeq = unfSeq;
		this.indFuncaoProf = indFuncaoProf;
	}

	public MbcProfAtuaUnidCirgJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer serMatricula,
			Short serVinCodigo, Short unfSeq, Integer serMatriculaECadastrado, Short serVinCodigoECadastrado,
			Integer serMatriculaAlterado, Short serVinCodigoAlterado, String indFuncaoProf, String situacao, Date criadoEm,
			Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq, String pucIndFuncaoProf) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.unfSeq = unfSeq;
		this.serMatriculaECadastrado = serMatriculaECadastrado;
		this.serVinCodigoECadastrado = serVinCodigoECadastrado;
		this.serMatriculaAlterado = serMatriculaAlterado;
		this.serVinCodigoAlterado = serVinCodigoAlterado;
		this.indFuncaoProf = indFuncaoProf;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.pucSerMatricula = pucSerMatricula;
		this.pucSerVinCodigo = pucSerVinCodigo;
		this.pucUnfSeq = pucUnfSeq;
		this.pucIndFuncaoProf = pucIndFuncaoProf;
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

	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "UNF_SEQ", nullable = false)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "SER_MATRICULA_E_CADASTRADO")
	public Integer getSerMatriculaECadastrado() {
		return this.serMatriculaECadastrado;
	}

	public void setSerMatriculaECadastrado(Integer serMatriculaECadastrado) {
		this.serMatriculaECadastrado = serMatriculaECadastrado;
	}

	@Column(name = "SER_VIN_CODIGO_E_CADASTRADO")
	public Short getSerVinCodigoECadastrado() {
		return this.serVinCodigoECadastrado;
	}

	public void setSerVinCodigoECadastrado(Short serVinCodigoECadastrado) {
		this.serVinCodigoECadastrado = serVinCodigoECadastrado;
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

	@Column(name = "IND_FUNCAO_PROF", nullable = false, length = 3)
	@Length(max = 3)
	public String getIndFuncaoProf() {
		return this.indFuncaoProf;
	}

	public void setIndFuncaoProf(String indFuncaoProf) {
		this.indFuncaoProf = indFuncaoProf;
	}

	@Column(name = "SITUACAO", length = 1)
	@Length(max = 1)
	public String getSituacao() {
		return this.situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "PUC_SER_MATRICULA")
	public Integer getPucSerMatricula() {
		return this.pucSerMatricula;
	}

	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}

	@Column(name = "PUC_SER_VIN_CODIGO")
	public Short getPucSerVinCodigo() {
		return this.pucSerVinCodigo;
	}

	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}

	@Column(name = "PUC_UNF_SEQ")
	public Short getPucUnfSeq() {
		return this.pucUnfSeq;
	}

	public void setPucUnfSeq(Short pucUnfSeq) {
		this.pucUnfSeq = pucUnfSeq;
	}

	@Column(name = "PUC_IND_FUNCAO_PROF", length = 3)
	@Length(max = 3)
	public String getPucIndFuncaoProf() {
		return this.pucIndFuncaoProf;
	}

	public void setPucIndFuncaoProf(String pucIndFuncaoProf) {
		this.pucIndFuncaoProf = pucIndFuncaoProf;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		UNF_SEQ("unfSeq"),
		SER_MATRICULA_E_CADASTRADO("serMatriculaECadastrado"),
		SER_VIN_CODIGO_E_CADASTRADO("serVinCodigoECadastrado"),
		SER_MATRICULA_ALTERADO("serMatriculaAlterado"),
		SER_VIN_CODIGO_ALTERADO("serVinCodigoAlterado"),
		IND_FUNCAO_PROF("indFuncaoProf"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		PUC_SER_MATRICULA("pucSerMatricula"),
		PUC_SER_VIN_CODIGO("pucSerVinCodigo"),
		PUC_UNF_SEQ("pucUnfSeq"),
		PUC_IND_FUNCAO_PROF("pucIndFuncaoProf");

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
