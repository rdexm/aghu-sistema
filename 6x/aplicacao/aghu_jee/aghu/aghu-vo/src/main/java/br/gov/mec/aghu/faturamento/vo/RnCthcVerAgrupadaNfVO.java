package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class RnCthcVerAgrupadaNfVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6877453709886103784L;

	private Integer ipsRmpSeq;

	private Short ipsNumero;

	private Boolean retorno;

	public Integer getIpsRmpSeq() {
		return ipsRmpSeq;
	}

	public void setIpsRmpSeq(Integer ipsRmpSeq) {
		this.ipsRmpSeq = ipsRmpSeq;
	}

	public Short getIpsNumero() {
		return ipsNumero;
	}

	public void setIpsNumero(Short ipsNumero) {
		this.ipsNumero = ipsNumero;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}

}
