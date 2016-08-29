package br.gov.mec.aghu.compras.pac.vo;

import java.io.Serializable;

public class FornecedoresParticipantesVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8467372741903260889L;
	
	private String razaoSocial;
	private Long cgcCnpj;
	private String dtValidadeFgts;
	private String dtValidadeInss;
	private String dtValidadeRecFed;
	private String dtValidadeCNDT;
	private String dtValidadeCrc;
	private String dtValidadeRecEst;
	private String dtValidadeRecMun;
	private String indAfe; 
	private String dtValidadeAvsm;
	private String dtValidadeBal;
	
	public String getRazaoSocial() {
		return razaoSocial;
	}
	
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	
	public Long getCgcCnpj() {
		return cgcCnpj;
	}
	
	public void setCgcCnpj(Long cgcCnpj) {
		this.cgcCnpj = cgcCnpj;
	}
	
	public String getDtValidadeFgts() {
		return dtValidadeFgts;
	}
	
	public void setDtValidadeFgts(String dtValidadeFgts) {
		this.dtValidadeFgts = dtValidadeFgts;
	}
	
	public String getDtValidadeInss() {
		return dtValidadeInss;
	}
	
	public void setDtValidadeInss(String dtValidadeInss) {
		this.dtValidadeInss = dtValidadeInss;
	}
	
	public String getDtValidadeRecFed() {
		return dtValidadeRecFed;
	}
	
	public void setDtValidadeRecFed(String dtValidadeRecFed) {
		this.dtValidadeRecFed = dtValidadeRecFed;
	}
	
	public String getDtValidadeCNDT() {
		return dtValidadeCNDT;
	}
	
	public void setDtValidadeCNDT(String dtValidadeCNDT) {
		this.dtValidadeCNDT = dtValidadeCNDT;
	}
	
	public String getDtValidadeCrc() {
		return dtValidadeCrc;
	}
	
	public void setDtValidadeCrc(String dtValidadeCrc) {
		this.dtValidadeCrc = dtValidadeCrc;
	}
	
	public String getDtValidadeRecEst() {
		return dtValidadeRecEst;
	}
	
	public void setDtValidadeRecEst(String dtValidadeRecEst) {
		this.dtValidadeRecEst = dtValidadeRecEst;
	}
	
	public String getDtValidadeRecMun() {
		return dtValidadeRecMun;
	}
	
	public void setDtValidadeRecMun(String dtValidadeRecMun) {
		this.dtValidadeRecMun = dtValidadeRecMun;
	}
	
	public String getIndAfe() {
		return indAfe;
	}
	
	public void setIndAfe(String indAfe) {
		this.indAfe = indAfe;
	}

	public String getDtValidadeAvsm() {
		return dtValidadeAvsm;
	}

	public void setDtValidadeAvsm(String dtValidadeAvsm) {
		this.dtValidadeAvsm = dtValidadeAvsm;
	}

	public String getDtValidadeBal() {
		return dtValidadeBal;
	}

	public void setDtValidadeBal(String dtValidadeBal) {
		this.dtValidadeBal = dtValidadeBal;
	}
	
}
