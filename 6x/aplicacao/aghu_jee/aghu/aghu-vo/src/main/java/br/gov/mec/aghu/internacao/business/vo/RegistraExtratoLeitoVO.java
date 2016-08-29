package br.gov.mec.aghu.internacao.business.vo;

import java.io.Serializable;


public class RegistraExtratoLeitoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4279325439899681279L;

	private String leitoID;
	
	private Boolean indBloqueioLimpeza;
	
	public RegistraExtratoLeitoVO() {
	}
	
	public RegistraExtratoLeitoVO(String leitoID, Boolean indBloqueioLimpeza) {
		this.leitoID = leitoID;
		this.indBloqueioLimpeza = indBloqueioLimpeza;
	}

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public Boolean getIndBloqueioLimpeza() {
		return indBloqueioLimpeza;
	}

	public void setIndBloqueioLimpeza(Boolean indBloqueioLimpeza) {
		this.indBloqueioLimpeza = indBloqueioLimpeza;
	}
	
}
