package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class CaracteristicaPhiVO implements Serializable {
		
	private static final long serialVersionUID = -3975623930015627474L;

	private Integer valorNumerico;
	
	private String valorChar;
	
	private Date valorData;
	
	private Boolean resultado;

	public Integer getValorNumerico() {
		return valorNumerico;
	}

	public void setValorNumerico(Integer valorNumerico) {
		this.valorNumerico = valorNumerico;
	}

	public String getValorChar() {
		return valorChar;
	}

	public void setValorChar(String valorChar) {
		this.valorChar = valorChar;
	}

	public Date getValorData() {
		return valorData;
	}

	public void setValorData(Date valorData) {
		this.valorData = valorData;
	}

	public Boolean getResultado() {
		return resultado;
	}

	public void setResultado(Boolean resultado) {
		this.resultado = resultado;
	}

	
}