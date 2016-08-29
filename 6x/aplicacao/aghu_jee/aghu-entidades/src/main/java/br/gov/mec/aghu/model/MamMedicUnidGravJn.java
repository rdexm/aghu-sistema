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
@Table(name = "MAM_MEDIC_UNID_GRAVS_JN", schema = "AGH")
@Immutable
public class MamMedicUnidGravJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -467302166104748715L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer seq;
	private Double valor;
	private Short tipoComparacao;
	private String indSituacao;
	private Date criadoEm;
	private Short grvSeq;
	private Integer uxmMdmSeq;
	private Short uxmUanUnfSeq;
	private String micNome;
	private Integer serMatricula;
	private Short serVinCodigo;

	public MamMedicUnidGravJn() {
	}

	public MamMedicUnidGravJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	public MamMedicUnidGravJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq, Double valor,
			Short tipoComparacao, String indSituacao, Date criadoEm, Short grvSeq, Integer uxmMdmSeq, Short uxmUanUnfSeq,
			String micNome, Integer serMatricula, Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.valor = valor;
		this.tipoComparacao = tipoComparacao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.grvSeq = grvSeq;
		this.uxmMdmSeq = uxmMdmSeq;
		this.uxmUanUnfSeq = uxmUanUnfSeq;
		this.micNome = micNome;
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

	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "VALOR", precision = 17, scale = 17)
	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	@Column(name = "TIPO_COMPARACAO")
	public Short getTipoComparacao() {
		return this.tipoComparacao;
	}

	public void setTipoComparacao(Short tipoComparacao) {
		this.tipoComparacao = tipoComparacao;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "GRV_SEQ")
	public Short getGrvSeq() {
		return this.grvSeq;
	}

	public void setGrvSeq(Short grvSeq) {
		this.grvSeq = grvSeq;
	}

	@Column(name = "UXM_MDM_SEQ")
	public Integer getUxmMdmSeq() {
		return this.uxmMdmSeq;
	}

	public void setUxmMdmSeq(Integer uxmMdmSeq) {
		this.uxmMdmSeq = uxmMdmSeq;
	}

	@Column(name = "UXM_UAN_UNF_SEQ")
	public Short getUxmUanUnfSeq() {
		return this.uxmUanUnfSeq;
	}

	public void setUxmUanUnfSeq(Short uxmUanUnfSeq) {
		this.uxmUanUnfSeq = uxmUanUnfSeq;
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

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		VALOR("valor"),
		TIPO_COMPARACAO("tipoComparacao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		GRV_SEQ("grvSeq"),
		UXM_MDM_SEQ("uxmMdmSeq"),
		UXM_UAN_UNF_SEQ("uxmUanUnfSeq"),
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
