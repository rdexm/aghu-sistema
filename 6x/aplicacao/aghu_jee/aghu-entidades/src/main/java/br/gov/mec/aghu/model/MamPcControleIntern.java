package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * MamPcControleIntern generated by hbm2java
 */
@Entity
@SequenceGenerator(name="mamPcySq1", sequenceName="AGH.MAM_PCY_SQ1", allocationSize = 1)
@Table(name = "MAM_PC_CONTROLE_INTERNS", schema = "AGH")
public class MamPcControleIntern extends BaseEntitySeq<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5619061319981547020L;
	private Long seq;
	private Integer version;
	private String tipo;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer asuApaAtdSeq;
	private Integer asuApaSeq;
	private Short asuSeqp;
	private Long rgtSeq;
	private Long anaSeq;
	private Long evoSeq;
	private Integer atdSeq;
	private Set<MamPcControleIntXUnf> mamPcControleIntXUnfes = new HashSet<MamPcControleIntXUnf>(0);

	public MamPcControleIntern() {
	}

	public MamPcControleIntern(Long seq, String tipo, Date criadoEm) {
		this.seq = seq;
		this.tipo = tipo;
		this.criadoEm = criadoEm;
	}

	public MamPcControleIntern(Long seq, String tipo, Date criadoEm, Integer serMatricula, Short serVinCodigo, Integer asuApaAtdSeq,
			Integer asuApaSeq, Short asuSeqp, Long rgtSeq, Long anaSeq, Long evoSeq, Integer atdSeq,
			Set<MamPcControleIntXUnf> mamPcControleIntXUnfes) {
		this.seq = seq;
		this.tipo = tipo;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.asuApaAtdSeq = asuApaAtdSeq;
		this.asuApaSeq = asuApaSeq;
		this.asuSeqp = asuSeqp;
		this.rgtSeq = rgtSeq;
		this.anaSeq = anaSeq;
		this.evoSeq = evoSeq;
		this.atdSeq = atdSeq;
		this.mamPcControleIntXUnfes = mamPcControleIntXUnfes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamPcySq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "TIPO", nullable = false, length = 5)
	@Length(max = 5)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
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

	@Column(name = "ASU_APA_ATD_SEQ")
	public Integer getAsuApaAtdSeq() {
		return this.asuApaAtdSeq;
	}

	public void setAsuApaAtdSeq(Integer asuApaAtdSeq) {
		this.asuApaAtdSeq = asuApaAtdSeq;
	}

	@Column(name = "ASU_APA_SEQ")
	public Integer getAsuApaSeq() {
		return this.asuApaSeq;
	}

	public void setAsuApaSeq(Integer asuApaSeq) {
		this.asuApaSeq = asuApaSeq;
	}

	@Column(name = "ASU_SEQP")
	public Short getAsuSeqp() {
		return this.asuSeqp;
	}

	public void setAsuSeqp(Short asuSeqp) {
		this.asuSeqp = asuSeqp;
	}

	@Column(name = "RGT_SEQ")
	public Long getRgtSeq() {
		return this.rgtSeq;
	}

	public void setRgtSeq(Long rgtSeq) {
		this.rgtSeq = rgtSeq;
	}

	@Column(name = "ANA_SEQ")
	public Long getAnaSeq() {
		return this.anaSeq;
	}

	public void setAnaSeq(Long anaSeq) {
		this.anaSeq = anaSeq;
	}

	@Column(name = "EVO_SEQ")
	public Long getEvoSeq() {
		return this.evoSeq;
	}

	public void setEvoSeq(Long evoSeq) {
		this.evoSeq = evoSeq;
	}

	@Column(name = "ATD_SEQ")
	public Integer getAtdSeq() {
		return this.atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mamPcControleIntern")
	public Set<MamPcControleIntXUnf> getMamPcControleIntXUnfes() {
		return this.mamPcControleIntXUnfes;
	}

	public void setMamPcControleIntXUnfes(Set<MamPcControleIntXUnf> mamPcControleIntXUnfes) {
		this.mamPcControleIntXUnfes = mamPcControleIntXUnfes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		TIPO("tipo"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		ASU_APA_ATD_SEQ("asuApaAtdSeq"),
		ASU_APA_SEQ("asuApaSeq"),
		ASU_SEQP("asuSeqp"),
		RGT_SEQ("rgtSeq"),
		ANA_SEQ("anaSeq"),
		EVO_SEQ("evoSeq"),
		ATD_SEQ("atdSeq"),
		MAM_PC_CONTROLE_INT_X_UNFES("mamPcControleIntXUnfes");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MamPcControleIntern)) {
			return false;
		}
		MamPcControleIntern other = (MamPcControleIntern) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}