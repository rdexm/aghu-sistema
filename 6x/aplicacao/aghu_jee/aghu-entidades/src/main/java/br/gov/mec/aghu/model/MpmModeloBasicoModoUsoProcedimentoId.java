package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class MpmModeloBasicoModoUsoProcedimentoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4983487679781273802L;
	private Integer modeloBasicoPrescricaoSeq;
	private Short modeloBasicoProcedimentoSeq;
	private Short tipoModoUsoProcedimentoSeq;
	private Short tipoModoUsoSeqp;

	public MpmModeloBasicoModoUsoProcedimentoId() {
	}

	public MpmModeloBasicoModoUsoProcedimentoId(
			Integer modeloBasicoPrescricaoSeq,
			Short modeloBasicoProcedimentoSeq,
			Short tipoModoUsoProcedimentoSeq, Short tipoModoUsoSeqp) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
		this.modeloBasicoProcedimentoSeq = modeloBasicoProcedimentoSeq;
		this.tipoModoUsoProcedimentoSeq = tipoModoUsoProcedimentoSeq;
		this.tipoModoUsoSeqp = tipoModoUsoSeqp;
	}

	// getters & setters
	@Column(name = "MBP_MDB_SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getModeloBasicoPrescricaoSeq() {
		return modeloBasicoPrescricaoSeq;
	}

	public void setModeloBasicoPrescricaoSeq(Integer modeloBasicoPrescricaoSeq) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
	}

	@Column(name = "MBP_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getModeloBasicoProcedimentoSeq() {
		return modeloBasicoProcedimentoSeq;
	}

	public void setModeloBasicoProcedimentoSeq(Short modeloBasicoProcedimentoSeq) {
		this.modeloBasicoProcedimentoSeq = modeloBasicoProcedimentoSeq;
	}

	@Column(name = "TUP_PED_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getTipoModoUsoProcedimentoSeq() {
		return tipoModoUsoProcedimentoSeq;
	}

	public void setTipoModoUsoProcedimentoSeq(Short tipoModoUsoProcedimentoSeq) {
		this.tipoModoUsoProcedimentoSeq = tipoModoUsoProcedimentoSeq;
	}

	@Column(name = "TUP_SEQP", nullable = false, precision = 3, scale = 0)
	public Short getTipoModoUsoSeqp() {
		return tipoModoUsoSeqp;
	}

	public void setTipoModoUsoSeqp(Short tipoModoUsoSeqp) {
		this.tipoModoUsoSeqp = tipoModoUsoSeqp;
	}

	// outros
	@Override
	public boolean equals(Object other) {

		if (!(other instanceof MpmModeloBasicoModoUsoProcedimentoId)) {
			return false;
		}
		
		MpmModeloBasicoModoUsoProcedimentoId castOther = (MpmModeloBasicoModoUsoProcedimentoId) other;
		return new EqualsBuilder()
				.append(this.modeloBasicoPrescricaoSeq,
						castOther.getModeloBasicoPrescricaoSeq())
				.append(this.modeloBasicoProcedimentoSeq,
						castOther.getModeloBasicoProcedimentoSeq())
				.append(this.tipoModoUsoProcedimentoSeq,
						castOther.getTipoModoUsoProcedimentoSeq())
				.append(this.tipoModoUsoSeqp, castOther.getTipoModoUsoSeqp())
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("modeloBasicoPrescricaoSeq",
						this.modeloBasicoPrescricaoSeq)
				.append("modeloBasicoProcedimentoSeq",
						this.modeloBasicoProcedimentoSeq)
				.append("tipoModoUsoProcedimentoSeq",
						this.tipoModoUsoProcedimentoSeq)
				.append("tipoModoUsoSeqp", this.tipoModoUsoSeqp).toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.modeloBasicoPrescricaoSeq)
				.append(this.modeloBasicoProcedimentoSeq)
				.append(this.tipoModoUsoProcedimentoSeq)
				.append(this.tipoModoUsoSeqp).toHashCode();
	}
	
}
