package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class RnCthcVerPacCtaVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6356839690646503060L;

	private Integer pacCodigo;
	
	private Integer pacProntuario;
	
	private Integer intSeq;
	
	private Boolean retorno;

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}
	
}
