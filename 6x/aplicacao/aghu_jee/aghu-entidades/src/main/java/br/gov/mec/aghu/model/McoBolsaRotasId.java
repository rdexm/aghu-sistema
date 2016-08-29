package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class McoBolsaRotasId implements EntityCompositeId {

	private static final long serialVersionUID = 4015338643922159680L;
	private Integer gsoPacCodigo;
	private Short gsoSeqp;

	public McoBolsaRotasId() {
	}

	public McoBolsaRotasId(Integer gsoPacCodigo, Short gsoSeqp) {
		this.gsoPacCodigo = gsoPacCodigo;
		this.gsoSeqp = gsoSeqp;
	}

	@Column(name = "GSO_PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	@NotNull
	public Integer getGsoPacCodigo() {
		return this.gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}

	@Column(name = "GSO_SEQP", nullable = false, precision = 3, scale = 0)
	@NotNull
	public Short getGsoSeqp() {
		return this.gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof McoBolsaRotasId)) {
			return false;
		}
		McoBolsaRotasId castOther = (McoBolsaRotasId) other;

		return ((getGsoPacCodigo() == castOther.getGsoPacCodigo())
				|| (getGsoPacCodigo() != null && getGsoPacCodigo().equals(castOther.getGsoPacCodigo())))
			&& ((getGsoSeqp() == castOther.getGsoSeqp())
				|| (getGsoSeqp() != null && getGsoSeqp().equals(castOther.getGsoSeqp())));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + (getGsoPacCodigo() == null ? 0 : getGsoPacCodigo().hashCode());
		result = 37 * result + (getGsoSeqp() == null ? 0 : getGsoSeqp().hashCode());
		
		return result;
	}

}
