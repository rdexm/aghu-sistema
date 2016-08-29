package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class RnCthcVerUtineoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2846956515505984266L;

	private String utiNeo;
	
	private Boolean retorno;

	public String getUtiNeo() {
		return utiNeo;
	}

	public void setUtiNeo(String utiNeo) {
		this.utiNeo = utiNeo;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}
	
}
