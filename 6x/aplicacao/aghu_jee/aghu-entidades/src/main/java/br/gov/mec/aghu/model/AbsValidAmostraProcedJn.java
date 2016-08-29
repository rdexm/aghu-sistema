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
@Table(name = "ABS_VALID_AMOSTRAS_PROCEDS_JN", schema = "AGH")
@Immutable
public class AbsValidAmostraProcedJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -2051214014504557606L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private String pheCodigo;
	private Short seqp;
	private Short idadeMesInicial;
	private Short idadeMesFinal;
	private Short nroMaximoSolicitacoes;
	private Integer validade;
	private String unidValidAmostra;
	private String tipoValidade;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date criadoEm;

	public AbsValidAmostraProcedJn() {
	}

	public AbsValidAmostraProcedJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, String pheCodigo, Short seqp) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.pheCodigo = pheCodigo;
		this.seqp = seqp;
	}

	public AbsValidAmostraProcedJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, String pheCodigo, Short seqp,
			Short idadeMesInicial, Short idadeMesFinal, Short nroMaximoSolicitacoes, Integer validade, String unidValidAmostra,
			String tipoValidade, Integer serMatricula, Short serVinCodigo, Date criadoEm) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.pheCodigo = pheCodigo;
		this.seqp = seqp;
		this.idadeMesInicial = idadeMesInicial;
		this.idadeMesFinal = idadeMesFinal;
		this.nroMaximoSolicitacoes = nroMaximoSolicitacoes;
		this.validade = validade;
		this.unidValidAmostra = unidValidAmostra;
		this.tipoValidade = tipoValidade;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.criadoEm = criadoEm;
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

	@Column(name = "PHE_CODIGO", nullable = false, length = 2)
	@Length(max = 2)
	public String getPheCodigo() {
		return this.pheCodigo;
	}

	public void setPheCodigo(String pheCodigo) {
		this.pheCodigo = pheCodigo;
	}

	@Column(name = "SEQP", nullable = false)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Column(name = "IDADE_MES_INICIAL")
	public Short getIdadeMesInicial() {
		return this.idadeMesInicial;
	}

	public void setIdadeMesInicial(Short idadeMesInicial) {
		this.idadeMesInicial = idadeMesInicial;
	}

	@Column(name = "IDADE_MES_FINAL")
	public Short getIdadeMesFinal() {
		return this.idadeMesFinal;
	}

	public void setIdadeMesFinal(Short idadeMesFinal) {
		this.idadeMesFinal = idadeMesFinal;
	}

	@Column(name = "NRO_MAXIMO_SOLICITACOES")
	public Short getNroMaximoSolicitacoes() {
		return this.nroMaximoSolicitacoes;
	}

	public void setNroMaximoSolicitacoes(Short nroMaximoSolicitacoes) {
		this.nroMaximoSolicitacoes = nroMaximoSolicitacoes;
	}

	@Column(name = "VALIDADE")
	public Integer getValidade() {
		return this.validade;
	}

	public void setValidade(Integer validade) {
		this.validade = validade;
	}

	@Column(name = "UNID_VALID_AMOSTRA", length = 1)
	@Length(max = 1)
	public String getUnidValidAmostra() {
		return this.unidValidAmostra;
	}

	public void setUnidValidAmostra(String unidValidAmostra) {
		this.unidValidAmostra = unidValidAmostra;
	}

	@Column(name = "TIPO_VALIDADE", length = 1)
	@Length(max = 1)
	public String getTipoValidade() {
		return this.tipoValidade;
	}

	public void setTipoValidade(String tipoValidade) {
		this.tipoValidade = tipoValidade;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		PHE_CODIGO("pheCodigo"),
		SEQP("seqp"),
		IDADE_MES_INICIAL("idadeMesInicial"),
		IDADE_MES_FINAL("idadeMesFinal"),
		NRO_MAXIMO_SOLICITACOES("nroMaximoSolicitacoes"),
		VALIDADE("validade"),
		UNID_VALID_AMOSTRA("unidValidAmostra"),
		TIPO_VALIDADE("tipoValidade"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		CRIADO_EM("criadoEm");

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
