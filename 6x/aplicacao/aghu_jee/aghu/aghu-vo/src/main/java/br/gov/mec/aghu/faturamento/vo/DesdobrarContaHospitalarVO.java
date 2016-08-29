package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class DesdobrarContaHospitalarVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8451499160407611600L;

	private String mensagem;

	private Boolean retorno;

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}

}
