package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MptItemPrescricaoMedicamentoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 772255569641191580L;
	private Integer pmoPteAtdSeq;
	private Integer pmoPteSeq;
	private Integer pmoSeq;
	private Short seqp;

	public MptItemPrescricaoMedicamentoId() {
	}

	public MptItemPrescricaoMedicamentoId(Integer pmoPteAtdSeq, Integer pmoPteSeq,
			Integer pmoSeq, Short seqp) {
		this.pmoPteAtdSeq = pmoPteAtdSeq;
		this.pmoPteSeq = pmoPteSeq;
		this.pmoSeq = pmoSeq;
		this.seqp = seqp;
	}

	@Column(name = "PMO_PTE_ATD_SEQ", nullable = false, precision = 7, scale = 0)
	public Integer getPmoPteAtdSeq() {
		return this.pmoPteAtdSeq;
	}

	public void setPmoPteAtdSeq(Integer pmoPteAtdSeq) {
		this.pmoPteAtdSeq = pmoPteAtdSeq;
	}

	@Column(name = "PMO_PTE_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getPmoPteSeq() {
		return this.pmoPteSeq;
	}

	public void setPmoPteSeq(Integer pmoPteSeq) {
		this.pmoPteSeq = pmoPteSeq;
	}

	@Column(name = "PMO_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getPmoSeq() {
		return this.pmoSeq;
	}

	public void setPmoSeq(Integer pmoSeq) {
		this.pmoSeq = pmoSeq;
	}

	@Column(name = "SEQP", nullable = false, precision = 3, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeqp());
		umHashCodeBuilder.append(this.getPmoPteAtdSeq());
		umHashCodeBuilder.append(this.getPmoPteSeq());
		umHashCodeBuilder.append(this.getPmoSeq());
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
		if (!(obj instanceof MptItemPrescricaoMedicamentoId)) {
			return false;
		}
		MptItemPrescricaoMedicamentoId other = (MptItemPrescricaoMedicamentoId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeqp(), other.getSeqp());
		umEqualsBuilder.append(this.getPmoPteAtdSeq(), other.getPmoPteAtdSeq());
		umEqualsBuilder.append(this.getPmoPteSeq(), other.getPmoPteSeq());
		umEqualsBuilder.append(this.getPmoSeq(), other.getPmoSeq());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
