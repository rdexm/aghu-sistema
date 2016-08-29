package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoUsoDispensacao implements Dominio {
	/**
	 * Ambos
	 */
	A,
	
	/**
	 * Estorno
	 */
	E,
	
	/**
	 * Dispensação
	 */
	D;
	
	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this){
		case A:
			return "Ambos";
		case E:
			return "Estorno";
		case D:
			return "Dispensação";
		default:
			return "";
		}
	}

}
