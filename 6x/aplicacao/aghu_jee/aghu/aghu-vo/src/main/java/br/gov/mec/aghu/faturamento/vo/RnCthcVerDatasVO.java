package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class RnCthcVerDatasVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4546209194268890759L;

	private Date dataLimite;

	private Integer phiSeq;

	private Boolean retorno;

	public Date getDataLimite() {
		return dataLimite;
	}

	public void setDataLimite(Date dataLimite) {
		this.dataLimite = dataLimite;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}
	
}
