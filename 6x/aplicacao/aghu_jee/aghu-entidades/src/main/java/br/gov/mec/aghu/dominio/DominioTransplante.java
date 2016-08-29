package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTransplante implements Dominio {
	
	/*
	 * Captação
	 */
	C,
	
	/*
	 * Transplante
	 */
	T;

	private static final long serialVersionUID = 2823578623553888204L;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
	switch(this){
		case C:
			return "Captação";
		case T:
			return "Transplante";
		
	}
			
		return null;
	}
	
}