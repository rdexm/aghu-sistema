package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class FiltroParametrosPadraoConsultaVO implements Serializable {
	
	private static final long serialVersionUID = -4666980102783255491L;
	
	private Integer firstResult;
	private Integer maxResult;
	private String orderProperty;
	private boolean ordenacaoAscDesc;
	
	public Integer getFirstResult() {
		return firstResult;
	}
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}
	public Integer getMaxResult() {
		return maxResult;
	}
	public void setMaxResult(Integer maxResult) {
		this.maxResult = maxResult;
	}
	public String getOrderProperty() {
		return orderProperty;
	}
	public void setOrderProperty(String orderProperty) {
		this.orderProperty = orderProperty;
	}
	public boolean isOrdenacaoAscDesc() {
		return ordenacaoAscDesc;
	}
	public void setOrdenacaoAscDesc(boolean ordenacaoAscDesc) {
		this.ordenacaoAscDesc = ordenacaoAscDesc;
	}

}
