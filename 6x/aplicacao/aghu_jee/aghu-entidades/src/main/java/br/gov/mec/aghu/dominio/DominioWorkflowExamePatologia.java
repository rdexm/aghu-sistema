package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioWorkflowExamePatologia implements Dominio {
	
		
	/**
	 * Área Técnica
	 */
	MC,
	
	/**
	 * Técnica Concluída
	 */
	TC
		
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case MC: return "Área Técnica";
			case TC: return "Técnica Concluída";
			
			default: return "";
		}
	}
	
	public DominioSituacaoExamePatologia getDominioSituacaoExamePatologia(){
		switch (this) {
			case MC: return DominioSituacaoExamePatologia.MC;
			case TC: return DominioSituacaoExamePatologia.TC;
			default: return null;
		}
		
	}
	
}