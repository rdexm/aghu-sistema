package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;

public class SubFormularioSinaisVitaisVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5001580464590785155L;

	private String cabecalho;
	private String valor;
	
	public String getCabecalho() {
		return cabecalho;
	}
	public void setCabecalho(String cabecalho) {
		this.cabecalho = cabecalho;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	} 
}
