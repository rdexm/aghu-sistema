package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class FactVerCarPhiCnvVO {
	
	private Boolean vResultado;
	private Integer pVlrNumber;
	private String pVlrChar;
	private Date pVlrDate;
	
	public FactVerCarPhiCnvVO(Boolean vResultado, Integer pVlrNumber,
			String pVlrChar, Date pVlrDate) {
		super();
		this.vResultado = vResultado;
		this.pVlrNumber = pVlrNumber;
		this.pVlrChar = pVlrChar;
		this.pVlrDate = pVlrDate;
	}
	
	public Boolean getvResultado() {
		return vResultado;
	}
	public void setvResultado(Boolean vResultado) {
		this.vResultado = vResultado;
	}
	public Integer getpVlrNumber() {
		return pVlrNumber;
	}
	public void setpVlrNumber(Integer pVlrNumber) {
		this.pVlrNumber = pVlrNumber;
	}
	public String getpVlrChar() {
		return pVlrChar;
	}
	public void setpVlrChar(String pVlrChar) {
		this.pVlrChar = pVlrChar;
	}
	public Date getpVlrDate() {
		return pVlrDate;
	}
	public void setpVlrDate(Date pVlrDate) {
		this.pVlrDate = pVlrDate;
	}
}
