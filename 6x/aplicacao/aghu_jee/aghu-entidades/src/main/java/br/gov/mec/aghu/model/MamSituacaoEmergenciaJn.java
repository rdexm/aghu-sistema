package br.gov.mec.aghu.model;

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
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mamSegJnSeq", sequenceName="AGH.MAM_SEG_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_SITUACAO_EMERGENCIAS_JN", schema = "AGH")
@Immutable
public class MamSituacaoEmergenciaJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 6636941379841045483L;
	private Short seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Boolean indUsoTriagem;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public MamSituacaoEmergenciaJn() {
	}

	public MamSituacaoEmergenciaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq) {
		this.seq = seq;
	}

	public MamSituacaoEmergenciaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq, String descricao,
			DominioSituacao indSituacao, Boolean indUsoTriagem, Date criadoEm, Integer serMatricula, Short serVinCodigo) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.indUsoTriagem = indUsoTriagem;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamSegJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	

	@Column(name = "SEQ", nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", length = 240)
	@Length(max = 240)
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

	@Column(name = "IND_USO_TRIAGEM", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoTriagem() {
		return this.indUsoTriagem;
	}

	public void setIndUsoTriagem(Boolean indUsoTriagem) {
		this.indUsoTriagem = indUsoTriagem;
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
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		IND_USO_TRIAGEM("indUsoTriagem"),
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
