package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioBoletimAmbulatorio implements Dominio {
	/**
	 * Boletim de Produção Ambualtorial
	 */
	BPA,
	
	/**
	 * Boletim de Produção Individual
	 */
	BPI;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {		
			case BPA: 
				return "Boletim de Produção Ambualtorial";			
			case BPI: 
				return "Boletim de Produção Individual";
			default: 
				return "";
		}
	}
}