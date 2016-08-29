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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoIndicacaoNascimento;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mcoInaJnSeq", sequenceName="AGH.MCO_INA_JN_SEQ", allocationSize = 1)
@Table(name = "MCO_INDICACAO_NASCIMENTOS_JN", schema = "AGH")
@Immutable
public class McoIndicacaoNascimentoJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -7637020093413171575L;

	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private DominioTipoIndicacaoNascimento tipoIndicacao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public McoIndicacaoNascimentoJn() {
	}

	public McoIndicacaoNascimentoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq) {
		this.seq = seq;
	}

	public McoIndicacaoNascimentoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq, String descricao,
			DominioSituacao indSituacao, DominioTipoIndicacaoNascimento tipoIndicacao, Date criadoEm, Integer serMatricula, Short serVinCodigo) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.tipoIndicacao = tipoIndicacao;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mcoInaJnSeq")
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

	@Column(name = "DESCRICAO", length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "TIPO_INDICACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoIndicacaoNascimento getTipoIndicacao() {
		return this.tipoIndicacao;
	}

	public void setTipoIndicacao(DominioTipoIndicacaoNascimento tipoIndicacao) {
		this.tipoIndicacao = tipoIndicacao;
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

		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		TIPO_INDICACAO("tipoIndicacao"),
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
