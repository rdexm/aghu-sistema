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
@Table(name = "MBC_ESPECIALIDADE_PROC_CIRG_JN", schema = "AGH")
@Immutable
public class MbcEspecialidadeProcCirgJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 5921415234496400136L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer pciSeq;
	private Short espSeq;
	private String situacao;
	private Date criadoEm;
	private Short serVinCodigo;
	private Integer serMatricula;
	private String achadosOperPadrao;

	public MbcEspecialidadeProcCirgJn() {
	}

	public MbcEspecialidadeProcCirgJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer pciSeq, Short espSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.pciSeq = pciSeq;
		this.espSeq = espSeq;
	}

	public MbcEspecialidadeProcCirgJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer pciSeq, Short espSeq,
			String situacao, Date criadoEm, Short serVinCodigo, Integer serMatricula, String achadosOperPadrao) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.pciSeq = pciSeq;
		this.espSeq = espSeq;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.serVinCodigo = serVinCodigo;
		this.serMatricula = serMatricula;
		this.achadosOperPadrao = achadosOperPadrao;
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

	@Column(name = "PCI_SEQ", nullable = false)
	public Integer getPciSeq() {
		return this.pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	@Column(name = "ESP_SEQ", nullable = false)
	public Short getEspSeq() {
		return this.espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
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

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "ACHADOS_OPER_PADRAO", length = 4000)
	@Length(max = 4000)
	public String getAchadosOperPadrao() {
		return this.achadosOperPadrao;
	}

	public void setAchadosOperPadrao(String achadosOperPadrao) {
		this.achadosOperPadrao = achadosOperPadrao;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		PCI_SEQ("pciSeq"),
		ESP_SEQ("espSeq"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA("serMatricula"),
		ACHADOS_OPER_PADRAO("achadosOperPadrao");

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
