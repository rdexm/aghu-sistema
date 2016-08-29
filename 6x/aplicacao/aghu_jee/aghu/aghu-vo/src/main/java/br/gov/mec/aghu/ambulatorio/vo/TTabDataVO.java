package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class TTabDataVO implements Serializable {
	
	private static final long serialVersionUID = -2988359736483755250L;
	
	private Integer vContTAb;
	private Integer vLimTab = 3;
	private Integer vAno;
	private Integer vMes;
	private Integer vData;
	private Integer vPeriodoAtual;
	private Integer vPeriodoAnt;
	private Boolean vPrimeiraVez;
	
	public Integer getvContTAb() {
		return vContTAb;
	}
	
	public void setvContTAb(Integer vContTAb) {
		this.vContTAb = vContTAb;
	}
	
	public Integer getvLimTab() {
		return vLimTab;
	}
	
	public void setvLimTab(Integer vLimTab) {
		this.vLimTab = vLimTab;
	}
	
	public Integer getvAno() {
		return vAno;
	}
	
	public void setvAno(Integer vAno) {
		this.vAno = vAno;
	}
	
	public Integer getvMes() {
		return vMes;
	}
	
	public void setvMes(Integer vMes) {
		this.vMes = vMes;
	}
	
	public Integer getvData() {
		return vData;
	}
	
	public void setvData(Integer vData) {
		this.vData = vData;
	}
	
	public Integer getvPeriodoAtual() {
		return vPeriodoAtual;
	}
	
	public void setvPeriodoAtual(Integer vPeriodoAtual) {
		this.vPeriodoAtual = vPeriodoAtual;
	}
	
	public Integer getvPeriodoAnt() {
		return vPeriodoAnt;
	}
	
	public void setvPeriodoAnt(Integer vPeriodoAnt) {
		this.vPeriodoAnt = vPeriodoAnt;
	}
	
	public Boolean getvPrimeiraVez() {
		return vPrimeiraVez;
	}
	
	public void setvPrimeiraVez(Boolean vPrimeiraVez) {
		this.vPrimeiraVez = vPrimeiraVez;
	}
}