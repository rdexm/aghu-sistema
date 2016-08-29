package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;

public class FatcVerCarPhiCnvVO implements Serializable {

	private static final long serialVersionUID = -208511876161199823L;

	private boolean result;
	private Integer valorNumerico;
	private String valorChar;
	private Date valorData;
	private Short iphPhoSeq;
	private Integer iphSeq;

	public enum Fields {
		IPH_SEQ("iphSeq"),
		IPH_PHO_SEQ("iphPhoSeq"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
		
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Integer getValorNumerico() {
		return valorNumerico;
	}

	public void setValorNumerico(Integer valorNumerico) {
		this.valorNumerico = valorNumerico;
	}

	public String getValorChar() {
		return valorChar;
	}

	public void setValorChar(String valorChar) {
		this.valorChar = valorChar;
	}

	public Date getValorData() {
		return valorData;
	}

	public void setValorData(Date valorData) {
		this.valorData = valorData;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

}
