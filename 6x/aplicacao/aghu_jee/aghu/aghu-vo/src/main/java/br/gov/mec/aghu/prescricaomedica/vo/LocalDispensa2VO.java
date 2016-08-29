package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigDecimal;

public class LocalDispensa2VO {

	private Integer atendimentoSeq;
	private Integer medMatCodigo;
	private BigDecimal dose;
	private Integer fdsSeq;

	public LocalDispensa2VO(Integer atendimentoSeq, Integer medMatCodigo, BigDecimal dose, Integer fdsSeq) {
		this.atendimentoSeq = atendimentoSeq;
		this.medMatCodigo = medMatCodigo;
		this.dose = dose;
		this.fdsSeq = fdsSeq;
	}

	public enum Fields {

		ATD_SEQ("atendimentoSeq"), 
		MED_MAT_CODIGO("medMatCodigo"), 
		DOSE("dose"), 
		FDSSEQ("fdsSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}

	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	public BigDecimal getDose() {
		return dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}

	public Integer getFdsSeq() {
		return fdsSeq;
	}

	public void setFdsSeq(Integer fdsSeq) {
		this.fdsSeq = fdsSeq;
	}

}
