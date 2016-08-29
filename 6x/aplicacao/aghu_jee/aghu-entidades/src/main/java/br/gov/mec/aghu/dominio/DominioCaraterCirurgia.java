package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;


public enum DominioCaraterCirurgia implements DominioString {
	/**
	 * ELETIVA
	 */
	ELT("ELT"),

	/**
	 * URGÊNCIA
	 */
	URG("URG");
	
	private String value;
	
	private DominioCaraterCirurgia(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}	

	@Override
	public String getDescricao() {
		
		switch (this) {
		case ELT:
			return "ELETIVA";
		case URG:
			return "URGÊNCIA";
		default:
			return "";
		}
	}

}
