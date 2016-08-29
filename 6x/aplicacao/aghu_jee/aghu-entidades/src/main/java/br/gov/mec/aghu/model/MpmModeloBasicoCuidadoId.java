package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class MpmModeloBasicoCuidadoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7037111098992101691L;
	private Integer modeloBasicoPrescricaoSeq;
	private Integer seq;

	public MpmModeloBasicoCuidadoId() {
	}

	public MpmModeloBasicoCuidadoId(Integer modeloBasicoPrescricaoSeq,
			Integer seq) {
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
		if (!(other instanceof MpmModeloBasicoCuidadoId)) {
			return false;
		}
		MpmModeloBasicoCuidadoId castOther = (MpmModeloBasicoCuidadoId) other;

		return (this.getModeloBasicoPrescricaoSeq() == castOther
				.getModeloBasicoPrescricaoSeq())
				&& (this.getSeq() == castOther.getSeq());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getModeloBasicoPrescricaoSeq();
		result = 37 * result + this.getSeq();
		return result;
	}
}
