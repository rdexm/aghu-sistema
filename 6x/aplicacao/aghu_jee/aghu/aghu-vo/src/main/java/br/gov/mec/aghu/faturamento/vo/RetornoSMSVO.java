package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;

public class RetornoSMSVO implements Serializable {

	private static final long serialVersionUID = -2788029075555655507L;

	private String strCont;
	private String strTipo;
	private String strLaudo;
	private String strDtNas;
	private String strDtInt;
	
	private Date dataNasc;
	private Date dataInt;
	private Long laudo;
	
	private String nomeArquivoImp;
	private Date dataArquivoImp;
	

	public String getStrCont() {
		return strCont;
	}

	public void setStrCont(String strCont) {
		this.strCont = strCont;
	}

	public String getStrTipo() {
		return strTipo;
	}

	public void setStrTipo(String strTipo) {
		this.strTipo = strTipo;
	}

	public String getStrLaudo() {
		return strLaudo;
	}

	public void setStrLaudo(String strLaudo) {
		this.strLaudo = strLaudo;
	}

	public String getStrDtNas() {
		return strDtNas;
	}

	public void setStrDtNas(String strDtNas) {
		this.strDtNas = strDtNas;
	}

	public String getStrDtInt() {
		return strDtInt;
	}

	public void setStrDtInt(String strDtInt) {
		this.strDtInt = strDtInt;
	}

	public Date getDataNasc() {
		return dataNasc;
	}

	public void setDataNasc(Date dataNasc) {
		this.dataNasc = dataNasc;
	}

	public Date getDataInt() {
		return dataInt;
	}

	public void setDataInt(Date dataInt) {
		this.dataInt = dataInt;
	}
	
	public Long getLaudo() {
		return laudo;
	}

	public void setLaudo(Long laudo) {
		this.laudo = laudo;
	}

	public String getNomeArquivoImp() {
		return nomeArquivoImp;
	}

	public void setNomeArquivoImp(String nomeArquivoImp) {
		this.nomeArquivoImp = nomeArquivoImp;
	}

	public Date getDataArquivoImp() {
		return dataArquivoImp;
	}

	public void setDataArquivoImp(Date dataArquivoImp) {
		this.dataArquivoImp = dataArquivoImp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((strCont == null) ? 0 : strCont.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		RetornoSMSVO other = (RetornoSMSVO) obj;
		if (strCont == null) {
			if (other.strCont != null) {
				return false;
			}	
		} else if (!strCont.equals(other.strCont)) {
			return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		return "RetornoSMSVO [strCont=" + strCont + ", strTipo=" + strTipo
				+ ", strLaudo=" + strLaudo + ", strDtNas=" + strDtNas
				+ ", strDtInt=" + strDtInt + "]";
	}
	
	

	

}
