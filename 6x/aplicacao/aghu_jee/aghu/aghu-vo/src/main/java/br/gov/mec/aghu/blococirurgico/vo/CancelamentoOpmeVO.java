package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class CancelamentoOpmeVO implements Serializable {
	
	private static final long serialVersionUID = -4416334722208802279L;
	
	private String requerente;
	private String justificativa;
	
	public String getRequerente() {
		return requerente;
	}
	public void setRequerente(String requerente) {
		this.requerente = requerente;
	}
	public String getJustificativa() {
		return justificativa;
	}
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

}
