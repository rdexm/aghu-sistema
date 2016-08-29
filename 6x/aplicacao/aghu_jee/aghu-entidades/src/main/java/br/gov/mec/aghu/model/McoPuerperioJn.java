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
@Table(name = "MCO_PUERPERIOS_JN", schema = "AGH")
@Immutable
public class McoPuerperioJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 7920971662575274941L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Date dthrEntSr;
	private Date dthrSaidaSr;
	private String aleitamentoContraInd;
	private String observacoes;
	private String imunoglobulinaAntiRh;
	private Short gsoSeqp;
	private Integer gsoPacCodigo;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public McoPuerperioJn() {
	}

	public McoPuerperioJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short gsoSeqp, Integer gsoPacCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.gsoSeqp = gsoSeqp;
		this.gsoPacCodigo = gsoPacCodigo;
	}

	public McoPuerperioJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Date dthrEntSr, Date dthrSaidaSr,
			String aleitamentoContraInd, String observacoes, String imunoglobulinaAntiRh, Short gsoSeqp, Integer gsoPacCodigo,
			Date criadoEm, Integer serMatricula, Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.dthrEntSr = dthrEntSr;
		this.dthrSaidaSr = dthrSaidaSr;
		this.aleitamentoContraInd = aleitamentoContraInd;
		this.observacoes = observacoes;
		this.imunoglobulinaAntiRh = imunoglobulinaAntiRh;
		this.gsoSeqp = gsoSeqp;
		this.gsoPacCodigo = gsoPacCodigo;
		this.criadoEm = criadoEm;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ENT_SR", length = 29)
	public Date getDthrEntSr() {
		return this.dthrEntSr;
	}

	public void setDthrEntSr(Date dthrEntSr) {
		this.dthrEntSr = dthrEntSr;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_SAIDA_SR", length = 29)
	public Date getDthrSaidaSr() {
		return this.dthrSaidaSr;
	}

	public void setDthrSaidaSr(Date dthrSaidaSr) {
		this.dthrSaidaSr = dthrSaidaSr;
	}

	@Column(name = "ALEITAMENTO_CONTRA_IND", length = 100)
	@Length(max = 100)
	public String getAleitamentoContraInd() {
		return this.aleitamentoContraInd;
	}

	public void setAleitamentoContraInd(String aleitamentoContraInd) {
		this.aleitamentoContraInd = aleitamentoContraInd;
	}

	@Column(name = "OBSERVACOES", length = 500)
	@Length(max = 500)
	public String getObservacoes() {
		return this.observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	@Column(name = "IMUNOGLOBULINA_ANTI_RH", length = 1)
	@Length(max = 1)
	public String getImunoglobulinaAntiRh() {
		return this.imunoglobulinaAntiRh;
	}

	public void setImunoglobulinaAntiRh(String imunoglobulinaAntiRh) {
		this.imunoglobulinaAntiRh = imunoglobulinaAntiRh;
	}

	@Column(name = "GSO_SEQP", nullable = false)
	public Short getGsoSeqp() {
		return this.gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	@Column(name = "GSO_PAC_CODIGO", nullable = false)
	public Integer getGsoPacCodigo() {
		return this.gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
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

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		DTHR_ENT_SR("dthrEntSr"),
		DTHR_SAIDA_SR("dthrSaidaSr"),
		ALEITAMENTO_CONTRA_IND("aleitamentoContraInd"),
		OBSERVACOES("observacoes"),
		IMUNOGLOBULINA_ANTI_RH("imunoglobulinaAntiRh"),
		GSO_SEQP("gsoSeqp"),
		GSO_PAC_CODIGO("gsoPacCodigo"),
		CRIADO_EM("criadoEm"),
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
