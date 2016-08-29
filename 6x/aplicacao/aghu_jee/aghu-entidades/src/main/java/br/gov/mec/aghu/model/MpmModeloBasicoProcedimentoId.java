package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable

public class MpmModeloBasicoProcedimentoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8501809358322279660L;
	private Integer modeloBasicoPrescricaoSeq;
	private Short seq;

	public MpmModeloBasicoProcedimentoId() {
	}

	public MpmModeloBasicoProcedimentoId(Integer modeloBasicoPrescricaoSeq,
			Short seq) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
		this.seq = seq;
	}

	@Column(name = "MDB_SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getModeloBasicoPrescricaoSeq() {
		return modeloBasicoPrescricaoSeq;
	}

	public void setModeloBasicoPrescricaoSeq(Integer modeloBasicoPrescricaoSeq) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
	}

	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof MpmModeloBasicoProcedimentoId)) {
			return false;
		}
		MpmModeloBasicoProcedimentoId castOther = (MpmModeloBasicoProcedimentoId) other;

		return (this.getModeloBasicoPrescricaoSeq().equals(castOther
				.getModeloBasicoPrescricaoSeq()))
				&& (this.getSeq().equals(castOther.getSeq()));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getModeloBasicoPrescricaoSeq();
		result = 37 * result + this.getSeq();
		return result;
	}

}
