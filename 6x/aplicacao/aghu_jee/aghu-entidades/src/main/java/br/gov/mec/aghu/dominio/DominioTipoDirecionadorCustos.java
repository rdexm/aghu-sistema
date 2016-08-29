package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoDirecionadorCustos implements Dominio {

	/**
	 * Recurso
	 */
	RC,
	/**
	 * Atividade
	 */
	AT,
	/**
	 * Rateio para Clientes
	 */
	RT,
	/**
	 * Rateio para Objeto de Custos
	 */
	RO;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case RC:
			return "Recurso";
		case AT:
			return "Atividade";
		case RT:
			return "Rateio para Clientes";
		case RO:
			return "Rateio para Objeto de Custos";
		default:
			return "";
		}
	}
	
	

}
