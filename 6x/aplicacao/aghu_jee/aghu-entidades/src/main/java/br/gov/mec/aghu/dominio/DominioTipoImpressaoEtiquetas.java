package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoImpressaoEtiquetas implements Dominio {
	
	/**
	 * 
	 */
	R, 
	/**
	 * Informado
	 */
	I;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public String getDescricao() {
//		switch (this) {
//		case R:
//			return "Re-Impressão";
//		case I:
//			return "Informado";
//		default:
//			return "";
//		}
//	}
}
