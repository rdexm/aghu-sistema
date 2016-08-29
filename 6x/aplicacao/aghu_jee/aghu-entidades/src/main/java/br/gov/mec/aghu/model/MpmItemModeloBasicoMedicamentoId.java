package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable

public class MpmItemModeloBasicoMedicamentoId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1611671775008951845L;
	private Integer modeloBasicoPrescricaoSeq;
	private Integer modeloBasicoMedicamentoSeq;
	private Integer medicamentoMaterialCodigo;
	private Integer seqp;

	public MpmItemModeloBasicoMedicamentoId() {
	}

	public MpmItemModeloBasicoMedicamentoId(Integer modeloBasicoPrescricaoSeq,
			Integer modeloBasicoMedicamentoSeq,
			Integer medicamentoMaterialCodigo, Integer seqp) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
		this.modeloBasicoMedicamentoSeq = modeloBasicoMedicamentoSeq;
		this.medicamentoMaterialCodigo = medicamentoMaterialCodigo;
		this.seqp = seqp;
	}

	@Column(name = "MBM_MDB_SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getModeloBasicoPrescricaoSeq() {
		return modeloBasicoPrescricaoSeq;
	}

	public void setModeloBasicoPrescricaoSeq(Integer modeloBasicoPrescricaoSeq) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
	}

	@Column(name = "MBM_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getModeloBasicoMedicamentoSeq() {
		return modeloBasicoMedicamentoSeq;
	}

	public void setModeloBasicoMedicamentoSeq(Integer modeloBasicoMedicamentoSeq) {
		this.modeloBasicoMedicamentoSeq = modeloBasicoMedicamentoSeq;
	}

	@Column(name = "MED_MAT_CODIGO", nullable = false, precision = 6, scale = 0)
	public Integer getMedicamentoMaterialCodigo() {
		return medicamentoMaterialCodigo;
	}

	public void setMedicamentoMaterialCodigo(Integer medicamentoMaterialCodigo) {
		this.medicamentoMaterialCodigo = medicamentoMaterialCodigo;
	}

	@Column(name = "SEQP", nullable = false, precision = 5, scale = 0)
	public Integer getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof MpmItemModeloBasicoMedicamentoId)) {
			return false;
		}
		MpmItemModeloBasicoMedicamentoId castOther = (MpmItemModeloBasicoMedicamentoId) other;

		return (this.getModeloBasicoPrescricaoSeq().equals(castOther
				.getModeloBasicoPrescricaoSeq()))
				&& (this.getModeloBasicoMedicamentoSeq().equals(castOther
						.getModeloBasicoMedicamentoSeq()))
				&& (this.getMedicamentoMaterialCodigo().equals(castOther
						.getMedicamentoMaterialCodigo()))
				&& (this.getSeqp().equals(castOther.getSeqp()));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getModeloBasicoPrescricaoSeq();
		result = 37 * result + this.getModeloBasicoMedicamentoSeq();
		result = 37 * result + this.getMedicamentoMaterialCodigo();
		result = 37 * result + this.getSeqp();
		return result;
	}
}
