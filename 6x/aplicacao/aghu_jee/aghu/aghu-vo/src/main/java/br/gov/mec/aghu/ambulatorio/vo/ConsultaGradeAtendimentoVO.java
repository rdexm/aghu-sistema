package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

public class ConsultaGradeAtendimentoVO  implements Serializable{
	
	private static final long serialVersionUID = -4195817103912553934L;


	private Date dthrInicio;
	private Date dtConsulta;
	private Integer grdSeq;
	private Integer atdSeq;
	private Short espSeq;
	private Integer conNumero;
	private String espSigla;
	
	
	public String getEspSigla() {
		return espSigla;
	}
	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}
	public Integer getConNumero() {
		return conNumero;
	}
	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	public Date getDthrInicio() {
		return dthrInicio;
	}
	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}
	public Integer getGrdSeq() {
		return grdSeq;
	}
	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Date getDtConsulta() {
		return dtConsulta;
	}
	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}


	

}
