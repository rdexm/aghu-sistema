package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class RnCthcAtuRegrasVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6868015744489024779L;

	private Integer clcRealiz;
	
	private Integer codExclusaoCritica;
	
	private Boolean retorno;

	public Integer getClcRealiz() {
		return clcRealiz;
	}

	public void setClcRealiz(Integer clcRealiz) {
		this.clcRealiz = clcRealiz;
	}

	public Integer getCodExclusaoCritica() {
		return codExclusaoCritica;
	}

	public void setCodExclusaoCritica(Integer codExclusaoCritica) {
		this.codExclusaoCritica = codExclusaoCritica;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}
	
}
