package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioLwsTipoResultado implements DominioString{
	
	QUANTITATIVO("1"),
	QUALITATIVO("2"),;
	
	private String value;

	private DominioLwsTipoResultado(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case QUANTITATIVO:
			return "Quantitativo";
		case QUALITATIVO:
			return "Qualitativo";
		default:
			return "";
		}
	}
}
