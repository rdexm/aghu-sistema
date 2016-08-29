package br.gov.mec.aghu.model;

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

import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * PdtInstrDescJn generated by hbm2java
 */
@Entity
@Table(name = "PDT_INSTR_DESCS_JN", schema = "AGH")
@SequenceGenerator(name = "pdtIsdJnSeq", sequenceName = "AGH.PDT_ISD_JN_SEQ", allocationSize = 1)
@Immutable
public class PdtInstrDescJn extends BaseJournal {

	private static final long serialVersionUID = -2554588179865213831L;
	
	private Integer ddtSeq;
	private Integer pinSeq;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;

	public PdtInstrDescJn() {
	}

	public PdtInstrDescJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer ddtSeq, Integer pinSeq) {
		this.ddtSeq = ddtSeq;
		this.pinSeq = pinSeq;
	}

	public PdtInstrDescJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer ddtSeq, Integer pinSeq, Date criadoEm,
			Integer serMatricula, Short serVinCodigo) {
		this.ddtSeq = ddtSeq;
		this.pinSeq = pinSeq;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "pdtIsdJnSeq")
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

	@Column(name = "PIN_SEQ", nullable = false)
	public Integer getPinSeq() {
		return this.pinSeq;
	}

	public void setPinSeq(Integer pinSeq) {
		this.pinSeq = pinSeq;
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
		PIN_SEQ("pinSeq"),
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