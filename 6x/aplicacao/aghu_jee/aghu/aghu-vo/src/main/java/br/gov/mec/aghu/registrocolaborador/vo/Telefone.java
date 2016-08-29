package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

public class Telefone  implements Serializable {
	
	private static final long serialVersionUID = -4703110007543388205L;
	
	private String ddi;
	private String ddd;
	private String number;
	private String ramal;
	private String qualificador;
	public String getDdi() {
		return ddi;
	}
	public void setDdi(String ddi) {
		this.ddi = ddi;
	}
	public String getDdd() {
		return ddd;
	}
	public void setDdd(String ddd) {
		this.ddd = ddd;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getRamal() {
		return ramal;
	}
	public void setRamal(String ramal) {
		this.ramal = ramal;
	}
	public String getQualificador() {
		return qualificador;
	}
	public void setQualificador(String qualificador) {
		this.qualificador = qualificador;
	}

}
