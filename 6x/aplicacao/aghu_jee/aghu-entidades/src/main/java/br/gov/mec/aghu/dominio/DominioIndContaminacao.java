package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioIndContaminacao implements DominioString {

	/**
	 * LIMPA
	 */
	L("L"),
	
	/**
	 * POTENCIALMENTE CONTAMINADO 
	 */
	P("P"),
	
	/**
	 * CONTAMINADO
	 */
	C("C"),
	
	/**
	 * INFECTADO 
	 */
	I("I")
	;
	
	private String value;
	
	private DominioIndContaminacao(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case L:
				return "LIMPA";
			case P:
				return "POTENCIALMENTE CONTAMINADO";
			case C:
				return "CONTAMINADO";
			case I:
				return "INFECTADO";
			default:
				return "";
		}
	}

}
