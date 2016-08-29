package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class RnCthcVerRegraespVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2254540786184677182L;

	private Integer codExclusaoCritica;
	
	private Boolean retorno;

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
