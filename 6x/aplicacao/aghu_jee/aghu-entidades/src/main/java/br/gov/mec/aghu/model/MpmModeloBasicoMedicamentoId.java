package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable

public class MpmModeloBasicoMedicamentoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8327868216750354546L;
	private Integer modeloBasicoPrescricaoSeq;
	private Integer seq;

	public MpmModeloBasicoMedicamentoId() {
	}

	public MpmModeloBasicoMedicamentoId(Integer mdbSeq, Integer seq) {
		this.modeloBasicoPrescricaoSeq = mdbSeq;
		this.seq = seq;
	}

	@Column(name = "MDB_SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getModeloBasicoPrescricaoSeq() {
		return modeloBasicoPrescricaoSeq;
	}

	public void setModeloBasicoPrescricaoSeq(Integer modeloBasicoPrescricaoSeq) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
	}

	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
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
		if (!(other instanceof MpmModeloBasicoMedicamentoId)) {
			return false;
		}
		MpmModeloBasicoMedicamentoId castOther = (MpmModeloBasicoMedicamentoId) other;

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
