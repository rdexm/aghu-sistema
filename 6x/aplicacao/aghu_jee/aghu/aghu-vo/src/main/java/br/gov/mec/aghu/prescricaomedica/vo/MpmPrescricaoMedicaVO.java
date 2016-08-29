package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

public class MpmPrescricaoMedicaVO implements Serializable {
	
	private static final long serialVersionUID = -2912587321708530893L;
	private Integer matriculaValida;
	private Integer matriculaVincValida;
	private Date criadoEm;
	
	
	public Integer getMatriculaValida() {
		return matriculaValida;
	}
	public void setMatriculaValida(Integer matriculaValida) {
		this.matriculaValida = matriculaValida;
	}
	public Integer getMatriculaVincValida() {
		return matriculaVincValida;
	}
	public void setMatriculaVincValida(Integer matriculaVincValida) {
		this.matriculaVincValida = matriculaVincValida;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
}
