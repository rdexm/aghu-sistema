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


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MCI_TOPOGRAFIA_PROCEDIMENTO_JN", schema = "AGH")
@SequenceGenerator(name="mci_top_jn_seq", sequenceName="AGH.MCI_TOP_JN_SEQ", allocationSize = 1)
@Immutable
public class MciTopografiaProcedimentoJn extends BaseJournal {

	private static final long serialVersionUID = 6039734507245845851L;

	private Short seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Boolean indPermSobreposicao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short toiSeq;
	private Date alteradoEm;
	private Integer serMatriculaMovimentado;
	private Short serVinCodigoMovimentado;

	public MciTopografiaProcedimentoJn() {
		
	}

	public MciTopografiaProcedimentoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq) {
		this.seq = seq;
	}

	public MciTopografiaProcedimentoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, 
			Short seq, String descricao, DominioSituacao indSituacao, Boolean indPermSobreposicao, Date criadoEm, 
			Integer serMatricula, Short serVinCodigo, Short toiSeq, Date alteradoEm, Integer serMatriculaMovimentado,
			Short serVinCodigoMovimentado) {
		
		this.seq = seq;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.indPermSobreposicao = indPermSobreposicao;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.toiSeq = toiSeq;
		this.alteradoEm = alteradoEm;
		this.serMatriculaMovimentado = serMatriculaMovimentado;
		this.serVinCodigoMovimentado = serVinCodigoMovimentado;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mci_top_jn_seq")
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

	@Column(name = "IND_PERM_SOBREPOSICAO", length = 1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPermSobreposicao() {
		return this.indPermSobreposicao;
	}

	public void setIndPermSobreposicao(Boolean indPermSobreposicao) {
		this.indPermSobreposicao = indPermSobreposicao;
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

	@Column(name = "TOI_SEQ")
	public Short getToiSeq() {
		return this.toiSeq;
	}

	public void setToiSeq(Short toiSeq) {
		this.toiSeq = toiSeq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "SER_MATRICULA_MOVIMENTADO")
	public Integer getSerMatriculaMovimentado() {
		return serMatriculaMovimentado;
	}

	public void setSerMatriculaMovimentado(Integer serMatriculaMovimentado) {
		this.serMatriculaMovimentado = serMatriculaMovimentado;
	}

	@Column(name = "SER_VIN_CODIGO_MOVIMENTADO")
	public Short getSerVinCodigoMovimentado() {
		return serVinCodigoMovimentado;
	}

	public void setSerVinCodigoMovimentado(Short serVinCodigoMovimentado) {
		this.serVinCodigoMovimentado = serVinCodigoMovimentado;
	}

	public enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		IND_PERM_SOBREPOSICAO("indPermSobreposicao"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		TOI_SEQ("toiSeq"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA_MOVIMENTADO("serMatriculaMovimentado"),
		SER_VIN_CODIGO_MOVIMENTADO("serVinCodigoMovimentado");

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
