package br.gov.mec.aghu.exames.vo;

import java.util.Date;

public class AelItemSolicConsultadoVO {
	
	private String servidor;
	private Date criadoEm;
	private Integer matricula;
	private Short vinculo;
	
	public String getServidor() {
		return servidor;
	}
	public void setServidor(String servidor) {
		this.servidor = servidor;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinculo() {
		return vinculo;
	}
	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}
	
	
	
}
