package br.gov.mec.aghu.prescricaomedica.vo;

public class LocalDispensaVO {
	private Short atendimentoUnidadeFuncionalSeq;
	private Short unidadeFuncionalDispDoseInt;
	private Short unidadeFuncionalDispDoseFrac;
	private Short unidadeFuncionalDispAlternativa;

	public LocalDispensaVO(Short atendimentoUnidadeFuncionalSeq,
			Short unidadeFuncionalDispDoseInt,
			Short unidadeFuncionalDispDoseFrac,
			Short unidadeFuncionalDispAlternativa) {
		this.atendimentoUnidadeFuncionalSeq = atendimentoUnidadeFuncionalSeq;
		this.unidadeFuncionalDispDoseInt = unidadeFuncionalDispDoseInt;
		this.unidadeFuncionalDispDoseFrac = unidadeFuncionalDispDoseFrac;
		this.unidadeFuncionalDispAlternativa = unidadeFuncionalDispAlternativa;
	}
	
	public enum Fields {

		ATENDIMENTO_UNIDADE_FUNCIONAL_SEQ("atendimentoUnidadeFuncionalSeq"),
		ATENDIMENTO_UNIDADE_FUNCIONAL_DOSE_INT("unidadeFuncionalDispDoseInt"),
		ATENDIMENTO_UNIDADE_FUNCIONAL_DOSE_FRAC("unidadeFuncionalDispDoseFrac"),
		ATENDIMENTO_UNIDADE_FUNCIONAL_DISP_ALTERNATIVA("unidadeFuncionalDispAlternativa");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	public Short getAtendimentoUnidadeFuncionalSeq() {
		return atendimentoUnidadeFuncionalSeq;
	}

	public void setAtendimentoUnidadeFuncionalSeq(
			Short atendimentoUnidadeFuncionalSeq) {
		this.atendimentoUnidadeFuncionalSeq = atendimentoUnidadeFuncionalSeq;
	}

	public Short getUnidadeFuncionalDispDoseInt() {
		return unidadeFuncionalDispDoseInt;
	}

	public void setUnidadeFuncionalDispDoseInt(Short unidadeFuncionalDispDoseInt) {
		this.unidadeFuncionalDispDoseInt = unidadeFuncionalDispDoseInt;
	}

	public Short getUnidadeFuncionalDispDoseFrac() {
		return unidadeFuncionalDispDoseFrac;
	}

	public void setUnidadeFuncionalDispDoseFrac(
			Short unidadeFuncionalDispDoseFrac) {
		this.unidadeFuncionalDispDoseFrac = unidadeFuncionalDispDoseFrac;
	}

	public Short getUnidadeFuncionalDispAlternativa() {
		return unidadeFuncionalDispAlternativa;
	}

	public void setUnidadeFuncionalDispAlternativa(
			Short unidadeFuncionalDispAlternativa) {
		this.unidadeFuncionalDispAlternativa = unidadeFuncionalDispAlternativa;
	}

}
