package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;


public class BuscaFormaAnteriorVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4666980402783255491L;

	private Short caaSeq;
	
	private Short tagSeq;
	
	private Short pgdSeq;
	
	private Boolean retorno;
	
	public BuscaFormaAnteriorVO() {
	}

	public Short getCaaSeq() {
		return caaSeq;
	}

	public void setCaaSeq(Short caaSeq) {
		this.caaSeq = caaSeq;
	}

	public Short getTagSeq() {
		return tagSeq;
	}

	public void setTagSeq(Short tagSeq) {
		this.tagSeq = tagSeq;
	}

	public Short getPgdSeq() {
		return pgdSeq;
	}

	public void setPgdSeq(Short pgdSeq) {
		this.pgdSeq = pgdSeq;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}
	
}
