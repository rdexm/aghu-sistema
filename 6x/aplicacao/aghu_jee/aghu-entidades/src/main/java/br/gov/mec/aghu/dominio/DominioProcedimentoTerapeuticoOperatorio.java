package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;


public enum DominioProcedimentoTerapeuticoOperatorio implements DominioString {
	
	/**
	 * Pré-procedimento
	 */
	PRE("PRE"),

	/**
	 * Trans-procedimento
	 */
	TRA("TRA");
	
	private String value;
	
	private DominioProcedimentoTerapeuticoOperatorio(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}	

	@Override
	public String getDescricao() {
		
		switch (this) {
		case PRE:
			return "Pré-procedimento";
		case TRA:
			return "Trans-procedimento";
		default:
			return "";
		}
	}
}