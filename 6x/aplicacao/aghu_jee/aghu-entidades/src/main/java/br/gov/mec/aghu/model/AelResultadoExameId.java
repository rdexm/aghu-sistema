package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class AelResultadoExameId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7601356377416285684L;
	private Integer iseSoeSeq;
	private Short iseSeqp;
	private String pclVelEmaExaSigla;
	private Integer pclVelEmaManSeq;
	private Integer pclVelSeqp;
	private Integer pclCalSeq;
	private Integer pclSeqp;
	private Integer seqp;

	public AelResultadoExameId() {
	}

	public AelResultadoExameId(Integer iseSoeSeq, Short iseSeqp,
			String pclVelEmaExaSigla, Integer pclVelEmaManSeq, Integer pclVelSeqp,
			Integer pclCalSeq, Integer pclSeqp, Integer seqp) {
		this.iseSoeSeq = iseSoeSeq;
		this.iseSeqp = iseSeqp;
		this.pclVelEmaExaSigla = pclVelEmaExaSigla;
		this.pclVelEmaManSeq = pclVelEmaManSeq;
		this.pclVelSeqp = pclVelSeqp;
		this.pclCalSeq = pclCalSeq;
		this.pclSeqp = pclSeqp;
		this.seqp = seqp;
	}

	@Column(name = "ISE_SOE_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getIseSoeSeq() {
		return this.iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	@Column(name = "ISE_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getIseSeqp() {
		return this.iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	@Column(name = "PCL_VEL_EMA_EXA_SIGLA", nullable = false, length = 5)
	@Length(max = 5)
	public String getPclVelEmaExaSigla() {
		return this.pclVelEmaExaSigla;
	}

	public void setPclVelEmaExaSigla(String pclVelEmaExaSigla) {
		this.pclVelEmaExaSigla = pclVelEmaExaSigla;
	}

	@Column(name = "PCL_VEL_EMA_MAN_SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getPclVelEmaManSeq() {
		return this.pclVelEmaManSeq;
	}

	public void setPclVelEmaManSeq(Integer pclVelEmaManSeq) {
		this.pclVelEmaManSeq = pclVelEmaManSeq;
	}

	@Column(name = "PCL_VEL_SEQP", nullable = false, precision = 5, scale = 0)
	public Integer getPclVelSeqp() {
		return this.pclVelSeqp;
	}

	public void setPclVelSeqp(Integer pclVelSeqp) {
		this.pclVelSeqp = pclVelSeqp;
	}

	@Column(name = "PCL_CAL_SEQ", nullable = false, precision = 7, scale = 0)
	public Integer getPclCalSeq() {
		return this.pclCalSeq;
	}

	public void setPclCalSeq(Integer pclCalSeq) {
		this.pclCalSeq = pclCalSeq;
	}

	@Column(name = "PCL_SEQP", nullable = false, precision = 5, scale = 0)
	public Integer getPclSeqp() {
		return this.pclSeqp;
	}

	public void setPclSeqp(Integer pclSeqp) {
		this.pclSeqp = pclSeqp;
	}

	@Column(name = "SEQP", nullable = false, precision = 5, scale = 0)
	public Integer getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getPclVelEmaExaSigla());
		umHashCodeBuilder.append(this.getPclVelEmaManSeq());
		umHashCodeBuilder.append(this.getPclVelSeqp());
		umHashCodeBuilder.append(this.getPclCalSeq());
		umHashCodeBuilder.append(this.getPclSeqp());
		umHashCodeBuilder.append(this.getIseSoeSeq());
		umHashCodeBuilder.append(this.getIseSeqp());
		umHashCodeBuilder.append(this.getSeqp());
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
		if (!(obj instanceof AelResultadoExameId)) {
			return false;
		}
		AelResultadoExameId other = (AelResultadoExameId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getPclVelEmaExaSigla(), other.getPclVelEmaExaSigla());
		umEqualsBuilder.append(this.getPclVelEmaManSeq(), other.getPclVelEmaManSeq());
		umEqualsBuilder.append(this.getPclVelSeqp(), other.getPclVelSeqp());
		umEqualsBuilder.append(this.getPclCalSeq(), other.getPclCalSeq());
		umEqualsBuilder.append(this.getPclSeqp(), other.getPclSeqp());
		umEqualsBuilder.append(this.getIseSoeSeq(), other.getIseSoeSeq());
		umEqualsBuilder.append(this.getIseSeqp(), other.getIseSeqp());
		umEqualsBuilder.append(this.getSeqp(), other.getSeqp());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
