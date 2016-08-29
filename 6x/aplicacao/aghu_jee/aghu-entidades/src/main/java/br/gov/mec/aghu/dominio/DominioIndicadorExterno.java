package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioIndicadorExterno implements DominioString {
	
	S("S"),
	N("N");
	
	private String value;
	
	private DominioIndicadorExterno(String value){
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Sim";
		case N:
			return "Não";
		default:
			return "";
		}
	}

}
