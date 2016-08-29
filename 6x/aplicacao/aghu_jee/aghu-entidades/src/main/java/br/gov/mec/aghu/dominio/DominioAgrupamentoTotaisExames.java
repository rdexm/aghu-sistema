package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioAgrupamentoTotaisExames implements Dominio{
	/**
	 * Agrupamento Completo
	 */
	C,
	/**
	 * Agrupamento por Data Programada	
	 */
	D,
	/**
	 * Agrupamento por Situação	
	 */
	S,
	/**
	 * Sem agrupamento	
	 */	
	X;

	@Override
	public int getCodigo() {		
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case C:
			return "Completo";
		case D:
			return "Data Programada";
		case S:
			return "Situação";
		case X:
			return "Sem Agrupamento";			
		default:
			return "";
		}
	}	
	
}
