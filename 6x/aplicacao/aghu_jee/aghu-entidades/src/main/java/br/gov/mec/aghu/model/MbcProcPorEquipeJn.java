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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;


@Entity
@Table(name = "MBC_PROC_POR_EQUIPES_JN", schema = "AGH")
@SequenceGenerator(name = "mbcPpxqJnSeq", sequenceName = "AGH.MBC_PXQ_JN_SEQ", allocationSize = 1)
@Immutable
public class MbcProcPorEquipeJn extends BaseJournal {

	private static final long serialVersionUID = 1480648022521697311L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer serMatriculaPrf;
	private Short serVinCodigoPrf;
	private Short unfSeq;
	private Integer pciSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date criadoEm;

	public MbcProcPorEquipeJn() {
	}

	public MbcProcPorEquipeJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer serMatriculaPrf,
			Short serVinCodigoPrf, Short unfSeq, Integer pciSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.serMatriculaPrf = serMatriculaPrf;
		this.serVinCodigoPrf = serVinCodigoPrf;
		this.unfSeq = unfSeq;
		this.pciSeq = pciSeq;
	}

	public MbcProcPorEquipeJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer serMatriculaPrf,
			Short serVinCodigoPrf, Short unfSeq, Integer pciSeq, Integer serMatricula, Short serVinCodigo, Date criadoEm) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.serMatriculaPrf = serMatriculaPrf;
		this.serVinCodigoPrf = serVinCodigoPrf;
		this.unfSeq = unfSeq;
		this.pciSeq = pciSeq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.criadoEm = criadoEm;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcPpxqJnSeq")
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

	@Column(name = "SER_MATRICULA_PRF", nullable = false)
	public Integer getSerMatriculaPrf() {
		return this.serMatriculaPrf;
	}

	public void setSerMatriculaPrf(Integer serMatriculaPrf) {
		this.serMatriculaPrf = serMatriculaPrf;
	}

	@Column(name = "SER_VIN_CODIGO_PRF", nullable = false)
	public Short getSerVinCodigoPrf() {
		return this.serVinCodigoPrf;
	}

	public void setSerVinCodigoPrf(Short serVinCodigoPrf) {
		this.serVinCodigoPrf = serVinCodigoPrf;
	}

	@Column(name = "UNF_SEQ", nullable = false)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "PCI_SEQ", nullable = false)
	public Integer getPciSeq() {
		return this.pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
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
		SER_MATRICULA_PRF("serMatriculaPrf"),
		SER_VIN_CODIGO_PRF("serVinCodigoPrf"),
		UNF_SEQ("unfSeq"),
		PCI_SEQ("pciSeq"),
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

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCriadoEm());
		umHashCodeBuilder.append(this.getPciSeq());
		umHashCodeBuilder.append(this.getSerMatricula());
		umHashCodeBuilder.append(this.getSerMatriculaPrf());
		umHashCodeBuilder.append(this.getSerVinCodigo());
		umHashCodeBuilder.append(this.getSerVinCodigoPrf());
		umHashCodeBuilder.append(this.getUnfSeq());
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MbcProcPorEquipeJn)) {
			return false;
		}
		MbcProcPorEquipeJn other = (MbcProcPorEquipeJn) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCriadoEm(), other.getCriadoEm());
		umEqualsBuilder.append(this.getPciSeq(), other.getPciSeq());
		umEqualsBuilder.append(this.getSerMatricula(), other.getSerMatricula());
		umEqualsBuilder.append(this.getSerMatriculaPrf(), other.getSerMatriculaPrf());
		umEqualsBuilder.append(this.getSerVinCodigo(), other.getSerVinCodigo());
		umEqualsBuilder.append(this.getSerVinCodigoPrf(), other.getSerVinCodigoPrf());
		umEqualsBuilder.append(this.getUnfSeq(), other.getUnfSeq());
		return umEqualsBuilder.isEquals();
	}

}