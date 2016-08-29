package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable

public class MpmItemModeloBasicoDietaId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2697032559281630304L;
	private Integer modeloBasicoPrescricaoSeq;
	private Integer modeloBasicoDietaSeq;
	private Integer tipoItemDietaSeq;

	public MpmItemModeloBasicoDietaId() {
	}

	public MpmItemModeloBasicoDietaId(Integer modeloBasicoPrescricaoSeq,
			Integer modeloBasicoDietaSeq, Integer tipoItemDietaSeq) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
		this.modeloBasicoDietaSeq = modeloBasicoDietaSeq;
		this.tipoItemDietaSeq = tipoItemDietaSeq;
	}

	@Column(name = "MBD_MDB_SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getModeloBasicoPrescricaoSeq() {
		return this.modeloBasicoPrescricaoSeq;
	}

	public void setModeloBasicoPrescricaoSeq(Integer mbdMdbSeq) {
		this.modeloBasicoPrescricaoSeq = mbdMdbSeq;
	}

	@Column(name = "MBD_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getModeloBasicoDietaSeq() {
		return this.modeloBasicoDietaSeq;
	}

	public void setModeloBasicoDietaSeq(Integer mbdSeq) {
		this.modeloBasicoDietaSeq = mbdSeq;
	}

	@Column(name = "TID_SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getTipoItemDietaSeq() {
		return this.tipoItemDietaSeq;
	}

	public void setTipoItemDietaSeq(Integer tidSeq) {
		this.tipoItemDietaSeq = tidSeq;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("modeloBasicoPrescricaoSeq",
				this.modeloBasicoPrescricaoSeq).append("modeloBasicoDietaSeq",
				this.modeloBasicoDietaSeq).append("tipoItemDietaSeq",
				this.tipoItemDietaSeq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MpmItemModeloBasicoDietaId)) {
			return false;
		}
		MpmItemModeloBasicoDietaId castOther = (MpmItemModeloBasicoDietaId) other;
		return new EqualsBuilder().append(this.modeloBasicoPrescricaoSeq,
				castOther.getModeloBasicoPrescricaoSeq()).append(
				this.modeloBasicoDietaSeq, castOther.getModeloBasicoDietaSeq())
				.append(this.tipoItemDietaSeq, castOther.getTipoItemDietaSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.modeloBasicoPrescricaoSeq)
				.append(this.modeloBasicoDietaSeq)
				.append(this.tipoItemDietaSeq).toHashCode();
	}
}
