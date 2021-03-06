package br.gov.mec.aghu.model;

// Generated 26/02/2010 17:37:25 by Hibernate Tools 3.2.5.Beta

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * McoIntercorrenciaGestacoesId generated by hbm2java
 */
@Embeddable
public class McoIntercorrenciaGestacoesId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2705899956678150480L;
	private Integer gsoPacCodigo;
	private Short gsoSeqp;
	private BigDecimal opaSeq;

	public McoIntercorrenciaGestacoesId() {
	}

	public McoIntercorrenciaGestacoesId(Integer gsoPacCodigo, Short gsoSeqp,
			BigDecimal opaSeq) {
		this.gsoPacCodigo = gsoPacCodigo;
		this.gsoSeqp = gsoSeqp;
		this.opaSeq = opaSeq;
	}

	@Column(name = "GSO_PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	public Integer getGsoPacCodigo() {
		return this.gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}

	@Column(name = "GSO_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getGsoSeqp() {
		return this.gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	@Column(name = "OPA_SEQ", nullable = false, precision = 22, scale = 0)
	public BigDecimal getOpaSeq() {
		return this.opaSeq;
	}

	public void setOpaSeq(BigDecimal opaSeq) {
		this.opaSeq = opaSeq;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof McoIntercorrenciaGestacoesId)) {
			return false;
		}
		McoIntercorrenciaGestacoesId castOther = (McoIntercorrenciaGestacoesId) other;

		return ((getGsoPacCodigo() == castOther.getGsoPacCodigo()) 
				|| (getGsoPacCodigo() != null && getGsoPacCodigo().equals(castOther.getGsoPacCodigo())))
			&& ((getGsoSeqp() == castOther.getGsoSeqp())
				|| (getGsoSeqp() != null && getGsoSeqp().equals(castOther.getGsoSeqp())))
			&& ((getOpaSeq() == castOther.getOpaSeq())
				|| (getOpaSeq() != null && getOpaSeq().equals(castOther.getOpaSeq())));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + (getGsoPacCodigo() == null ? 0 : getGsoPacCodigo().hashCode());
		result = 37 * result + (getGsoSeqp() == null ? 0 : getGsoSeqp().hashCode());
		result = 37 * result+ (getOpaSeq() == null ? 0 : this.getOpaSeq().hashCode());
		return result;
	}

}
