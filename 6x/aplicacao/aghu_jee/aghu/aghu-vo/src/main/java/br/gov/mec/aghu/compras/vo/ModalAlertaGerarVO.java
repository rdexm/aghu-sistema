package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

public class ModalAlertaGerarVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7054020593896336204L;
	
	private String titulo;
	private String alerta;
	
	public String getTitulo() {
		return titulo;
	}
	
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	public String getAlerta() {
		return alerta;
	}
	
	public void setAlerta(String alerta) {
		this.alerta = alerta;
	}
}
