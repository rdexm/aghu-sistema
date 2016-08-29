package br.gov.mec.aghu.paciente.business;

import java.util.Date;

/**
 * Vo resultado do cursor c_pac_consultas
 * 
 * @author riccosta
 * 
 */
public class PacConsultaVo {
	Integer prontuario;
	Date dtConsulta;
	Boolean indImpresso;

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Date getDtConsulta() {
		return dtConsulta;
	}

	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}

	public Boolean getIndImpresso() {
		return indImpresso;
	}

	public void setIndImpresso(Boolean indImpresso) {
		this.indImpresso = indImpresso;
	}

}