package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioLinguagemImpressora implements Dominio {
	/**
	 * EPL
	 */
	E, 
	/**
	 * ZPL
	 */
	Z;
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {	
		switch (this) {
		case E:
			return "EPL";
		case Z:
			return "ZPL";		
		default:
			return "";
		}
	}

}