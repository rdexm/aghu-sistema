package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

public class PropostasVencedorasFornecedorVO implements Serializable {
	
	private static final long serialVersionUID = -7468004122874334159L;
	
	private Integer cdpNumero;
	private String razaoSocial;
	
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setCdpNumero(Integer cdpNumero) {
		this.cdpNumero = cdpNumero;
	}
	public Integer getCdpNumero() {
		return cdpNumero;
	}

}
