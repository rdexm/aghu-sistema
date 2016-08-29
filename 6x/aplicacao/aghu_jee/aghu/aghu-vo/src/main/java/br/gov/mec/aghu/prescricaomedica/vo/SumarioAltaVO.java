package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

public class SumarioAltaVO {

	private Integer atdSeq;
	private Short plaSeq;
	private Short mamSeq;
	private String complMotivoAlta;
	private String complPlanoPosAlta;
	private String estadoPacienteAlta;
	private String indNecropsia;
	private Date dthrElaboracaoAlta;
	private Date dthrAlta;
	private Integer matriculaSerValida;
	private Short vinCodigoSerValida;

	public SumarioAltaVO() {
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Short getPlaSeq() {
		return plaSeq;
	}

	public void setPlaSeq(Short plaSeq) {
		this.plaSeq = plaSeq;
	}

	public Short getMamSeq() {
		return mamSeq;
	}

	public void setMamSeq(Short mamSeq) {
		this.mamSeq = mamSeq;
	}

	public String getComplMotivoAlta() {
		return complMotivoAlta;
	}

	public void setComplMotivoAlta(String complMotivoAlta) {
		this.complMotivoAlta = complMotivoAlta;
	}

	public String getComplPlanoPosAlta() {
		return complPlanoPosAlta;
	}

	public void setComplPlanoPosAlta(String complPlanoPosAlta) {
		this.complPlanoPosAlta = complPlanoPosAlta;
	}

	public String getEstadoPacienteAlta() {
		return estadoPacienteAlta;
	}

	public void setEstadoPacienteAlta(String estadoPacienteAlta) {
		this.estadoPacienteAlta = estadoPacienteAlta;
	}

	public String getIndNecropsia() {
		return indNecropsia;
	}

	public void setIndNecropsia(String indNecropsia) {
		this.indNecropsia = indNecropsia;
	}

	public Date getDthrElaboracaoAlta() {
		return dthrElaboracaoAlta;
	}

	public void setDthrElaboracaoAlta(Date dthrElaboracaoAlta) {
		this.dthrElaboracaoAlta = dthrElaboracaoAlta;
	}

	public Date getDthrAlta() {
		return dthrAlta;
	}

	public void setDthrAlta(Date dthrAlta) {
		this.dthrAlta = dthrAlta;
	}

	public Integer getMatriculaSerValida() {
		return matriculaSerValida;
	}

	public void setMatriculaSerValida(Integer matriculaSerValida) {
		this.matriculaSerValida = matriculaSerValida;
	}

	public Short getVinCodigoSerValida() {
		return vinCodigoSerValida;
	}

	public void setVinCodigoSerValida(Short vinCodigoSerValida) {
		this.vinCodigoSerValida = vinCodigoSerValida;
	}
	
	public enum Fields {
		
		SEQ("atdSeq"), 
		MAM_SEQ("mamSeq"),
		PLA_SEQ("plaSeq"),
		DATA_ALTA("dthrAlta"),
		DATA_ELB_ALTA("dthrElaboracaoAlta"),
		IND_NECROPSIA("indNecropsia"),
		MATRICULA_SER_VALIDA("matriculaSerValida"),
		CODIGO_SER_VALIDA("vinCodigoSerValida"),
		COMPL_PLN_POS_ALTA("complPlanoPosAlta"),
		ESTD_PAC_ALTA("estadoPacienteAlta"),
		COMPL_MTV_ALTA("complMotivoAlta");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
