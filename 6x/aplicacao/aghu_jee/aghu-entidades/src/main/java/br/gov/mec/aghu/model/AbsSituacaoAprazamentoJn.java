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
@Table(name = "ABS_SITUACOES_APRAZAMENTOS_JN", schema = "AGH")
@Immutable
public class AbsSituacaoAprazamentoJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 6733519593307413787L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private String codigo;
	private String descricao;
	private String indComponente;
	private String indProcedimento;
	private String indEnfermagem;
	private String indPendBancoSangue;
	private String indSituacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public AbsSituacaoAprazamentoJn() {
	}

	public AbsSituacaoAprazamentoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, String codigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.codigo = codigo;
	}

	public AbsSituacaoAprazamentoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, String codigo, String descricao,
			String indComponente, String indProcedimento, String indEnfermagem, String indPendBancoSangue, String indSituacao,
			Date criadoEm, Integer serMatricula, Short serVinCodigo) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.codigo = codigo;
		this.descricao = descricao;
		this.indComponente = indComponente;
		this.indProcedimento = indProcedimento;
		this.indEnfermagem = indEnfermagem;
		this.indPendBancoSangue = indPendBancoSangue;
		this.indSituacao = indSituacao;
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

	@Column(name = "CODIGO", nullable = false, length = 2)
	@Length(max = 2)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "DESCRICAO", length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_COMPONENTE", length = 1)
	@Length(max = 1)
	public String getIndComponente() {
		return this.indComponente;
	}

	public void setIndComponente(String indComponente) {
		this.indComponente = indComponente;
	}

	@Column(name = "IND_PROCEDIMENTO", length = 1)
	@Length(max = 1)
	public String getIndProcedimento() {
		return this.indProcedimento;
	}

	public void setIndProcedimento(String indProcedimento) {
		this.indProcedimento = indProcedimento;
	}

	@Column(name = "IND_ENFERMAGEM", length = 1)
	@Length(max = 1)
	public String getIndEnfermagem() {
		return this.indEnfermagem;
	}

	public void setIndEnfermagem(String indEnfermagem) {
		this.indEnfermagem = indEnfermagem;
	}

	@Column(name = "IND_PEND_BANCO_SANGUE", length = 1)
	@Length(max = 1)
	public String getIndPendBancoSangue() {
		return this.indPendBancoSangue;
	}

	public void setIndPendBancoSangue(String indPendBancoSangue) {
		this.indPendBancoSangue = indPendBancoSangue;
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
		CODIGO("codigo"),
		DESCRICAO("descricao"),
		IND_COMPONENTE("indComponente"),
		IND_PROCEDIMENTO("indProcedimento"),
		IND_ENFERMAGEM("indEnfermagem"),
		IND_PEND_BANCO_SANGUE("indPendBancoSangue"),
		IND_SITUACAO("indSituacao"),
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
