package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable

public class MpmModeloBasicoDietaId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6568929430856751996L;
	private Integer modeloBasicoPrescricaoSeq;
	private Integer seq;

	// construtores

	public MpmModeloBasicoDietaId() {
	}

	public MpmModeloBasicoDietaId(Integer mdbSeq, Integer seq) {
		this.modeloBasicoPrescricaoSeq = mdbSeq;
		this.seq = seq;
	}

	@Column(name = "MDB_SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getModeloBasicoPrescricaoSeq() {
		return this.modeloBasicoPrescricaoSeq;
	}

	public void setModeloBasicoPrescricaoSeq(Integer mdbSeq) {
		this.modeloBasicoPrescricaoSeq = mdbSeq;
	}

	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("mdbSeq",
				this.modeloBasicoPrescricaoSeq).append("seq", this.seq)
				.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmModeloBasicoDietaId)) {
			return false;
		}
		MpmModeloBasicoDietaId castOther = (MpmModeloBasicoDietaId) other;
		return new EqualsBuilder().append(this.modeloBasicoPrescricaoSeq,
				castOther.getModeloBasicoPrescricaoSeq()).append(this.seq,
				castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.modeloBasicoPrescricaoSeq)
				.append(this.seq).toHashCode();
	}

}
