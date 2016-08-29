package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * PdtDescTecnicaJn generated by hbm2java
 */
@Entity
@Table(name = "PDT_DESC_TECNICAS_JN", schema = "AGH")
@SequenceGenerator(name = "pdtDspJnSeq", sequenceName = "AGH.PDT_DSP_JN_SEQ", allocationSize = 1)
@Immutable
public class PdtDescTecnicaJn extends BaseJournal {

	private static final long serialVersionUID = 3499761685410409772L;
	private Integer ddtSeq;
	private String descricao;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public PdtDescTecnicaJn() {
	}

	public PdtDescTecnicaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	public PdtDescTecnicaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer ddtSeq, String descricao,
			Date criadoEm, Integer serMatricula, Short serVinCodigo) {
		this.ddtSeq = ddtSeq;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "pdtDspJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "DDT_SEQ", nullable = false)
	public Integer getDdtSeq() {
		return this.ddtSeq;
	}

	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	@Column(name = "DESCRICAO", length = 4000)
	@Length(max = 4000)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

		DDT_SEQ("ddtSeq"),
		DESCRICAO("descricao"),
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