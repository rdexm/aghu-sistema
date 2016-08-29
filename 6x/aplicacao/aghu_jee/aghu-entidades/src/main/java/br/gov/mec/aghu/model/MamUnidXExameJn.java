package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MAM_UNID_X_EXAMES_JN", schema = "AGH")
@Immutable
public class MamUnidXExameJn extends BaseJournal {

	private static final long serialVersionUID = 8279662444710761686L;

	private Integer emsSeq;
	private Short uanUnfSeq;
	private String indObrigatorio;
	private Date criadoEm;
	private String indSituacao;
	private String micNome;
	private Integer serMatricula;
	private Short serVinCodigo;

	public MamUnidXExameJn() {
	}

	public MamUnidXExameJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer emsSeq, Short uanUnfSeq) {
		this.emsSeq = emsSeq;
		this.uanUnfSeq = uanUnfSeq;
	}

	public MamUnidXExameJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer emsSeq, Short uanUnfSeq,
			String indObrigatorio, Date criadoEm, String indSituacao, String micNome, Integer serMatricula, Short serVinCodigo) {
		this.emsSeq = emsSeq;
		this.uanUnfSeq = uanUnfSeq;
		this.indObrigatorio = indObrigatorio;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.micNome = micNome;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	//@GeneratedValue(strategy = GenerationType.AUTO, generator = "")
	@NotNull
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
	@NotNull
	@Length(max = 30)
	public String getJnUser() {
		return this.jnUser;
	}

	public void setJnUser(String jnUser) {
		this.jnUser = jnUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "JN_DATE_TIME", nullable = false, length = 29)
	@NotNull
	public Date getJnDateTime() {
		return this.jnDateTime;
	}

	public void setJnDateTime(Date jnDateTime) {
		this.jnDateTime = jnDateTime;
	}

	@Column(name = "JN_OPERATION", nullable = false, length = 3)
	@NotNull
	@Length(max = 3)
	public String getJnOperation() {
		return this.jnOperation;
	}

	public void setJnOperation(String jnOperation) {
		this.jnOperation = jnOperation;
	}*/

	@Column(name = "EMS_SEQ", nullable = false)
	public Integer getEmsSeq() {
		return this.emsSeq;
	}

	public void setEmsSeq(Integer emsSeq) {
		this.emsSeq = emsSeq;
	}

	@Column(name = "UAN_UNF_SEQ", nullable = false)
	public Short getUanUnfSeq() {
		return this.uanUnfSeq;
	}

	public void setUanUnfSeq(Short uanUnfSeq) {
		this.uanUnfSeq = uanUnfSeq;
	}

	@Column(name = "IND_OBRIGATORIO", length = 1)
	@Length(max = 1)
	public String getIndObrigatorio() {
		return this.indObrigatorio;
	}

	public void setIndObrigatorio(String indObrigatorio) {
		this.indObrigatorio = indObrigatorio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "MIC_NOME", length = 50)
	@Length(max = 50)
	public String getMicNome() {
		return this.micNome;
	}

	public void setMicNome(String micNome) {
		this.micNome = micNome;
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

		EMS_SEQ("emsSeq"),
		UAN_UNF_SEQ("uanUnfSeq"),
		IND_OBRIGATORIO("indObrigatorio"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		MIC_NOME("micNome"),
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
