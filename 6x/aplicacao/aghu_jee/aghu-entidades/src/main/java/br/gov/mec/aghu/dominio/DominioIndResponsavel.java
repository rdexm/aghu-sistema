package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioIndResponsavel implements DominioString {

	R("1");

	private String value;
	
	private DominioIndResponsavel(String value) {
		this.value = value;
	}	
	
	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case R:
			return "Responsável";
		default:
			return "";
		}
	}
}