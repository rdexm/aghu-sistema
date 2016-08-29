package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.sql.Timestamp;

public class AihsFaturadasPorClinicaVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7258773426530640337L;

	private String dcih;
	
	private String dcihlabel;
	
	private Integer codcli;
	
	private String desccli;
	
	private Integer prontuario;
	
	private String prontuarioformatado;
	
	private String pacnome;
	
	private Long ssmrealiz;

	private Timestamp dtint;
	
	private Timestamp dtalta;
	
	private Long aih;
	
	private String clinica;

	public String getDcih() {
		return dcih;
	}

	public void setDcih(String dcih) {
		this.dcih = dcih;
	}

	public Integer getCodcli() {
		return codcli;
	}

	public void setCodcli(Integer codcli) {
		this.codcli = codcli;
	}

	public String getDesccli() {
		return desccli;
	}

	public void setDesccli(String desccli) {
		this.desccli = desccli;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getPacnome() {
		return pacnome;
	}

	public void setPacnome(String pacnome) {
		this.pacnome = pacnome;
	}

	public Long getSsmrealiz() {
		return ssmrealiz;
	}

	public void setSsmrealiz(Long ssmrealiz) {
		this.ssmrealiz = ssmrealiz;
	}

	public Long getAih() {
		return aih;
	}

	public void setAih(Long aih) {
		this.aih = aih;
	}

	public Timestamp getDtint() {
		return dtint;
	}

	public void setDtint(Timestamp dtint) {
		this.dtint = dtint;
	}

	public Timestamp getDtalta() {
		return dtalta;
	}

	public void setDtalta(Timestamp dtalta) {
		this.dtalta = dtalta;
	}

	public String getProntuarioformatado() {
		return prontuarioformatado;
	}

	public void setProntuarioformatado(String prontuarioformatado) {
		this.prontuarioformatado = prontuarioformatado;
	}

	public String getClinica() {
		return clinica;
	}

	public void setClinica(String clinica) {
		this.clinica= clinica;
	}

	public String getDcihlabel() {
		return dcihlabel;
	}

	public void setDcihlabel(String dcihlabel) {
		this.dcihlabel = dcihlabel;
	}
}