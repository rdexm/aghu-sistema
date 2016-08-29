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
@Table(name = "MAM_EMG_GRP_PLANO_X_PERFIS_JN", schema = "AGH")
@Immutable
public class MamEmgGrpPlanoXPerfilJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -8633103957646401668L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Short egpSeq;
	private Short seqp;
	private Date criadoEm;
	private String indSituacao;
	private Short eepEspSeq;
	private String perNome;
	private Integer serMatricula;
	private Short serVinCodigo;

	public MamEmgGrpPlanoXPerfilJn() {
	}

	public MamEmgGrpPlanoXPerfilJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short egpSeq, Short seqp) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.egpSeq = egpSeq;
		this.seqp = seqp;
	}

	public MamEmgGrpPlanoXPerfilJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short egpSeq, Short seqp,
			Date criadoEm, String indSituacao, Short eepEspSeq, String perNome, Integer serMatricula, Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.egpSeq = egpSeq;
		this.seqp = seqp;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.eepEspSeq = eepEspSeq;
		this.perNome = perNome;
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

	@Column(name = "EGP_SEQ", nullable = false)
	public Short getEgpSeq() {
		return this.egpSeq;
	}

	public void setEgpSeq(Short egpSeq) {
		this.egpSeq = egpSeq;
	}

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

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "EEP_ESP_SEQ")
	public Short getEepEspSeq() {
		return this.eepEspSeq;
	}

	public void setEepEspSeq(Short eepEspSeq) {
		this.eepEspSeq = eepEspSeq;
	}

	@Column(name = "PER_NOME", length = 30)
	@Length(max = 30)
	public String getPerNome() {
		return this.perNome;
	}

	public void setPerNome(String perNome) {
		this.perNome = perNome;
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
		EGP_SEQ("egpSeq"),
		SEQP("seqp"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		EEP_ESP_SEQ("eepEspSeq"),
		PER_NOME("perNome"),
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
